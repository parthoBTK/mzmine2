/*
 * Copyright 2006-2009 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.data;

/**
 * This interface represents an identification result.
 */
public interface PeakIdentity {

	public static final PeakIdentity UNKNOWN_IDENTITY = null;

	/**
	 * Returns description of identification method, e.g. which database was
	 * searched.
	 * 
	 * @return Identification method
	 */
	public String getIdentificationMethod();

	/**
	 * Returns ID of identified compound, e.g. ID of this compound in a given
	 * database.
	 * 
	 * @return Compound ID
	 */
	public String getID();

	/**
	 * Returns compound name
	 * 
	 * @return Compound name
	 */
	public String getName();

	/**
	 * Returns compound formula
	 * 
	 * @return Formula
	 */
	public String getCompoundFormula();


	/**
	 * Returns a String with information about this compound.
	 * 
	 * @return String
	 */
	public String toString();

}