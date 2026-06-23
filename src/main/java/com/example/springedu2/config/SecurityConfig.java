package com.example.springedu2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 실수는 설정, 공부 설정 안 함
            .authorizeHttpRequests(auth-> auth
                    .requestMatchers(
                            "/", "/index.html",
                            "/css/**", "/img/**", "/js/**", "/fonts/**",
                            "/login", "/members/register"
                    ).permitAll() // 로그인 없이 사용 가능
                    .requestMatchers("/admin/**","/vupdate", "/vdelete").hasRole("ADMIN")
                    .requestMatchers(
                            "/visitorMain", "/visitorForm.html",
                            "/vlist", "/vinsert", "/vsearch", "/one",
                            "/members/me"
                    ).authenticated() // 로그인이 필요함
                    .anyRequest().authenticated() // 설정하지 않은 다른 요청도 로그인 필요
            )

                //formLogin() 는 사용자가 <form>으로 입력한  username, password를 기반으로 인증 처리
                // 로그인 기초데이터를 미리 db에 만들어 둔다
                // DataInitailizer 클래스를 미리 db에 저장 -> Member table
                .formLogin( form->form
                        .loginPage("/login") // 로그인 페이지 변경
                        // get /login -> PageController 에 /login 주소 이동 -> login.html 로 보낸다
                        // 내가 만든 로그인 화면 사용
                        // 만약 <input name="username" /> -> <input name="loginId" />
                        //      <input name="password" /> -> <input name="loginPwd" />
                        // Security 설정
                        //  .formLogin ( form -> form
                        //      .usernameParameter("loginId")
                        //      .usernameParameter("loginPwd")
                        // )

                        .loginProcessingUrl("/login") // 기본값이 /login 생략 가능
                        // post /login 로그인 처리
                        // Spring Security 가 username, password 읽어서 인증 처리한다 : 자동
                        // UserDetailsService 안의 loadUserByusername() 를 실행해서 db 검색 로그인 처리까지 진행
                        .defaultSuccessUrl("/visitorMain", true)
                        // 로그인 성공하면 "/"나 "/visitorMain"
                        // 비밀번호가 틀리거나 사용자가 없으면
                        // '/login?error' 또는 .failureUrl("/login?error")로 이동해서 thymeleaf 에서 처리
                        // <p><th:if="${param.error}" class="error">
                        // 아이디나 또는 비밀번호가 맞지 않습니다
                        // </p>
                        .permitAll() // 로그인 페이지는 누구나 접근 가능하다
                        // 로그인 화면, 로그인 처리 url, 로그인 실패 url는 인증 없이 접근 가능
                )

                .logout(logout-> logout.logoutUrl("/logout")
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(
                        exception->
                                exception.accessDeniedPage("/access-denied")
                ); // 접근 거부 페이지 처리
        return http.build();
    }

    // 비밀번호를 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
