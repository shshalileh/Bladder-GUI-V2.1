package com.flomio.ndef.helper.exceptions;

public class NdefException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NdefException(String msg) {
		super(msg);
	}

	public NdefException(String msg, Throwable t) {
		super(msg, t);
	}

}
