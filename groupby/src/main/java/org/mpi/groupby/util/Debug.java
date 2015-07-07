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
 * The Class Debug.
 */
public class Debug {

	/** The debug. */
	public static boolean debug = true;

	/** The profile. */
	public static boolean profile = false;

	/**
	 * Log.
	 *
	 * @param module the module
	 * @param level the level
	 * @param format the format
	 * @param args the args
	 */
	public static void log(boolean module, boolean level, String format,
			Object[] args) {
		if (module && level) {
			System.err.printf(format, args);
		}
	}

	/**
	 * Println.
	 *
	 * @param obj the obj
	 */
	public static void println(Object obj) {
		if (debug) {
			System.err.println(obj);
		}
	}

	/**
	 * Println.
	 *
	 * @param str the str
	 */
	public static void println(String str) {
		if (debug) {
			System.err.println(str);
		}
	}

	/**
	 * Printf.
	 *
	 * @param format the format
	 * @param args the args
	 */
	
	public static void printf(String format, Object... args){
    	if (debug){
    		System.err.printf(format, args);
    	}
    }

	/**
	 * Println.
	 *
	 * @param cond the cond
	 * @param st the st
	 */
	public static void println(boolean cond, Object st) {
		if (debug && cond) {
			System.err.println(st);
		}
	}

	/**
	 * Println.
	 */
	public static void println() {
		if (debug) {
			// System.err.println();
		}
	}

	/**
	 * Prints the.
	 *
	 * @param obj the obj
	 */
	public static void print(Object obj) {
		if (debug) {
			System.err.print(obj);
		}
	}

	/**
	 * Kill.
	 *
	 * @param e the e
	 */
	static public void kill(Exception e) {
		e.printStackTrace();
		System.exit(0);
	}

	/**
	 * Kill.
	 *
	 * @param st the st
	 */
	public static void kill(String st) {
		kill(new RuntimeException(st));
	}

	/** The baseline. */
	static protected long baseline = 0;// System.currentTimeMillis() - 1000000;

	/**
	 * Profile start.
	 *
	 * @param s the s
	 */
	public static void profileStart(String s) {
		if (!profile) {
			return;
		}
		String tmp = Thread.currentThread() + " " + s + " START "
				+ (System.currentTimeMillis() - baseline);
		System.err.println(tmp);
	}

	/**
	 * Profile finis.
	 *
	 * @param s the s
	 */
	public static void profileFinis(String s) {
		if (!profile) {
			return;
		}
		String tmp = Thread.currentThread() + " " + s + " FINIS "
				+ (System.currentTimeMillis() - baseline);
		System.err.println(tmp);
	}

}
