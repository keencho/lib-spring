package com.keencho.lib.spring.security.exception;

import com.keencho.lib.spring.common.exception.KcRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class KcAccountDisabledException extends KcRuntimeException {
    public KcAccountDisabledException() {
        super("비활성화된 계정입니다.");
    }
}
