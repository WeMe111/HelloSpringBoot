package com.example.demo.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.domain.User;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행
// 로그인을 진행이 완료가 되면 섹션을 만들어줌
public class PrincipalDetails implements UserDetails {
	
	private User user;
	
	public PrincipalDetails(User user) {
		this.user = user;
	}

	// 해당 User의 권한을 리턴하는 곳
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(new GrantedAuthority() {
			
			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		return collect;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	// 계정이 만료되었는지 (true: 만료되지 않음)
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	
	// 계정이 감겨있는지 
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// 패스워드가 만려되지 않았는지
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	
	//계정이 활성화되어 있는지
	@Override
	public boolean isEnabled() {
		return true;
	}

}
