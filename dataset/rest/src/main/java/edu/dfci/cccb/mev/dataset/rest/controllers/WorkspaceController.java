/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.dfci.cccb.mev.dataset.rest.controllers;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilderException;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDatasetNameException;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDimensionTypeException;
import edu.dfci.cccb.mev.dataset.domain.contract.Workspace;
import edu.dfci.cccb.mev.dataset.rest.assembly.tsv.MultipartTsvInput;

/**
 * @author levk
 * 
 */
@RestController
@Log4j
@ToString (exclude = { "workspace", "builder" })
public class WorkspaceController {


  private @Getter @Setter @Inject Workspace workspace;
  private @Getter @Setter @Inject DatasetBuilder builder;

  @RequestMapping (value = "/dataset", method = GET)
  public List<String> list () {
    return workspace.list ();
  }

  @RequestMapping (value = "/dataset", method = POST, consumes = "multipart/form-data")
  @ResponseStatus (OK)
  public void upload (@RequestParam ("upload") MultipartFile upload) throws DatasetBuilderException,
                                                                    InvalidDatasetNameException,
                                                                    InvalidDimensionTypeException {
    log.warn (new Exception ("STACK TRACE"));
    Dataset dataset = builder.build (new MultipartTsvInput (upload));
    if (log.isDebugEnabled ())
      log.debug ("Uploaded " + dataset);
    workspace.put (dataset);
  }
  
}