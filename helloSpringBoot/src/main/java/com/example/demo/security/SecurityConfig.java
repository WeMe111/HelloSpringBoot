package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final PrincipalDetailService principalDetailService;
	
	@Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(principalDetailService).passwordEncoder(bCryptPasswordEncoder());
    }
	
	@Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                    .authorizeRequests()  //HttpServletRequest 요청 URL에 따라 접근 권한을 설정합니다. 
                    .antMatchers("/index").permitAll()
                    .antMatchers("/auth/user/**").access("hasRole('USER') or hasRole('ADMIN')")
                    .antMatchers("/admin/userList").access("hasRole('ADMIN')")
                    .antMatchers("/file-download/**").permitAll()  //파일 다운로드
        			.antMatchers("/resource/**/images/**").permitAll()  // 이미지
                .and()
                    .formLogin()
                    .loginPage("/auth/login")
                    .loginProcessingUrl("/auth/api/v1/user/login")  //로그인 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행
                    .defaultSuccessUrl("/auth/user/home")
                .and()  //예외처리
                    .exceptionHandling()
                    .accessDeniedHandler(new CustomAccessDeniedHandler());

        
        http  		//자동로그인
        			.rememberMe().tokenValiditySeconds(60 * 60 * 7)
        			.userDetailsService(principalDetailService);
    }
}
