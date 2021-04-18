package com.cos.jwt.auth;

import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// http://localhost:8080/login 요청이 올 때 서비스가 동작
// 스프링 시큐리티가 기본적으로 로그인 요청 주소가 /login이기 때문
// 시큐리티 설정으로 동작하지 않게 했으므로
// PrincipalDetailsService를 부르는 필터를 만들어줘야 함
@Service
@RequiredArgsConstructor
@Log
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("PrincipalDetailsService의 loadUserByUsername()");
        User userEntity = userRepository.findByUsername(username);
        log.info(" ==============================userEntity: " + userEntity);
        return new PrincipalDetails(userEntity);
    }
}
