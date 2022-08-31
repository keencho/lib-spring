package com.keencho.lib.spring.security.config;

import com.keencho.lib.spring.security.provider.KcJwtTokenProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * jwt 토큰을 파싱하여 security account를 얻고 contextholder에 세팅하는 필터
 */
public class KcJwtAuthenticationFilter extends GenericFilterBean {

    private final KcJwtTokenProvider jwtTokenProvider;

    public KcJwtAuthenticationFilter(KcJwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var token = this.jwtTokenProvider.resolveToken((HttpServletRequest) request);

        if (this.jwtTokenProvider.isValidate(token)) {
            var authentication = this.jwtTokenProvider.getAuthentication(token);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
