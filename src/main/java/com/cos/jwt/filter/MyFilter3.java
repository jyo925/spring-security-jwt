package com.cos.jwt.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
//시큐리티가 동작하기 전에 동작함
public class MyFilter3 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        //다운캐스팅
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        //토큰이 넘어오면 인증이 되게 하고 아니면 필터를 계속 타서 인증을 받도록 하기
        //ID, PW가 정상적으로 들어와서 로그인이 완료되면 토큰을 생성해서 토큰을 응답한다.
        //클라이언트 요청할 때 마다 header Authorization value 값으로 토큰을 들고 오면
        //토큰이 내가 만든 토큰이 맞는지 검증만 하면 됨(RSA or HS256을 사용해서...)
        if (req.getMethod().equals("POST")) {
            System.out.println("POST로 요청됨");
            String headerAuth = req.getHeader("Authorization");
            System.out.println(headerAuth);
            System.out.println("필터3");

            if (headerAuth.equals("cos")) {
                System.out.println("인증 OK");
                chain.doFilter(req, res);
            } else {
                PrintWriter out = res.getWriter();
                out.println("인증 안됨");
            }
        }
    }
}
