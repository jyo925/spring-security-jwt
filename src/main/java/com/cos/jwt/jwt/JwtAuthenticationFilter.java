package com.cos.jwt.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에 UsernamePasswordAuthenticationFilter가 있음
// /login으로 요청해서 username, password를 post로 전송하면
// 이 필터가 동작한다.
// 지금 동작하지 않는 이유는 security config 에서 formlogin.disable했기 때문
// 이 필터를 다시 security config에 등록하면 됨
// 로그인 진행 필터이기 때문에 AuthenticationManager 객체를 통해서 진행해야 함
@Log
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    // /login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.info("JwtAuthenticationFilter: 로그인 시도중..................");
//        log.info(request.getParameter("username"));
//        log.info(request.getParameter("password"));

        // 1. username, password를 받아서
        // request.getInputStream() 안에 username, password가 담겨 있음
        // post 요청이라 request.getparameter로 id, pw를 못 꺼낸다고 설명하셧는데.....위 로그를 찍어보면 꺼내지는 거 같음..

        try {
/*            BufferedReader br = request.getReader();
            String input = null;
            while ((input = br.readLine()) != null) {
                log.info(input);
            }
            log.info(request.getInputStream().toString());*/

            //json으로 id, pw 넘기는 경우
            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class);
//            log.info(user.getUsername() +" / " + user.getPassword());

            //토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // 2. 정상인지 로그인 시도를 하기.
            // authenticationManager로 로그인 시도를 하면 PrincipalDetailsService 호출 되며 loadUserByUsername이 자동으로 실행되고 정상이면 authentication이 리턴됨
            // 패스워드는 스프링이 알아서 DB 연동해서 처리해줌(내부 메커니즘은 알 필요 X) -> 인증완료
            // authenticationManager에 토큰을 넣어서 던지면 인증을 해주며 인증이 되면 authentication를 받는다.
            // authentication에는 사용자의 로그인 정보가 담김
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 3. PrincipalDetails를 리턴 받아서 세션에 담고(세션에 담지 않으면 권한 관리가 안됨 admin, manager, user 등)
            // authentication 객체는 session 영역에 저장됨
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            log.info("로그인 완료 username: " + principalDetails.getUser().getUsername()); //값이 있다는 것은 로그인이 정장 처리

            // 4. JWT 토큰을 만들어서 응답해주면 됨
            // 여기서 만들지 않고 attemptAuthentication 메서드가 종료되면 그 이후에 실행되는 함수가 있는데 거기서 처리

            // 리터하는 이유는 권한 관리를 security가 대신 해주기 때문(편의성)
            // 굳이 JWT 토큰을 사용하면서 세션을 만들 이유가 없지만, 권한 처리 때문에 세션에 넣어 주는 것
            return authentication;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //attemptAuthentication 실행 후 인증 정상 처리 되면 실행되는 함수
    //JWT 토큰을 만들어서 request 요청한 사용자에게 JWT 토큰을 response 해주면 됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.info("successfulAuthentication 실행 -> 인증 완료");
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        //JWT 라이브러리를 이용해서 토큰 생성
        //HS256 방식
        String jwtToken = JWT.create()
                .withSubject(principalDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+jwtToken);
    }
}
