/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but useOUT ANY WARRANTY; useout even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along use this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package edu.dfci.cccb.mev.common.domain.guice.jackson;

import com.fasterxml.jackson.databind.AnnotationIntrospector;

/**
 * Binds Jackson introspector
 * 
 * @author levk
 * @since CRYSTAL
 */
public interface JacksonIntrospectorBinder extends JacksonProviderSetBinder<AnnotationIntrospector> {}
