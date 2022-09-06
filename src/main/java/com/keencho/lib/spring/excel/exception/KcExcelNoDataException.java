package com.keencho.lib.spring.excel.exception;


import com.keencho.lib.spring.common.exception.KcRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class KcExcelNoDataException extends KcRuntimeException {
    public KcExcelNoDataException() {
        super("엑셀 파일을 작성할 데이터가 없습니다.");
    }
}
