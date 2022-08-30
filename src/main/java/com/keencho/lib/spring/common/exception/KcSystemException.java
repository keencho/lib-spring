package com.keencho.lib.spring.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class KcSystemException extends KcRuntimeException {
    public KcSystemException() {
        super("시스템 에러가 발생하였습니다.");
    }
}
