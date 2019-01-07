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
package edu.dfci.cccb.mev.dataset.domain.contract;

import java.util.Calendar;

/**
 * @author levk
 * 
 */
public interface Analysis extends Comparable<Analysis> {

  final String MEV_ANALYSIS_STATUS_SUCCESS = "SUCCESS";
  final String MEV_ANALYSIS_STATUS_IN_PROGRESS = "IN_PROGRESS";
  final String MEV_ANALYSIS_STATUS_ERROR = "ERROR";

  final String VALID_ANALYSIS_NAME_REGEX = "[a-zA-Z0-9_\\-\\+\\ \\.]+";

  String name ();
  
  Analysis name(String name);

  String type ();

  Calendar timestamp ();

  String status ();
  
  Analysis status(String status);

  String error ();
  
  Analysis error (String error);

}
