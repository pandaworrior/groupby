/*******************************************************************************
 * Copyright (c) 2015 Dependable Cloud Group and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dependable Cloud Group - initial API and implementation
 *
 * Creator:
 *     Cheng Li
 *
 * Contact:
 *     chengli@mpi-sws.org    
 *******************************************************************************/
package org.mpi.groupby.util;

/**
 * A factory for handling Role objects.
 */
public class RoleFactory {
	
	/** The rootstr. */
	private final static String ROOTSTR = "root node";
	
	/** The internalstr. */
	private final static String INTERNALSTR = "internal node";
	
	/** The leafstr. */
	private final static String LEAFSTR = "leaf node";

	/**
	 * Gets the role string.
	 *
	 * @param _role the _role
	 * @return the role string
	 */
	public static String getRoleString(Role _role){
		switch(_role){
		case ROOT:
			return ROOTSTR;
		case INTERNAL:
			return INTERNALSTR;
		case LEAF:
			return LEAFSTR;
			default:
				throw new RuntimeException("invalid role tag " + _role);
		}
	}
}
