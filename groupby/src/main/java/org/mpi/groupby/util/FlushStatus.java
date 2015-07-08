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
 * The Enum FlushStatus to point out where the data is.
 */
public enum FlushStatus {
	
	/** The not flushed tag, indicates that all data is in memory. */
	NOTFLUSHED, 
	
	/** The on flush list tag, indicates that this node is on the waiting list. */
	ONFLUSHLIST,
	
	/** The flushed. */
	FLUSHED
}
