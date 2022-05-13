package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	 @Bean
	 public BCryptPasswordEncoder encoderpwd() {
		 return new BCryptPasswordEncoder();
	 }
	
	 @Bean
	 @Override
	 public AuthenticationManager authenticationManagerBean() throws Exception {
		 return super.authenticationManagerBean();
	 }
	
	@Override
	public void configure(HttpSecurity http) throws Exception{
		http
			.authorizeRequests()  //권한 
			.antMatchers("/index").authenticated()
			.antMatchers("/file-download/**").permitAll()  //파일 다운로드
			.antMatchers("/resource/**/images/**").permitAll()  // 이미지
			.antMatchers("/user/home", "/board/**").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
			.antMatchers("/admin/userList").access("hasRole('ROLE_ADMIN')")
			.anyRequest().permitAll()
			.and()  
			.formLogin()
			.loginPage("/login")
			.loginProcessingUrl("/login") //로그인 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행
			.defaultSuccessUrl("/user/home")
			.and()
			.logout()
			.logoutUrl("/logout")
			.logoutSuccessUrl("/")
			.invalidateHttpSession(true);
	}

}
