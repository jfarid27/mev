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

package edu.dfci.cccb.mev.dataset.domain;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Dimension
 * 
 * @author levk
 * @since BAYLIE
 */
@XmlRootElement
@XmlAccessorType (NONE)
public interface Dimension <K> extends Iterable<K> {

  /**
   * @return name of this dimension
   */
  @XmlAttribute
  String name ();

  /* (non-Javadoc)
   * @see java.lang.Iterable#iterator() */
  @Override
  @XmlAttribute (name = "keys")
  Iterator<K> iterator ();

  /**
   * @return number of keys
   */
  int size ();

  /**
   * @param index
   * @return key at index specified
   */
  K get (int index);
}