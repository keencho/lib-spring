package com.keencho.lib.spring.excel.exception;

import com.keencho.lib.spring.common.exception.KcRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class KcExcelException extends KcRuntimeException {
    public KcExcelException() {
        super("엑셀 파일 작성중 에러가 발생하였습니다.");
    }

    public KcExcelException(String message) {
        super(message);
    }
}
