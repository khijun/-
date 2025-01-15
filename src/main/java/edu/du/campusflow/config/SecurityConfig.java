package edu.du.campusflow.config;


import edu.du.campusflow.entity.Member;
import edu.du.campusflow.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
//@EnableWebSecurity(debug = true)
@Log4j2
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberService memberService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            log.info("사용자: " + username);
            return toUserDetails(memberService.findByMemberId(Long.parseLong(username)));
        };
    }

    public UserDetails toUserDetails(Member member) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getMemberType().toString().toUpperCase()));
        return User.builder()
                .username(member.getMemberId().toString())
                .password(member.getPassword())
                .authorities(authorities)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/js/**", "/css/**").permitAll() // 정적 리소스 허용
//                .antMatchers("/login").permitAll() // 로그인 페이지 허용
//                .anyRequest().authenticated() // 그 외 요청은 인증 필요
                .anyRequest().permitAll() // 실험코드
                .and()
                .headers()
                .frameOptions().sameOrigin() // X-Frame-Options 설정
                .and()
                .formLogin() // 폼 로그인 활성화
//                .loginPage("/login") // 커스텀 로그인 페이지 설정 (필요 시)
                .defaultSuccessUrl("/") // 로그인 성공 후 이동할 페이지
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
                .and()
                .csrf().disable();  // 임시 코드

//        http
//                .authorizeRequests()
//                .anyRequest().permitAll() // 모든 요청을 허용
//                .and()
//                .csrf().disable(); // CSRF 보호를 비활성화

        return http.build();
    }
}
