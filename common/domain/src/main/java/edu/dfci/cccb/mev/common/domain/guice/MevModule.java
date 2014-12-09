/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package edu.dfci.cccb.mev.common.domain.guice;

import static ch.lambdaj.Lambda.convert;
import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;
import static com.google.inject.Key.get;
import static com.google.inject.internal.Annotations.isBindingAnnotation;
import static com.google.inject.name.Names.bindProperties;
import static java.lang.Short.valueOf;
import static java.lang.System.getenv;
import static java.net.InetSocketAddress.createUnresolved;

import java.lang.annotation.Annotation;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import lombok.ToString;
import lombok.extern.log4j.Log4j;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import ch.lambdaj.function.convert.Converter;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

import edu.dfci.cccb.mev.common.domain.guice.c3p0.PooledDataSourceProvider;
import edu.dfci.cccb.mev.common.domain.guice.jackson.JacksonHandlerInstantiatorBinder;
import edu.dfci.cccb.mev.common.domain.guice.jackson.JacksonInjectableValuesBinder;
import edu.dfci.cccb.mev.common.domain.guice.jackson.JacksonIntrospectorBinder;
import edu.dfci.cccb.mev.common.domain.guice.jackson.JacksonModule;
import edu.dfci.cccb.mev.common.domain.guice.jackson.JacksonSerializerBinder;
import edu.dfci.cccb.mev.common.domain.guice.jaxrs.ExceptionBinder;
import edu.dfci.cccb.mev.common.domain.guice.jaxrs.JaxrsModule;
import edu.dfci.cccb.mev.common.domain.guice.jaxrs.MessageReaderBinder;
import edu.dfci.cccb.mev.common.domain.guice.jaxrs.MessageWriterBinder;
import edu.dfci.cccb.mev.common.domain.guice.rserve.RserveHostConfigurer;
import edu.dfci.cccb.mev.common.domain.guice.rserve.RserveModule;
import edu.dfci.cccb.mev.common.domain.mappers.MevExceptionMapper;
import edu.dfci.cccb.mev.common.domain.messages.JacksonMessageHandler;

/**
 * MeV domain configuration module
 * 
 * @author levk
 * @since CRYSTAL
 */
@ToString
@Log4j
public class MevModule implements Module {
  /**
   * Environment variable listing Rserve hosts; hosts are delimited by a semi
   * (;) each host constisting of hostname or IP separated and port separated by
   * colon (:)
   */
  public static final String RSERVE_HOSTS = "RSERVE_HOSTS";

  /* (non-Javadoc)
   * @see com.google.inject.Module#configure(com.google.inject.Binder) */
  @Override
  public void configure (Binder binder) {
    binder.install (new SingletonModule () {

      @Override
      public void configure (Binder binder) {
        // Persistence
        bindProperties (binder, load ("/META-INF/configuration/persistence.properties"));
        binder.bind (DataSource.class).toProvider (new PooledDataSourceProvider ()).in (Singleton.class);

        // Rserve
        binder.install (new RserveModule () {

          @Override
          public void configure (RserveHostConfigurer configurer) {
            String envHosts = null;
            try {
              envHosts = getenv (RSERVE_HOSTS);
            } catch (SecurityException e) {
              log.warn ("Unable to retreive " + RSERVE_HOSTS + " environment variable due to security"
                        + " manager constraints, falling back to default configuration");
            }
            if (envHosts == null)
              super.configure (configurer);
            else
              for (InetSocketAddress address : convert (envHosts.split (";"),
                                                        new Converter<String, InetSocketAddress> () {
                                                          @Override
                                                          public InetSocketAddress convert (String from) {
                                                            String[] split = from.split (":");
                                                            return createUnresolved (split[0], (int) valueOf (split[1]));
                                                          }
                                                        }))
                configurer.add (address);
          }
        });

        // JAX-RS
        binder.install (new JaxrsModule () {
          private final JacksonMessageHandler handler = new JacksonMessageHandler ();

          @Override
          public void configure (MessageReaderBinder binder) {
            binder.useInstance (handler);
          }

          @Override
          public void configure (MessageWriterBinder binder) {
            binder.useInstance (handler);
          }

          @Override
          public void configure (ExceptionBinder binder) {
            binder.use (MevExceptionMapper.class);
          }
        });

        // Jackson
        binder.install (new JacksonModule () {

          @Override
          public void configure (JacksonInjectableValuesBinder binder) {
            binder.useInstance (new InjectableValues () {
              private @Inject Injector injector;

              @Override
              public Object findInjectableValue (Object valueId,
                                                 DeserializationContext ctxt,
                                                 BeanProperty forProperty,
                                                 Object beanInstance) {
                return injector.getInstance ((Key<?>) valueId);
              }
            });
          }

          @Override
          public void configure (JacksonHandlerInstantiatorBinder binder) {
            binder.useInstance (new HandlerInstantiator () {
              private @Inject Injector injector;

              @Override
              public TypeResolverBuilder<?> typeResolverBuilderInstance (MapperConfig<?> config,
                                                                         Annotated annotated,
                                                                         Class<?> builderClass) {
                return (TypeResolverBuilder<?>) injector.getInstance (builderClass);
              }

              @Override
              public TypeIdResolver typeIdResolverInstance (MapperConfig<?> config,
                                                            Annotated annotated,
                                                            Class<?> resolverClass) {
                return (TypeIdResolver) injector.getInstance (resolverClass);
              }

              @Override
              public JsonSerializer<?> serializerInstance (SerializationConfig config,
                                                           Annotated annotated,
                                                           Class<?> serClass) {
                return (JsonSerializer<?>) injector.getInstance (serClass);
              }

              @Override
              public KeyDeserializer keyDeserializerInstance (DeserializationConfig config,
                                                              Annotated annotated,
                                                              Class<?> keyDeserClass) {
                return (KeyDeserializer) injector.getInstance (keyDeserClass);
              }

              @Override
              public JsonDeserializer<?> deserializerInstance (DeserializationConfig config,
                                                               Annotated annotated,
                                                               Class<?> deserClass) {
                return (JsonDeserializer<?>) injector.getInstance (deserClass);
              }
            });
          }

          @Override
          public void configure (JacksonIntrospectorBinder binder) {
            binder.withInstance (new NopAnnotationIntrospector () {
              private static final long serialVersionUID = 1L;

              @Override
              public Object findInjectableValueId (AnnotatedMember m) {
                if (m.getAnnotation (javax.inject.Inject.class) != null
                    || m.getAnnotation (Inject.class) != null || m.getAnnotation (JacksonInject.class) != null) {
                  for (Annotation annotation : m.annotations ())
                    if (isBindingAnnotation (annotation.annotationType ()))
                      return get (m.getGenericType (), annotation);
                  return get (m.getGenericType ());
                } else
                  return null;
              }
            });
            binder.withInstance (new JacksonAnnotationIntrospector ());
            binder.withInstance (new JaxbAnnotationIntrospector (defaultInstance ()));
          }
        });
      }
    });

    binder.install (new JaxrsModule () {
      @Override
      public void configure (ExceptionBinder binder) {
        MevModule.this.configure (binder);
      }

      @Override
      public void configure (MessageReaderBinder binder) {
        MevModule.this.configure (binder);
      }

      @Override
      public void configure (MessageWriterBinder binder) {
        MevModule.this.configure (binder);
      }
    });

    binder.install (new JacksonModule () {
      @Override
      public void configure (JacksonSerializerBinder binder) {
        MevModule.this.configure (binder);
      }
    });
  }

  /**
   * @see JacksonModule#configure(JacksonSerializerBinder)
   */
  public void configure (JacksonSerializerBinder binder) {}

  /**
   * @see JaxrsModule#configure(MessageReaderBinder)
   */
  public void configure (MessageReaderBinder binder) {}

  /**
   * @see JaxrsModule#configure(MessageWriterBinder)
   */
  public void configure (MessageWriterBinder binder) {}

  /**
   * @see JaxrsModule#configure(ExceptionBinder)
   */
  public void configure (ExceptionBinder binder) {}

  /**
   * @param resources names of classpath resources containing properties
   * @return loaded properties
   */
  public static Properties load (String... resources) {
    RuntimeException toThrow = null;
    Properties properties = new Properties ();
    for (String resource : resources)
      try {
        Configuration configuration = new PropertiesConfiguration (MevModule.class.getResource (resource));
        for (Iterator<String> keys = configuration.getKeys (); keys.hasNext ();) {
          String key = keys.next ();
          properties.setProperty (key, configuration.getString (key));
        }
      } catch (ConfigurationException e) {
        if (toThrow == null)
          toThrow = new RuntimeException (e);
        else
          toThrow.addSuppressed (e);
      }

    if (toThrow != null)
      throw toThrow;

    return properties;
  }
}