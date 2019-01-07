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

import static java.util.Calendar.getInstance;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.dfci.cccb.mev.dataset.domain.contract.Analysis;

/**
 * @author levk
 * 
 */
@EqualsAndHashCode
@ToString
@Accessors (fluent = true)
@SuppressWarnings ("unchecked")
@JsonIgnoreProperties({"timestamp"})
public abstract class AbstractAnalysis <T extends AbstractAnalysis<?>> implements Analysis {

  private @JsonProperty @Getter String name;
  private @JsonProperty @Getter String type;
  private @JsonProperty @Getter @Setter String status = Analysis.MEV_ANALYSIS_STATUS_SUCCESS;
  private @JsonProperty @Getter @Setter String error;
  private @Getter Calendar timestamp = getInstance ();

  public T name (String name) {
    this.name = name;
    return (T) this;
  }

  public T type (String type) {
    this.type = type;
    return (T) this;
  }

  public T status (String status) {
    this.status = status;
    return (T) this;
  }

  public T error (String error) {
    this.error = error;
    return (T) this;
  }
  
  public T timestamp (Locale locale, TimeZone timezone) {
    timestamp = getInstance (timezone, locale);
    return (T) this;
  }

  /* (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object) */
  @Override
  public int compareTo (Analysis o) {
    return timestamp ().compareTo (o.timestamp ());
  }
}
