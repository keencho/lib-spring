package com.keencho.lib.spring.common.config;

import com.keencho.lib.spring.common.exception.KcRuntimeException;
import com.keencho.lib.spring.common.model.KcErrorResponse;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@ControllerAdvice
public abstract class KcGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // https://stackoverflow.com/a/38461711/13160032 여기에 따르면 직접 status 를 관리해줘야 한다. 실제로 해보면 무조건 200이 넘어오기도 하고.
    @ExceptionHandler(KcRuntimeException.class)
    public ResponseEntity<KcErrorResponse> handle(HttpServletRequest request, KcRuntimeException ex) throws IOException {
        // 기본 코드는 500
        var status = HttpStatus.INTERNAL_SERVER_ERROR;

        var responseStatusAnnotation = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatusAnnotation != null) {
            status = responseStatusAnnotation.code();
        }

        return new ResponseEntity<>(new KcErrorResponse(ex.getMessage(), request.getServletPath(), status), status);
    }
}
