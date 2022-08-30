package com.keencho.lib.spring.security.exception;

import com.keencho.lib.spring.common.exception.KcRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class KcAccountLockedException extends KcRuntimeException {
    public KcAccountLockedException() {
        super("잠금조치된 계정입니다.");
    }
}
