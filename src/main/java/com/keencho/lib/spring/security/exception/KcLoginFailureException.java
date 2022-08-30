package com.keencho.lib.spring.security.exception;

import com.keencho.lib.spring.common.exception.KcRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class KcLoginFailureException extends KcRuntimeException {
    private static final String DEFAULT_MESSAGE = "아이디 또는 비밀번호를 확인하세요.";

    public KcLoginFailureException() {
        super(DEFAULT_MESSAGE);
    }

    public KcLoginFailureException(int loginAttemptCount, int maxLoginAttemptCount) {
        super(String.format(DEFAULT_MESSAGE + "(%d / %d)", loginAttemptCount, maxLoginAttemptCount));
    }

    public KcLoginFailureException(String message) {
        super(message);
    }
}
