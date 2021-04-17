package com.cos.jwt.config;


import com.cos.jwt.filter.MyFilter1;
import com.cos.jwt.filter.MyFilter3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity //SecurityConfig 활성화
@RequiredArgsConstructor 
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //시큐리티 필터 체인은 여러개임 BasicAuthenticationFilter 등등
        http.addFilterBefore(new MyFilter3(), BasicAuthenticationFilter.class); //시큐리티 필터 체인에 꼭 걸 필요는 X
        http.csrf().disable();
        //세션 사용X
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(corsFilter) //모든 요청은 이 필터를 타게 되며, 내 서버는 크로스 오리진 정책 안 쓰겠다 -> 필터에 의해 크로스 오리진 요청이 와도 모두 허용됨
                .formLogin().disable() //jwt 서버이기 때문 form 태그 로그인 사용 X
                .httpBasic().disable() //기본적인 http 로그인 방식도 사용 X
                .authorizeRequests()
                .antMatchers("/api/v1/user/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/manager/**")
                .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/admin/**")
                .access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll();

    }
}
