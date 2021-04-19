package com.cos.jwt.filter;

import javax.servlet.*;
import java.io.IOException;

public class MyFilter1 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        System.out.println("I'm filter1");
        chain.doFilter(request, response); //계속 필터를 타도록 체인에 넘겨주기
    }
}
