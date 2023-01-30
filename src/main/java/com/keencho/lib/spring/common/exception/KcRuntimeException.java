package com.keencho.lib.spring.common.exception;

public class KcRuntimeException extends RuntimeException {

    public KcRuntimeException() {
        super();
    }

    public KcRuntimeException(Throwable cause) {
        super(cause);
    }

    public KcRuntimeException(String message) {
        super(message);
    }
}
