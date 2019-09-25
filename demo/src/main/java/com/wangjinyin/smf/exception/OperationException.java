package com.wangjinyin.smf.exception;

/**
 *    自定义异常类
 * @author 汪进银 
 *
 */
public class OperationException  extends Exception{
	
	private static final long serialVersionUID = 2L;

	public OperationException() {
		super();
	}

	public OperationException(String message) {
		super(message);
	}

	public OperationException(Throwable throwable) {
		super(throwable);
	}
	
	public OperationException(String message, Throwable throwable) {
		super(message, throwable);
	}


}
