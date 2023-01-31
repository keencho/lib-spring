package com.keencho.lib.spring.common.exception;

public class KcHttpException extends KcRuntimeException{

    public KcHttpException(String message) {
        super(String.format("Error occurred while requesting http - %s", message));
    }
}
