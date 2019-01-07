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

import static edu.dfci.cccb.mev.dataset.rest.resolvers.AnalysisPathVariableMethodArgumentResolver.ANALYSIS_URL_ELEMENT;
import static edu.dfci.cccb.mev.dataset.rest.resolvers.DatasetPathVariableMethodArgumentResolver.DATASET_URL_ELEMENT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

import javax.inject.Inject;

import edu.dfci.cccb.mev.dataset.domain.contract.AnalysisNotFoundException;
import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import edu.dfci.cccb.mev.dataset.domain.contract.Analysis;

/**
 * @author levk
 * 
 */
@RestController
@RequestMapping ("/dataset/" + DATASET_URL_ELEMENT + "/analysis/" + ANALYSIS_URL_ELEMENT)
@Log4j
@Scope (SCOPE_REQUEST)
public class AnalysisController {

  private @Inject Dataset dataset;
  private @Inject Analysis analysis;

  @RequestMapping (method = GET)
  @ResponseBody
  public Analysis analysis () {
    if (log.isDebugEnabled ())
      log.debug ("Returning analysis " + analysis);
    return analysis;
  }

  @RequestMapping (method = DELETE)
  @ResponseStatus(HttpStatus.OK)
  public void deleteByName (@PathVariable("analysis") String name) throws AnalysisNotFoundException {
    log.info ("Deleting analysis " + name);
    dataset.analyses().remove(name);
  }
}
