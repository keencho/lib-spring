package com.keencho.lib.spring.excel.exception;


import com.keencho.lib.spring.common.exception.KcRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class KcExcelNotEffectiveClassIncludeException extends KcRuntimeException {
    public KcExcelNotEffectiveClassIncludeException() {
        super("유효하지 않은 대상 클래스가 포함되어 있습니다.");
    }
}
