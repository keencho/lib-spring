package com.keencho.lib.spring.security.exception;

import com.keencho.lib.spring.common.exception.KcRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class KcAccountLongTermNotUsedException extends KcRuntimeException {
    public KcAccountLongTermNotUsedException() {
        super("장기 미사용으로 인해 잠금 조치된 계정입니다.");
    }
}
