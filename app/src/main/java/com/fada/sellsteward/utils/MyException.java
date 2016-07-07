package com.fada.sellsteward.utils;

public class MyException extends RuntimeException {

	private static final long serialVersionUID = 1991964385367743107L;

	public MyException() {
		super();
	}

	public MyException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public MyException(String detailMessage) {
		super(detailMessage);
	}

	public MyException(Throwable throwable) {
		super(throwable);
	}

}
