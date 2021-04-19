package com.cos.jwt.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;
import lombok.extern.java.Log;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

// 시큐리티가 filter를 가지고 있는데 그 필터 중에 BasicAuthenticationFilter 라는 것이 있다.
// 권한이나 인증이 필요한 특정 주소를 요청했을 때 위 필터를 무조건 타게 되어 있다.
// 만약에 권한이나 인증이 필요한 주소가 아니라면 이 필터를 안 탐
@Log
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    //인증이나 권한이 필요한 주소요청이 있을 때 이 필터를 타게 됨
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//        super.doFilterInternal(request, response, chain); 지우지 않으면 응답을 두번하기 때문에 오류 발생!!!!!!!!!!!
        log.info("인증이나 권한이 필요한 주소가 요청됨");

        String jwtHeader = request.getHeader("Authorization");
        log.info("jwtHeader:  " + jwtHeader);

        // header가 있는지 확인
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            log.info("인증되지 않음");
            chain.doFilter(request, response); //다시 필터를 타게
            return;
        }
        //JWT 토큰을 검증해서 정상적인 사용자인지 확인하자
        String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");

        //서명이 되면 username 가져오기
        String username =
                JWT.require(Algorithm.HMAC512("붕어빵")).build()
                        .verify(jwtToken).getClaim("username").asString();
        //서명이 정상적으로 되면 repository에 연결
        if (username != null) {
            User userEntity = userRepository.findByUsername(username);

            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

            //JwtAuthentication 클래스처럼 만들 수도 있지만
            //여기서는 Authentication을 강제로 만들거임
            //패스워드는 null, 서비스를 통해서 로그인을 진행하는 것이 아닌 임의로 객체를 만들기 때문(위에서 정상유저인지 확인했으므로)
            //즉, JWT 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 생성해준다.
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            //시큐리티를 저장할 수 있는 세션 공간을 찾아서 authentication 객체를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("JWT 토큰 인증 완료");
            chain.doFilter(request, response);
        }

    }
}
