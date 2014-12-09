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

package edu.dfci.cccb.mev.dataset.domain.jackson;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import java.util.Map;

import javax.inject.Singleton;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;

import edu.dfci.cccb.mev.common.domain.guice.jackson.annotation.Handling;
import edu.dfci.cccb.mev.dataset.domain.AnalysisInvocationException;
import edu.dfci.cccb.mev.dataset.domain.Dataset;
import edu.dfci.cccb.mev.dataset.domain.Dimension;
import edu.dfci.cccb.mev.dataset.domain.annotation.Analysis;
import edu.dfci.cccb.mev.dataset.domain.annotation.NameOf;
import edu.dfci.cccb.mev.dataset.domain.guice.AnalysisTypeRegistrar;
import edu.dfci.cccb.mev.dataset.domain.guice.DatasetModule;
import edu.dfci.cccb.mev.dataset.domain.prototype.AnalysisAdapter;
import edu.dfci.cccb.mev.dataset.domain.prototype.DatasetAdapter;

/**
 * @author levk
 */
public class AnalysisTypeResolverTest {

  private Injector injector;

  @Analysis ("mockType")
  @XmlRootElement
  @ToString
  @Accessors (fluent = true)
  @EqualsAndHashCode (callSuper = false)
  public static class MockAnalysys extends AnalysisAdapter {
    public @Getter @Setter @XmlElement String hello = "world";

    public void run () throws AnalysisInvocationException {}
  }

  @Before
  public void setUp () throws Exception {
    injector = Guice.createInjector (new DatasetModule () {
      public void configure (AnalysisTypeRegistrar registrar) {
        registrar.register (MockAnalysys.class);
      }
    }, new Module () {
      @Provides
      @Singleton
      @NameOf (Dataset.class)
      public String dataset () {
        return "mock";
      }

      @Provides
      @Singleton
      @NameOf (edu.dfci.cccb.mev.dataset.domain.Analysis.class)
      public String analysis () {
        return "mock";
      }

      @Provides
      @Singleton
      @NameOf (Dimension.class)
      public String dimension () {
        return "mock";
      }

      @Provides
      @Singleton
      public Map<String, Dataset<String, Double>> wokrspace () {
        return DatasetAdapter.workspace ();
      }

      public void configure (Binder binder) {}
    });
  }

  @Test
  public void serialize () throws Exception {
    assertEquals ("{type:mockType,name:mock,hello:world}",
                  injector.getInstance (ObjectMapper.class).writeValueAsString (new MockAnalysys ().name ("mock")),
                  true);
  }

  @Test
  public void deserialize () throws Exception {
    edu.dfci.cccb.mev.dataset.domain.Analysis actual;
    actual = injector.getInstance (Key.get (ObjectMapper.class, Handling.Factory.APPLICATION_JSON))
                     .readValue ("{\"type\":\"mockType\",\"name\":\"mock\",\"hello\":\"world\"}",
                                 edu.dfci.cccb.mev.dataset.domain.Analysis.class);
    assertThat (actual, is ((edu.dfci.cccb.mev.dataset.domain.Analysis) new MockAnalysys ().name ("mock")));
  }
}