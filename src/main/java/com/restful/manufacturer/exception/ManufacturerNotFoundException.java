package com.restful.manufacturer.exception;

public class ManufacturerNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ManufacturerNotFoundException(String message) {
        super(message);
    }
}