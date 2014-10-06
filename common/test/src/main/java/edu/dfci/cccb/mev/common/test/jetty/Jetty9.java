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

package edu.dfci.cccb.mev.common.test.jetty;

import static com.google.inject.Guice.createInjector;
import static java.net.CookieHandler.setDefault;
import static java.net.CookiePolicy.ACCEPT_ALL;
import static java.util.Arrays.asList;
import static java.util.EnumSet.allOf;
import static org.apache.log4j.lf5.util.StreamUtils.getBytes;
import static org.h2.util.IOUtils.copy;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContextListener;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Jetty9 wrapper
 * 
 * @author levk
 */
public class Jetty9 implements AutoCloseable {

  static {
    setDefault (new CookieManager (null, ACCEPT_ALL));
  }

  private Server server;

  public Jetty9 () throws Exception {
    this (new WebAppContext () {
      {
        setConfigurations (new Configuration[] { new Servlet3Jetty9AnnotationConfiguration () });
      }
    });
  }

  public Jetty9 (Handler handler) throws Exception {
    server = new Server (0);
    server.setHandler (handler);
    server.start ();
  }

  public Jetty9 (Module... modules) throws Exception {
    this (asList (modules));
  }

  public Jetty9 (final Iterable<Module> modules) throws Exception {
    this (new ServletContextHandler () {
      {
        ServletContextListener context = new GuiceServletContextListener () {

          @Override
          protected Injector getInjector () {
            return createInjector (modules);
          }
        };

        addEventListener (context);
        addFilter (GuiceFilter.class, "/*", allOf (DispatcherType.class));
        addServlet (DefaultServlet.class, "/");
        setSessionHandler (new SessionHandler ());
      }
    });
  }

  public int port () {
    return ((ServerConnector) server.getConnectors ()[0]).getLocalPort ();
  }

  /* (non-Javadoc)
   * @see java.lang.AutoCloseable#close() */
  @Override
  public void close () throws Exception {
    server.stop ();
    server.join ();
  }

  public HttpURLConnection connect (String uri) throws MalformedURLException, IOException {
    return (HttpURLConnection) new URL ("http://localhost:" + port () + uri).openConnection ();
  }

  public int rc (String uri) throws MalformedURLException, IOException {
    return connect (uri).getResponseCode ();
  }

  public String get (String uri) throws MalformedURLException, IOException {
    return new String (getBytes (connect (uri).getInputStream ()));
  }

  public String post (String uri, InputStream data) throws MalformedURLException, IOException {
    HttpURLConnection connection = connect (uri);
    connection.setRequestMethod ("POST");
    connection.setDoOutput (true);
    copy (data, connection.getOutputStream ());
    return new String (getBytes (connection.getInputStream ()));
  }

  public String put (String uri, InputStream data) throws MalformedURLException, IOException {
    HttpURLConnection connection = connect (uri);
    connection.setRequestMethod ("PUT");
    connection.setDoOutput (true);
    copy (data, connection.getOutputStream ());
    return new String (getBytes (connection.getInputStream ()));
  }
}
