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

package edu.dfci.cccb.mev.googleplus.domain.support.jackson;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.api.services.drive.model.File.Labels;

public class LabelsSerializerTest extends JsonSerializerTest {

  private Labels labels;

  public LabelsSerializerTest () {
    super (Labels.class, LabelsSerializer.class);
  }

  @Before
  public void setUpLabels () throws Exception {
    labels = new Labels ();
    labels.set ("mockVisible", Boolean.TRUE);
    labels.set ("mockHidden", Boolean.FALSE);
  }

  @Test
  public void labels () throws Exception {
    assertEquals ("[mockVisible]", mapper.writeValueAsString (labels), true);
  }
}