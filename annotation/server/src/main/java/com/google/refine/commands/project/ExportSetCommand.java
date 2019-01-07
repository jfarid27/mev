/*

Copyright 2010, Google Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

 * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above
copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the
distribution.
 * Neither the name of Google Inc. nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,           
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY           
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

package com.google.refine.commands.project;

import static com.google.refine.io.FileProjectManager.REQUEST_ATTEIBUTE_DATASET;
import static com.google.refine.io.FileProjectManager.REQUEST_ATTEIBUTE_DIMENSION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.refine.ProjectManager;
import com.google.refine.ProjectMetadata;
import com.google.refine.browsing.Engine;
import com.google.refine.browsing.FilteredRows;
import com.google.refine.browsing.RowVisitor;
import com.google.refine.commands.Command;
import com.google.refine.model.Column;
import com.google.refine.model.Project;
import com.google.refine.model.Row;

import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension;
import edu.dfci.cccb.mev.dataset.domain.contract.Selections;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleSelection;

public class ExportSetCommand extends Command {

  static public Properties getRequestParameters (HttpServletRequest request) {
    Properties options = new Properties ();

    Enumeration<String> en = request.getParameterNames ();
    while (en.hasMoreElements ()) {
      String name = en.nextElement ();
      options.put (name, request.getParameter (name));
    }
    return options;
  }

  @Override
  public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    logger.info (String.format ("******************* EXPORT SET: %s *******************",
                                request.getParameter ("set-name")));
    ProjectManager.getSingleton ().setBusy (true);
    try {

      /* try { String name = request.getParameter("name"); ProjectMetadata pm =
       * getProjectMetadata(request); pm.setName(name); respond(response,
       * "{ \"code\" : \"ok\" }"); } catch (Exception e) {
       * respondException(response, e); } */

      Project project = getProject (request);

      Engine engine = getEngine (request, project);
      final Dataset heatmap = (Dataset) request.getAttribute (REQUEST_ATTEIBUTE_DATASET);
      Dimension.Type dimensionType = Dimension.Type.from ((String)request.getAttribute (REQUEST_ATTEIBUTE_DIMENSION));
      final Selections selections = heatmap.dimension (dimensionType).selections ();
            final String setName = request.getParameter ("selectionName");
      final Properties properties = new Properties ();
            properties.put ("selectionDescription", request.getParameter ("selectionDescription"));
            properties.put ("selectionColor", request.getParameter ("selectionColor"));
            properties.put ("selectionFacetLink", request.getParameter ("selectionFacetLink"));
      final List<String> keys = new ArrayList<String> ();

      RowVisitor visitor = new RowVisitor () {
        Column theIdColumn;

        @Override
        public void start (Project project) {
          theIdColumn = project.getKeyColumn("annotationId", "id");
        }


        @Override
        public boolean visit (Project project, int rowIndex, Row row) {
          String cellData = row.getCell (theIdColumn.getCellIndex ()).value.toString ();
          if (cellData != null) {
            keys.add (cellData);
          }
          return false;
        }

        @Override
        public void end (Project project) {
          selections.put (new SimpleSelection (setName, properties, keys));
        }

        @Override
        public boolean pass (Project project, int rowIndex, Row row) {
          // TODO Auto-generated method stub
          return false;
        }
      };

      FilteredRows filteredRows = engine.getAllFilteredRows ();
      filteredRows.accept (project, visitor);
      ProjectManager.getSingleton ().save (true);
      respond (response, "{ \"code\" : \"ok\" }");

    } catch (Exception e) {
      // Use generic error handling rather than our JSON handling
      // throw new ServletException(e);
      respondException (response, e);
    } finally {
      ProjectManager.getSingleton ().setBusy (false);
    }
  }
}
