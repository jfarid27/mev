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
package edu.dfci.cccb.mev.dataset.domain.prototype;

import static lombok.AccessLevel.PROTECTED;

import javax.inject.Inject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j;
import edu.dfci.cccb.mev.dataset.domain.contract.Analysis;
import edu.dfci.cccb.mev.dataset.domain.contract.AnalysisBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetException;

/**
 * @author levk
 * 
 */
@RequiredArgsConstructor (access = PROTECTED)
@ToString
@EqualsAndHashCode
@Accessors (fluent = true, chain = true)
@SuppressWarnings ("unchecked")
@Log4j
public abstract class AbstractAnalysisBuilder <B extends AnalysisBuilder<?, ?>, A extends Analysis> implements AnalysisBuilder<B, A> {

  private @Getter final String type;
  private @Getter (PROTECTED) String name;
  private @Getter (PROTECTED) Dataset dataset;

  /* (non-Javadoc)
   * @see
   * edu.dfci.cccb.mev.dataset.domain.contract.AnalysisBuilder#name(java.lang
   * .String) */
  @Override
  public B name (String name) {
    this.name = name;
    return (B) this;
  }

  /* (non-Javadoc)
   * @see
   * edu.dfci.cccb.mev.dataset.domain.contract.AnalysisBuilder#dataset(edu.
   * dfci.cccb.mev.dataset.domain.contract.Dataset) */
  @Override
  @Inject
  public B dataset (Dataset dataset) {
    if (log.isDebugEnabled ())
      log.debug ("Setting dataset to " + dataset);
    this.dataset = dataset;
    return (B) this;
  }
  
  public static class AnalysisStatus extends AbstractAnalysis<AnalysisStatus> implements Analysis{}
  
  @Override  
  public AnalysisStatus buildAsync(){
    
    final AnalysisStatus status = new AnalysisStatus ().type (type()).name (name()).status (Analysis.MEV_ANALYSIS_STATUS_IN_PROGRESS);
    
    new Thread (new Runnable() {           
      @Override      
      public void run () {
          try {
            Analysis result = build();
            if(result.name() == null)
              result.name(name());
            dataset().analyses ().complete (result);
            log.info(String.format("Analysis %s of type %s completed wit status %s.", name(), type(), result.status ()));
          } catch (Throwable e) {                    
            status.status (Analysis.MEV_ANALYSIS_STATUS_ERROR).error (new DatasetException(String.format("Error running analysis '%s' of type %s: %s", type, name, e.getMessage ()), e).toString ());
            log.error(String.format("ERROR in %s analysis %s.", type, name), e);
          }
      }
    }).start ();
    
    dataset().analyses().put(status);
    return status;
  }
  
}
