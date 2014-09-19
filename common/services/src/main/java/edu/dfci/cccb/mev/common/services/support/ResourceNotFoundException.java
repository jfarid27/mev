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

package edu.dfci.cccb.mev.common.services.support;

import edu.dfci.cccb.mev.common.domain.MevException;

/**
 * Exception indicating the requested resource does not exist
 * 
 * @author levk
 * @since CRYSTAL
 */
public class ResourceNotFoundException extends MevException {
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public ResourceNotFoundException () {}

  /**
   * @param message
   */
  public ResourceNotFoundException (String message) {
    super (message);
  }

  /**
   * @param cause
   */
  public ResourceNotFoundException (Throwable cause) {
    super (cause);
  }

  /**
   * @param message
   * @param cause
   */
  public ResourceNotFoundException (String message, Throwable cause) {
    super (message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public ResourceNotFoundException (String message,
                                    Throwable cause,
                                    boolean enableSuppression,
                                    boolean writableStackTrace) {
    super (message, cause, enableSuppression, writableStackTrace);
  }

  public ResourceNotFoundException uri (String uri) {
    return property ("uri", uri);
  }
}
