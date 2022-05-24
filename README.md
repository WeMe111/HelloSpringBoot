# 목차
[프로젝트 생성](#프로젝트-생성)  
[DB 설정](#DB-설정)  
[시큐리티를 이용한 회원가입, 로그인](#시큐리티를-이용한-회원가입,-로그인)  
[회원목록](#회원목록)  
[게시판 CRUD](#게시판-CRUD)    
[조회수, 페이징과 검색](#조회수,-페이징과-검색)  
[댓글](#댓글)  
[자동로그인](#자동로그인)  
[예외처리](#예외처리)  
[파일 업로드/ 다운로드](#파일-업로드/-다운로드)  
[프로젝트 진행과정에서 궁금했던 점](#프로젝트-진행과정에서-궁금했던-점)  
[MariaDB 계정 생성 및 권한부여](MariaDB-계정-생성-및-권한부여)
[후기](#후기)  
# 구현
1. SpringSicurity를 이용한 로그인, 회원가입  
2. 로그아웃  
3. 자동로그인  
4. 예외처리(ADMIN권한자만 들어갈 수 있는 회원목록)  
5. 글쓰기, 수정, 삭제(게시글은 없어지고 DB에는 데이터가 남는다.)  
6. 댓글/ 조회수 기능  
7. 검색 기능(대소문자 구분없이 검색 가능)  
8. 쿠키나 세션으로 조회수 중복방지  
# 추가기능 할 것
1. 파일 업로드/ 다운로드
2. 예외처리 좀 더 공부
3. 이메일 인증/ 아이디 중복 방지
4. 네이버 & 카카오톡 로그인 
5. 아이디/ 패스워드 찾기 

# 프로젝트 생성
<details>   
<summary>접기/펼치기</summary>  

![프로젝트 생성1](https://user-images.githubusercontent.com/94879395/165679231-659fa912-256e-4feb-8445-a8ba387edee7.PNG)  
![image](https://user-images.githubusercontent.com/94879395/165679347-25ca6249-873a-4b38-88fd-2779bfbba8ff.png)  
</details>  

# DB 설정  
<details>   
<summary>접기/펼치기</summary>  
	
**build.gradle**  
```Java
dependencies {
	runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
}
```

의존성 추가 
**application.yml**  
```Java
spring:
  datasource:
    driver-class-name: org.mariadb.jdbc.Driver
    url: jdbc:mariadb://localhost:3306/hellospringboot?serverTimezone=Asia/Seoul
    username: aplm12
    password: 123123

  jpa:
    open-in-view: true
    hibernate:
      ddl-auto: create
      use-new-id-generate-mappings: false
    show-sql: true
    properties:
      hibernate.format_sql: true
```
**open-in-view** : [참고](https://gracelove91.tistory.com/100)  
**ddl-auto** : 프로젝트 실행 시에 자동으로 DDL(create, drop, alter 등)을 생성할 것인지를 결정하는 설정입니다. 주로 create와 update를 사용하는데 create는 프로젝트 실행 시 매번 테이블을 생성해주고, update는 변경이 필요한 경우 테이블을 수정해줍니다.  
**use-new-id-generate-mappings** : JPA의 기본 numbering(넘버링) 전략을 사용할 것인지에 대한 설정입니다. 저는 Entity 클래스에서 따로 설정해줄 것이기 때문에 false로 했습니다.  
**show-sql** : 프로젝트 실행 시 sql문을 로그로 보여줍니다.  
**hibernate.format_sql** : sql을 포맷팅해서 좀 더 예쁘게 sql문을 로그로 보여줍니다.  
	
</details>  
 
# 시큐리티를 이용한 회원가입, 로그인
## User 테이블 생성
<details> 
<summary>접기/펼치기</summary> 
	
**User.class**
```Java
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //sequence, auto_increment

    @Column(nullable = false, length = 20, unique = true)
    private String username; //아이디

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 20)
    private String nickname; //닉네임

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    //비밀번호 암호화 메소드
    public void setPassword(String password) {
        this.password = password;
    }
    
    //권한 메소드
    public String getRoleKey() {
        return this.role.getKey();
    }
    
    //회원수정 메소드
    public void update(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }
    
}

}
```
	
**@NoArgsConstructor** : Lombok 어노테이션으로 빈 생성자를 만들어줍니다.  
**@AllArgsConstructor** : 모든 필드 값을 파라미터로 받는 생성자를 만듦  
**Builder**를 사용하는 이유는 어느 필드에 어떤 값을 채워야하는지 명확하게 알 수 있기 때문에 실수가 나지 않습니다.
[참고](https://www.daleseo.com/lombok-useful-annotations/)  
**@Entity** : 해당 클래스가 엔티티를 위한 클래스이며, 해당 클래스의 인스턴스들이 JPA로 관리되는 엔티티 객체라는 것을 의미합니다. 즉, 테이블을 의미합니다.   
디폴트값으로 클래스의 카멜케이스 이름을 언더스코어 네이밍(_)으로 테이블 이름을 매칭합니다.   
**@Id** : 테이블의 Primary Key(PK)  
**@GeneratedValue(strategy = GenerationType.IDENTITY)** : PK를 자동으로 생성하고자 할 때 사용합니다. 즉, auto_increment를 말합니다. 여기서는 JPA의 넘버링 전략이 아닌 이 전략을 사용합니다. (전에 application.yml 설정에서 use-new-id-generate-mappings: false로 한 것이 있습니다.)  
오라클, mysql등 사용법이 다르고 mysql,mariaDB는 IDENTITY를 사용 하고 Auto는 자동으로 db랑 비교해서 넣어준다.  
**@Column** : 해당 필드가 컬럼이라는 것을 말하고, @Column에는 다양한 속성을 지정할 수 있습니다. (nullable = false: null값이 되면 안된다!, length = 50: 길이 제한 등등)
**@Enumerated(EnumType.STRING)** : JPA로 DB에 저장할 때 Enum 값을 어떤 형태로 저장할지를 결정합니다.  
기본적으로는 int로 저장하지만 int로 저장하면 무슨 의미인지 알 수가 없기 때문에 문자열로 저장될 수 있도록 설정합니다.  
User 클래스 Setter가 없는 이유는 이 setter를 무작정 생성하게 되면 해당 클래스의 인스턴스가 언제 어디서 변해야하는지 코드상으로는 명확하게 알 수가 없어 나중에는 변경시에 매우 복잡해집니다.   
	
```Java
 //비밀번호 암호화 메소드
    public void setPassword(String password) {
        this.password = password;
    }
    
    //권한 메소드
    public String getRoleKey() {
        return this.role.getKey();
    }
    
    //회원수정 메소드
    public void update(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }
```
**더티 체킹(Dirty Checking)이란?**: [참고](https://interconnection.tistory.com/121)  
	
```Java
@Getter
@RequiredArgsConstructor
public enum Role {

    USER("ROLE_USER", "사용자"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
    
}
```
스프링 시큐리티에서는 권한 코드에 항상 ROLE_이 앞에 있어야 합니다. 따라서 키 값을 ROLE_USER, ROLE_ADMIN로 지정했습니다.  
	
</details>  


## Security 회원가입    
<details>  
<summary>접기/펼치기</summary>  
	  
**signup.html**
```Java
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<meta name="_csrf" th:content="${_csrf.token}">
<meta name="_csrf_header" th:content="${_csrf.headerName}">
<head th:replace="layout/header :: header" />
<body>
	<div class="container">
		<div th:replace="layout/bodyHeader :: bodyHeader" />
        <form id="needs-validation" novalidate>
            <h1 class="h3 m-3 fw-normal ">회원가입</h1>
            <div class="form-floating m-3">
            <label for="username">아이디</label>
                <input type="text" class="form-control" id="username" placeholder="아이디를 입력하세요." required
                       minlength="2" size="20">
                <div class="valid-feedback">
                    good!
                </div>
                <div class="invalid-feedback">
                    아이디는 4자 이상 입력해야 합니다.
                </div>
            </div>
            <div class="form-floating m-3">
            <label for="password">패스워드</label>
                <input type="password" class="form-control" id="password" placeholder="패스워드를 입력하세요." required
                       minlength="3" size="20">
                <div class="valid-feedback">
                    very good!
                </div>
                <div class="invalid-feedback">
                    패스워드는 8자 이상 입력해야 합니다.
                </div>
            </div>
            <div class="form-floating m-3">
            <label for="email">이메일</label>
                <input type="email" class="form-control" id="email" placeholder="이메일을 입력하세요." required>
                <div class="valid-feedback">
                    nice!
                </div>
                <div class="invalid-feedback">
                    이메일 형식으로 입력해야 합니다.
                </div>
            </div>
            <div class="form-floating m-3">
            <label for="nickname">닉네임</label>
                <input type="text" class="form-control" id="nickname" placeholder="닉네임을 입력하세요." required
                       minlength="4" size="20">
                <div class="valid-feedback">
                    very nice!
                </div>
                <div class="invalid-feedback">
                    닉네임은 4자 이상 입력해야 합니다.
                </div>
            </div>
        </form>
        <button class="w-100 btn btn-lg btn-primary" id="btn-save">회원가입</button>
		<br />
		<div th:replace="layout/footer :: footer" />
	</div>
<script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>
<script th:src="@{/js/validation.js}"></script>
<script th:src="@{/js/user.js}"></script>
</body>
</html>
```  
```
<form **th:action**="@{/signupJoin}" method="POST">
```
: th:action을 사용하면 csrf토큰이 자동으로 추가된다. 
```
<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />	
```
: 이런 식으로 넣어도 작동 한다.
	
**user.js**  
```Java
'use strict';

let index = {
    init: function () {
        $("#btn-save").on("click", () => { //this를 바인딩하기 위해 화샬표 함수 사용
            let form = document.querySelector("#needs-validation");
            if (form.checkValidity() == false) {
                console.log("회원가입 안됨")
            } else {
                this.save();
            }
        });
        $("#btn-update").on("click", () => {
            let form = document.querySelector("#needs-validation");
            if (form.checkValidity() == false) {
                console.log("회원수정 안됨")
            } else {
                this.update();
            }
        });
    },

    save: function () {
        let data = { 
            username: $("#username").val(),
            password: $("#password").val(),
            email: $("#email").val(),
            nickname: $("#nickname").val()
        }

        $.ajax({
            type: "POST", //Http method
            url: "/auth/api/v1/user", //API 주소
            data: JSON.stringify(data), //JSON으로 변환
            contentType: "application/json; charset=utf-8", //MIME 타입
            dataType: "json" //응답 데이터
        }).done(function (res) {
            alert("회원가입이 완료되었습니다.");
            location.href = "/auth/login";
        }).fail(function (err) {
            alert(JSON.stringify(err));
        });
    },

    update: function () {
        let data = {
            id: $("#id").val(),
            password: $("#password").val(),
            nickname: $("#nickname").val()
        }

        $.ajax({
            type: "PUT",
            url: "/api/v1/user",
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (res) {
            alert("회원수정이 완료되었습니다.");
            location.href = "/";
        }).fail(function (err) {
            alert(JSON.stringify(err));
        });
    }
}
index.init();

var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
});
```
	
**validation.js**    

```Java
'use strict';

(function () {
    window.addEventListener("load", function () {
        let form = this.document.querySelector("#needs-validation");
        let btnSave = this.document.querySelector("#btn-save");

        btnSave.addEventListener("click", function (event) {
            if (form.checkValidity() == false) {
                event.preventDefault();
                event.stopPropagation();
                form.classList.add("was-validated");
            }
        }, false);
    }, false);
})();

var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
});
```

**UserRepository** 
```Java
public interface UserRepository extends JpaRepository<User, Long> {

	// findBy 규칙 -> UserId 문법
	// ex) select * from user where userId = ?
	Optional<User> findByUsername(String username);
	
}
```
CRUD 함수를 JPARepository가 들고 있고 @Repository라는 어노테이션이 없어도 loc됩니다. 이유는 JpaRepositori를 상속했기 때문에..  
**Optional**: Java8에서는 Optional<T> 클래스를 사용해 NPE를 방지할 수 있도록 도와준다. Optional<T>는 null이 올 수 있는 값을 감싸는 Wrapper 클래스로, 참조하더라도 NPE가 발생하지 않도록 도와준다. Optional 클래스는 아래와 같은 value에 값을 저장하기 때문에 값이 null이더라도 바로 NPE가 발생하지 않으며, 클래스이기 때문에 각종 메소드를 제공해준다.
[참고](https://mangkyu.tistory.com/70)   
	
**UserController.class**
```Java
@Controller
public class UserController {
	
	@GetMapping("/")
	public String index() {
		return "index";
	}

	//회원가입 페이지 이동
	@GetMapping("/auth/signup")
    public String userSave() {
        return "signup";
    }
	
	//로그인 페이지 이동
	@GetMapping("/auth/login")
    public String userLogin() {
        return "login";
    }
	
	//메인페이지
	@GetMapping("/auth/user/home")
	public String home() {
		return "layout/user/home";
	}
	
	//회원수정 페이지 이동
    @GetMapping("/auth/user/update")
    public String userUpdate() {
        return "layout/user/update";
    }
	
}
```
	
**UserSaveRequestDto.class**
```Java
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class UserSaveRequestDto {

	private String username;
    private String password;
    private String email;
    private String nickname;
    private Role role;

    public User toEntity() {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .nickname(nickname)
                .role(Role.USER)
                .build();
    }
    
}
```
Entity 클래스는 DB와 매우 밀접한 관계이기 때문에 Request/Response할 때는 따로 Dto 클래스를 만들어주는 것이 좋습니다.  
	
**UserService.class**
```Java
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //회원가입 로직
    @Transactional
    public Long save(User user) {
        String hashPw = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(hashPw);
        
        return userRepository.save(user).getId();
    }
    
}
```
**@RequiredArgsConstructor**: private final UserRepository userRepository : 생성자 주입을 받기 위해 @RequiredArgsConstructor 어노테이션을 썼습니다.  
**@Transactional**: 로직이 실행하면서 예외가 생기면 자동으로 롤백해준다.
[참고](https://velog.io/@kdhyo/JavaTransactional-Annotation-%EC%95%8C%EA%B3%A0-%EC%93%B0%EC%9E%90-26her30h)  
	

**UserApiController.class**

```Java
@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    //회원가입 API
    @PostMapping("/auth/api/v1/user")
    public Long save(@RequestBody UserSaveRequestDto userSaveRequestDto) {
        return userService.save(userSaveRequestDto.toEntity());
    }
    
}
```

**@RestController**: @Controller에 @ResponseBody가 추가된 것입니다. 당연하게도 RestController의 주용도는 Json 형태로 객체 데이터를 반환하는 것입니다. 최근에 데이터를 응답으로 제공하는 REST API를 개발할 때 주로 사용하며 객체를 ResponseEntity로 감싸서 반환합니다. 이러한 이유로 동작 과정 역시 @Controller에 @ReponseBody를 붙인 것과 완벽히 동일합니다.
[참고](https://mangkyu.tistory.com/49)
	

**SecurityConfig**   

```Java
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
```
  
**@EnableWebSecurity**: 스프링 시큐리티 설정들을 활성화시킵니다.  
**@Configuration**: 설정파일을 만들기 위한 애노테이션 or Bean을 등록하기 위한 애노테이션   
**authorizeRequests()** : URL별 권환 관리를 설정하는 옵션  
**antMatchers()** : 권한 관리 대상을 지정하는 옵션  
**authenticated()** : 메소드는 애플리케이션에 로그인된 사용자가 요청을 수행할 떄 필요하다. 만약 사용자가 인증되지 않았다면, 스프링 시큐리티 필터는 요청을 잡아내고 사용자를 로그인 페이지로 리다이렉션 해준다.  
**permitAll()** : 메소드는 어떠한 보안 요구 없이 요청을 허용해준다.  
**formLogin()** : 권한이 없는 사람이 페이지를 이동하려고 하면 로그인 페이지로 이동  
**loginPage()** : 해당하는 로그인 페이지 URL로 이동  
**loginProcessingUrl()** : 스프링 시큐리티가 해당 주소로 요청오는 로그인을 가로채서 대신 로그인해줍니다. (loginProcessingUrl는 잠시 주석처리해놓겠습니다. 지금 당장 사용하지 않을 겁니다.)  
**defaultSuccessUrl()** : 로그인이 성공하면 해당 URL로 이동  

	
**PrincipalDetails**  

```Java
package com.example.demo.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.domain.user.User;

import lombok.Getter;

@Getter
public class PrincipalDetail implements UserDetails {

    private User user;
    
  //일반 사용자
    public PrincipalDetail(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(() -> user.getRoleKey());

        return collection;
    }

    //사용자 패스워드
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    //사용자 아이디
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    
  //사용자 이메일
    public String getEmail() {
        return user.getEmail();
    }

    //사용자 닉네임
    public String getNickname() {
        return user.getNickname();
    }

    //사용자 pk
    public Long getId() {
        return user.getId();
    }
    
    public void setUser(User user) {
    	this.user = user;
    }

    //계정이 만료되었는지 (true: 만료되지 않음)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //계정이 잠겨있는지 (true: 잠겨있지 않음)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //패스워드가 만료되지 않았는지 (true: 만료되지 않음)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //계정이 활성화되어 있는지 (true: 활성화)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
```
**UserDatails**: 객체를 상속받으면 스프링 시큐리티의 고유한 세션저장소에 저장을 할 수 있게 됩니다.


**PrincipalDetalisService**  
```Java
@RequiredArgsConstructor
@Service
public class PrincipalDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User principal =  userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. " + username));
        return new PrincipalDetail(principal);
    }
}
```
@Service로 Bean으로 등록합니다.  
UserDetailsService를 상속받게 되면 오버라이딩을 해야하는데 이 메소드는 DB에 username이 있는지 확인하는 메소드입니다.  
PrincipalDetail(principal)을 리턴을 하게 되면 시큐리티의 세션에 유저 정보가 저장됩니다.   
처음에 userId로 했는데 값이 들어가지 않아 오류가 났지만, username으로 변경하니 잘 작동되었다.    
정확한 이유는 모르겠지만 username으로 고정으로 사용해야 겠다.. 다음 또 시큐리티를 사용하게 되면 다시 시도 해봐야겠다.  
	
</details>  

## Security 로그인  

<details>   
<summary>접기/펼치기</summary>  

**SCRF 설정**  
Cross-site request forgery의 약자로 타사이트에서 본인의 사이트로 form 데이터를 사용하여 공격하려고 할 때, 그걸 방지하기 위해 csrf 토큰 값을 사용하는 것이다.  
타임리프 템플릿으로 form 생성시 타임리프, 스프링 MVC, 스프링 시큐리티가 조합이 되어 자동으로 csrf 토큰 기능을 지원해준다.
[참고](https://wiken.io/ken/957)  

**CORS**  
Cross-Origin Resource Sharing,CORS의 약자로 다른 출처의 자원을 공유할 수 있도록 설정하는 권한 체제를 말합니다.
[참고](https://valuefactory.tistory.com/1141)  

**login.html**  
```Java
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<meta name="_csrf" th:content="${_csrf.token}">
<meta name="_csrf_header" th:content="${_csrf.headerName}">
<head th:replace="layout/header :: header" />
<body>
	<div class="container">
		<div th:replace="layout/bodyHeader :: bodyHeader" />
		<form action="/auth/api/v1/user/login" method="post">
		<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
            <h1 class="h3 m-3 fw-normal">로그인</h1>
            <div th:if="${param.error}" class="alert alert-danger" role="alert">
                아이디 혹은 비밀번호가 잘못 입력되었습니다.
            </div>
            <div th:if="${param.logout}" class="alert alert-primary" role="alert">
                로그아웃이 완료되었습니다.
            </div>
            <div class="form-floating m-3">
            	<label for="username">아이디</label>
                <input type="text" name="username" class="form-control" id="username" placeholder="아이디를 입력하세요." required>
            </div>
            <div class="form-floating m-3">
           	 	<label for="password">패스워드</label>
                <input type="password" name="password" class="form-control" id="password" placeholder="패스워드를 입력하세요." required>
            </div>
            <div class="checkbox mb-3">
                <input type="checkbox" name="remember-me" id="rememberMe">
                <label for="rememberMe" aria-describedby="rememberMeHelp">로그인 유지</label>
            </div>
            <button class="w-100 btn btn-lg btn-primary" id="btn-login">로그인</button>
        </form>
		<br />
		<div th:replace="layout/footer :: footer" />
	</div>
<script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>
<script th:src="@{/js/user.js}"></script>
</body>
</html>
```  
```<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />```: 이렇게 해줘도 토큰값을 받을 수 있다.  

</details>    


# 회원목록

<details>   
<summary>접기/펼치기</summary>  

**SecurityConfig.class**
```Java
http.authorizeRequests()  //권한 
    .antMatchers("/admin/userList").access("hasRole('ADMIN')");
```
시큐리티에 ADMIN 만 볼 수 있게 주소를 설정

**UserList.html**  
```Java
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="layout/header :: header" />
<body>
	<div class="container">
		<div th:replace="layout/bodyHeader2 :: bodyHeader" />
		<div>
			<table class="table table-striped">
				<thead>
					<tr>
						<th>#</th>
						<th>아이디</th>
						<th>패스워드</th>
						<th>이메일</th>
						<th>이름</th>
						<th>가입날짜</th>
					</tr>
				</thead>
				<tbody>
					<tr th:each="user : ${user}">
						<td th:text="${user.id}"></td>
						<td th:text="${user.username}"></td>
						<td th:text="${user.password}"></td>
						<td th:text="${user.email}"></td>
						<td th:text="${user.nickname}"></td>
						<td th:text="${#temporals.format(user.createdDate, 'yyyy-MM-dd')}"></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div th:replace="layout/footer :: footer" />
	</div>
</body>
</html>

```
th:each 문법을 사용하여 회원정보를 출력

**AdminController**  
```Java
@GetMapping("/admin/userList")
	public String userList(Model model) {
		List<User> user = userService.findUser();
		model.addAttribute("user", user);
		return "user/admin/userList";
	}
```
JpaRepository에 기본으로 재공하는 findAll() 사용하여 회원 정보를 가져옴.

</details>  

# 회원수정
<details>   
<summary>접기/펼치기</summary>  

**UserApiController.class**  
```Java
@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

...
    
    //회원 수정 API
    @PutMapping("/api/v1/user")
    public Long update(@RequestBody User user, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        userService.update(user, principalDetail);
        return user.getId();
    }
    
}
```
**@AuthenticationPrincipal**: 로그인한 사용자의 정보를 파라메터로 받고 싶을때 기존에는 다음과 같이 Principal 객체로 받아서 사용한다. [참고](https://ncucu.me/137)  

**update.html**
```Java
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<meta name="_csrf" th:content="${_csrf.token}">
<meta name="_csrf_header" th:content="${_csrf.headerName}">
<head th:replace="layout/header :: header" />
<body>
	<div class="container">
		<div th:replace="layout/bodyHeader2 :: bodyHeader" />
		<main class="form-signin" style="max-width: 100%;">
   			<div class="container border rounded flex-md-row mb-4 shadow-sm h-md-250">
        		<form>
            		<h1 class="h3 m-3 fw-normal">글수정</h1>
           		 	<input type="hidden" id="id" th:value="${board.id}">
            		<div class="form-floating m-3">
            			<label for="title">제목</label>
                		<input type="text" th:value="${board.title}" class="form-control" id="title" placeholder="제목을 입력하세요." required>
            		</div>
            		<div class="form-floating m-3">
            			<label for="content">내용</label>
                		<textarea class="form-control" th:text="${board.content}" rows="5" id="content" style="height: 450px;"></textarea>
            		</div>
        		</form>
        		<button class="btn btn-danger" id="btn-delete" style="margin-left: 20px;">삭제</button>
        		<button class="w-100 btn btn-lg btn-primary" id="btn-update" style="max-width: 250px; margin-left: 140px;">수정완료</button>
    		</div>
		</main>
		<br>
		<div th:replace="layout/footer :: footer" />
	</div>
<script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>
<script th:src="@{/js/board.js}"></script>
</body>
</html>
```

**PrincipalDetail.class**
```Java
@RequiredArgsConstructor
public class PrincipalDetail implements UserDetails {

    private final User user;

    ...

    //사용자 이메일
    public String getEmail() {
        return user.getEmail();
    }

    //사용자 닉네임
    public String getNickname() {
        return user.getNickname();
    }

    //사용자 pk
    public Long getId() {
        return user.getId();
    }
    ...
```

**UserService**
```Java
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //회원가입 로직
	...
    
    //회원수정 로직
    @Transactional
    public Long update(User user, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        User userEntity = userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다. id=" + user.getId()));
        userEntity.update(bCryptPasswordEncoder.encode(user.getPassword()), user.getNickname());
        principalDetail.setUser(userEntity);
        return userEntity.getId();
    }
    
    //유저목록
    ...
    
}
```
**User**
```Java
//비밀번호 암호화 메소드
    public void setPassword(String password) {
        this.password = password;
    }
    
    //권한 메소드
    public String getRoleKey() {
        return this.role.getKey();
    }
    
    //회원수정 메소드
    public void update(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }
```

**SecurityConfig**
```Java
@Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
```

</details>  


# 게시판 CRUD  
## Board 테이블 생성

<details>   
<summary>접기/펼치기</summary>  

**Board**
```Java
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicInsert  //@DynamicInsert, @DynamicUpdate는 각각 insert 또는 update 시 null인 field는 제외 한다.
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    private String content;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int count; //조회수

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;
    
    @OrderBy("id desc")
    @JsonIgnoreProperties({"board"})
    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Reply> replyList;
    
    @Column(columnDefinition = "varchar(20) default 'Y'")
    private String useYn;
    
    @OneToMany(mappedBy = "board", cascade = CascadeType.PERSIST)
    private List<FileEntity> fileEntity = new ArrayList<>();
    
    //글 수정
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    //글 삭제
    public void delete(String useYn) {
    	this.useYn = useYn;
    }
    
}
```
**@NoArgsConstructor(access = AccessLevel.PROTECTED):** Entity나 DTO를 사용할때 많이 사용하는 편입니다.  
기본 생성자의 접근 제어를 PROTECTED로 설정해놓게 되면 무분별한 객체 생성에 대해 한번 더 체크할 수 있는 수단이 되기 때문입니다.
[참조](https://cobbybb.tistory.com/14)  
**@CreationTimestamp:** INSERT 쿼리가 발생할 때, 현재 시간을 값으로 채워서 쿼리를 생성한다. @InsertTimeStamp 어노테이션을 사용하면 데이터가 생성된 시점에 대한 관리하는 수고스러움을 덜 수 있다.
[참조](https://velog.io/@koo8624/Spring-CreationTimestamp-UpdateTimestamp)  
**@UpdateTimestamp:** UPDATE 쿼리가 발생할 때, 현재 시간을 값으로 채워서 쿼리를 생성한다. @UpdateTimestamp 어노테이션을 사용하면 수정이 발생할 때마다 마지막 수정시간을 업데이트 해주어야 하는 데이터에 유용하게 활용될 수 있다.  
**@Builder:** [참조](https://mangkyu.tistory.com/163)  
**BaseTimeEntity** :  JPA Auditing으로 생성시간/수정시간 자동화   
사실 테이블을 만들때 필요한 컬럼은 생성시간과 수정입니다. 언제 데이터가 들어갔는지 이런 시간이 중요합니다.  
Entity 클래스마다 따로 날짜 필드를 생성하지 않아도 자동으로 생성하도록 하겠습니다.  
클래스는 모든 Entity의 상위클래스가 되어 Entity들의 날짜 필드를 자동으로 관리합니다.  
**@Lob** : 대용량 데이터를 저장할 때 사용합니다. 내용은 summernote라는 라이브러리를 이용할 것이기 때문에 @Lob으로 설정합니다.  
**@ManyToOne(fetch = FetchType.EAGER)** : 게시글을 작성할 때 누가 작성했는지 알아야 하기 때문에 User 테이블과 조인해야합니다. 이 때 Java코드로 객체를 선언하게 되면 ORM문법으로 알아서 조인을 해줍니다. 즉, id값이 서로 있으니까 id값으로 foreign키를 생성하는 거죠. 그리고 이 때 연관관계를 맺어줘야 하는데 게시판과 유저의 관계를 한 명의 유저가 여러 게시글을 작성할 수 있으므로 @ManyToOne을 사용합니다. @ManyToOne의 FetchType의 디폴트값이 EAGER 입니다. (EAGER 전략은 조인할 때 관련된 데이터들을 모두 가져오는 것이죠.)  
**@JoinColumn(name = "userId")** : foreign키의 컬럼명 설정입니다.  


**BaseTimeEntity**
```Java
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate //생성할 때 자동저장
    private LocalDateTime createdDate;

    @LastModifiedDate //수정할 때 자동저장
    private LocalDateTime modifiedDate;
}

```  
**@MappedSuperclass** : JPA Entity 클래스들이 BaseTimeEntity를 상속할 경우 날짜 필드도 칼럼으로 인식  

**JpaConfig**
```Java
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}

```
**@EnableJpaAuditing** : JPA Auditing 활성화[참고](https://webcoding-start.tistory.com/53)  
**@Configuration**: 설정파일을 만들기 위한 애노테이션 or Bean을 등록하기 위한 애노테이션이다.


</details>  

## 글쓰기 구현
<details>   
<summary>접기/펼치기</summary>  

**register.html**
<details>   
<summary>접기/펼치기</summary>  

```Java
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<meta name="_csrf" th:content="${_csrf.token}">
<meta name="_csrf_header" th:content="${_csrf.headerName}">
<head th:replace="layout/header :: header" />
<script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>
<body>
	<div class="container">
		<div th:replace="layout/bodyHeader2 :: bodyHeader" />
		<form action="/auth/board/register" method="post" enctype="multipart/form-data">
            <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
            <h1 class="h3 m-3 fw-normal">글쓰기</h1>
            <div class="form-floating m-3">
            <label for="title">제목</label>
                <input type="text" class="form-control" name="title" id="title" placeholder="제목을 입력하세요." required>
            </div>
            <div class="form-floating m-3">
            <label for="content">내용</label>
                <textarea class="form-control" rows="5" name="content" id="content" style="height: 450px;"></textarea>
            </div>
            
	            <input type="file" name="file">
	            <input type="file" multiple="multiple" name="files">
				<br/>
				<br/>
		   	    <input class="w-100 btn btn-lg btn-primary" type="submit" value="작성완료" style="max-width: 250px; margin-left: 220px;"/>
		    </form>
		    <!--  <button type="submit" class="w-100 btn btn-lg btn-primary" id="btn-save" style="max-width: 250px; margin-left: 220px;">작성완료</button>-->
        	<br>
		<div th:replace="layout/footer :: footer" />
	</div>
	<script th:src="@{/js/board.js}"></script>
</body>
```Java
'board strict';

let index = {
    init: function () {
        $("#btn-save").on("click", () => {
            this.save();
        });
        $("#btn-delete").on("click", () => {
            this.delete();
        });
        $("#btn-update").on("click", () => {
            this.update();
        });
    },

    save: function () {
        let data = {
            title: $("#title").val(),
            content: $("#content").val(),
            useYn: 'Y'
        }

        $.ajax({
            type: "POST",
            url: "/api/v1/board",
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (res) {
            alert("글작성이 완료되었습니다.");
            location.href = "/auth/board/list";
        }).fail(function (err) {
            alert(JSON.stringify(err));
        });
    },

    delete: function () {
		let id = $("#id").val();
		
        let data = {
		useYn: 'N'
		}

        $.ajax({
            type: "PUT",
            url: "/api/v1/board/delete/" + id,
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (res) {
            alert("글삭제가 완료되었습니다.");
            location.href = "/auth/board/list";
        }).fail(function (err) {
            alert(JSON.stringify(err));
        });
    },

    update: function () {
        let id = $("#id").val();

        let data = {
            title: $("#title").val(),
            content: $("#content").val()
        }

        $.ajax({
            type: "PUT",
            url: "/api/v1/board/" + id,
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (res) {
            alert("글수정이 완료되었습니다.");
            location.href = "/auth/board/list";
        }).fail(function (err) {
            alert(JSON.stringify(err));
        });
    }
}

index.init();

var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
});
```

</html>
```
</details>  

**BoardController**
```Java
@RequiredArgsConstructor
@Controller
public class BoardController {

	private final BoardService boardService;
	private final FileRepository fileRepository;
	private final FileService fileService;
	
	// 글작성 페이지 이동
	@GetMapping("/auth/board/register")
	public String save() {
		return "layout/board/register";
	}
	
	// 글작성
	@PostMapping("/auth/board/register")
	public String registerPost(BoardSaveRequestDto boardSaveRequestDto,
							   @AuthenticationPrincipal PrincipalDetail principalDetail) {
	
		boardService.register(boardSaveRequestDto, principalDetail.getUser());
		return "redirect:/auth/board/list";
	}
}
```

**BoardSaveRequestDto**
```Java
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardSaveRequestDto {

    private Long id;
    private String title;
    private String content;
    private int count;
    private User user;
    private String useYn;
    
    private int attachCount;
    private String fileIdxs;
    private String deleteFileIdxs;

    public Board toEntity() {
        return Board.builder()
                .title(title)
                .content(content)
                .count(0)
                .user(user)
                .useYn(useYn)
                .build();
    }

    public void setUser(User user) {
        this.user = user;
    }
}
```

**BoardService**
```Java
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    //글작성 로직
    @Transactional
    public Long register(BoardSaveRequestDto boardSaveRequestDto, User user) {
    	boardSaveRequestDto.setUser(user);
    	return boardRepository.save(boardSaveRequestDto.toEntity()).getId();
    }
   
}
```
</details>  

## 글 목록

<details>   
<summary>접기/펼치기</summary>  

**BoardController**
```Java
@RequiredArgsConstructor
@Controller
public class BoardController {

	private final BoardService boardService;
	private final FileRepository fileRepository;
	private final FileService fileService;

	@GetMapping("/auth/board/list")
	public String list(Model model) {
		model.addAttribute("boards", boards);
		return "layout/board/list";
	}
}
```
View에 뿌려줄 모델을 파라미터로 박아서 키값을 boards라고 합시다.  

**BoardService**
```Java
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    /**
     * 글목록 로직
     */
    public List<Board> findAll() {
        return boardRepository.findAll();
    }
}
```
JPA의 findAll() 메소드를 사용하면 테이블의 raw 데이터를 모두 조회해서 가져옵니다.  

**list.html**
<details>   
<summary>접기/펼치기</summary>  

```Java
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<meta name="_csrf" th:content="${_csrf.token}">
<meta name="_csrf_header" th:content="${_csrf.headerName}">
<head th:replace="layout/header :: header" />
<body>
	<div class="container">
		<div th:replace="layout/bodyHeader2 :: bodyHeader" />
		<form class="form-inline d-flex justify-content-end" method="GET" th:action="@{/auth/board/list}">
            <div class="form-group mx-sm-3 mb-2">
                <select class="form-control" aria-label="Default select example" name="select">
					<option value="title">제목</option>
					<option value="content">내용</option>
				</select>
				<label for="search" class="sr-only">검색어 입력</label>
                <input type="search" placeholder="Search" class="form-control me-2" id="search" name="keyword" th:value="${param.keyword}">
            </div>
            <button type="submit" class="btn btn-outline-primary">Search</button>
        </form>
            
		<main  th:each="board : ${boards}" class="flex-shrink-0">
			<th:block th:if="${board.useYn == 'Y'}">
			<div class="container">
				<div class="p-2"></div>
				<div class="row g-0 border rounded overflow-hidden flex-md-row mb-4 shadow-sm h-md-250 position-relative">
					<div class="col p-4 d-flex flex-column position-static">
						 <a th:href="@{/auth/board/{id}(id=${board.id})}" class="a-title">
                   			 <h3 class="mb-0 title" style="padding-bottom: 10px;" th:text="${board.title}"></h3>
                		</a>
						<div class="card-text mb-auto" th:text="${board.content}"></div>
						<div class="mb-1 text-muted" style="padding-top: 15px;" th:text="${#temporals.format(board.createdDate, 'yyyy-MM-dd')}"></div>
						<div class="mb-1 text-muted" style="padding-top: 15px;" th:text="${board.count}"></div>
					</div>
				</div>
			</div>
		</main>
		</th:block>
		<br>
		<a  class="btn btn-primary" th:href="@{/auth/board/register}">글쓰기</a>

		<nav aria-label="Page navigation example">
			<ul class="pagination justify-content-center">
			    <li class="page-item" th:classappend="${1 == boards.pageable.pageNumber + 1} ? 'disabled' : '' ">
			      <a class="page-link" th:href="@{/auth/board/list/(page=${boards.pageable.pageNumber - 1}, search=${param.search})}">Previous</a>
			    </li>
			    <li class="page-item"  th:classappend="${i == boards.pageable.pageNumber + 1} ? 'active' : '' " th:each="i : ${#numbers.sequence(startPage, endPage)}">
			      <a class="page-link" th:href="@{/auth/board/list/(page=${i - 1}, search=${param.search})}" th:text="${i}"></a>
			    </li>
			    <li class="page-item" th:classappend="${boards.totalPages == boards.pageable.pageNumber + 1} ? 'disabled' : '' ">
			      <a class="page-link" th:href="@{/auth/board/list/(page=${boards.pageable.pageNumber + 1}, search=${param.search})}">Next</a>
			    </li>
			</ul>
		</nav>
		    
	</div>
	<div th:replace="layout/footer :: footer" />
</body>
</html>

```

</details>  

</details> 

## 글 상세
<details>   
<summary>접기/펼치기</summary>  

**BoardController**  
```Java
@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardService boardService;

    ...

    @GetMapping("/auth/board/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.detail(id));
        return "layout/board/detail";
    }
}
```
주소 뒤에 {id} 이렇게 id를 받을 때는 @PathVariable을 사용하면 주소의 id로 받습니다.  

**BoardService**  
```Java
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    ...

    //글상세 로직
    @Transactional(readOnly = true)
    public Board detail(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 id가 없습니다. id=" + id));
    }
}
```
**detali.html**
<details>   
<summary>접기/펼치기</summary>  

```Java
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<meta name="_csrf" th:content="${_csrf.token}">
<meta name="_csrf_header" th:content="${_csrf.headerName}">
<head th:replace="layout/header :: header" />
<script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>
<body>
	<div class="container">
		<div th:replace="layout/bodyHeader2 :: bodyHeader" />
		<table class="table table-striped">
    <thead>
    <tr>
        <th scope="col">글 번호</th>
        <th scope="col">작성자</th>
        <th scope="col">조회수</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <th scope="row" th:text="${board.id}" id="id"></th>
        <td th:text="${board.user.username}"></td>
        <td th:text="${board.count}"></td>
    </tr>
    </tbody>
</table>

<main class="form-signin" style="max-width: 100%;">
    <div class="container border rounded flex-md-row mb-4 shadow-sm h-md-250">
        <div class="form-floating m-3">
            <h3 th:text="${board.title}" style="margin-bottom: 20px;"></h3>
        </div>
        <hr/>
        <div class="form-floating m-3">
            <p th:text="${board.content}"></p>
            <br/>
            <div th:each="imageFile : ${all} ">
				<img th:src="|/images/${imageFile.id}|" >
				<br/>
				<br/>
			</div>
        </div>
			<h3 class="form-floating m-3">파일 다운로드</h3>
			<hr/>
			<div class="form-floating m-3" th:each="file : ${all}">
				<a th:href="|/attach/${file.id}|" th:text="${file.orgNm}"></a>
			</div>
    </div>
    <span th:if="${board.user.id == #authentication.principal.id}">
        <a th:href="@{/board/{id}/update(id=${board.id})}" class="btn btn-warning">수정</a>
    </span>
    <button class="btn btn-secondary" onclick="history.back()">뒤로</button>

    <div class="card mb-2 mt-5">

        <div class="card-header bg-light">
            <i class="fa fa-comment fa"></i> 댓글
        </div>
        <form>
            <div class="card-body">
                <input type="hidden" id="boardId" th:value="${board.id}">
                <ul class="list-group list-group-flush">
                    <li class="list-group-item">
                        <textarea class="form-control" id="reply-content" rows="1"></textarea>
                        <button id="reply-btn-save" type="button" class="btn btn-primary mt-3">등록</button>
                    </li>
                </ul>
            </div>
        </form>
    </div>
    <br/>
    <div class="card">
        <div class="card-header">댓글</div>
        <ul id="reply--box" class="list-group" th:each="reply : ${board.replyList}">
            <li th:id="'reply--' + ${reply.id}" class="list-group-item d-flex justify-content-between">
                <div th:text="${reply.content}"></div>
                <div class="d-flex" >
                    <span class="text-monospace">작성자: &nbsp;</span><div class="text-monospace" th:text="${reply.user.username}"></div>
                    <span th:if="${reply.user.id == #authentication.principal.id}">
                        <button th:onclick="|replyIndex.replyDelete('${board.id}', '${reply.id}')|" class="badge btn-danger" style="margin-left: 10px;">삭제</button>
                    </span>
                </div>
            </li>
        </ul>
    </div>
</main>
		<div th:replace="layout/footer :: footer" />
	</div>
<script th:src="@{/js/board.js}"></script>
<script th:src="@{/js/reply.js}"></script>
</body>
</html>
```
삭제 버튼과 수정 버튼은 글쓴이만 볼 수 있게 해야줘야 합니다.  
thymeleaf 조건문을 쓰면 해결됩니다. th:if="${board.user.id == #authentication.principal.id}"  
board에 저장되어있는 userId와 현재 로그인 되어있는 id와 비교하면 끝입니다.  
수정하기는 수정하기 페이지가 따로 있기 때문에 a 태그를 걸어주었습니다.  

</details>  

</details>  

## 글 삭제
<details>   
<summary>접기/펼치기</summary>  

**board.js**
```Java
'board strict';

let index = {
    init: function () {
        $("#btn-save").on("click", () => {
            this.save();
        });
        $("#btn-delete").on("click", () => {
            this.delete();
        });
        $("#btn-update").on("click", () => {
            this.update();
        });
    },

    save: function () {
        let data = {
            title: $("#title").val(),
            content: $("#content").val(),
            useYn: 'Y'
        }

        $.ajax({
            type: "POST",
            url: "/api/v1/board",
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (res) {
            alert("글작성이 완료되었습니다.");
            location.href = "/auth/board/list";
        }).fail(function (err) {
            alert(JSON.stringify(err));
        });
    },

    delete: function () {
		let id = $("#id").val();
		
        let data = {
		useYn: 'N'
		}

        $.ajax({
            type: "PUT",
            url: "/api/v1/board/delete/" + id,
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (res) {
            alert("글삭제가 완료되었습니다.");
            location.href = "/auth/board/list";
        }).fail(function (err) {
            alert(JSON.stringify(err));
        });
    },

    update: function () {
        let id = $("#id").val();

        let data = {
            title: $("#title").val(),
            content: $("#content").val()
        }

        $.ajax({
            type: "PUT",
            url: "/api/v1/board/" + id,
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (res) {
            alert("글수정이 완료되었습니다.");
            location.href = "/auth/board/list";
        }).fail(function (err) {
            alert(JSON.stringify(err));
        });
    }
}

index.init();

var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
});
```

**BoardApiController**  
```Java
@RequiredArgsConstructor
@RestController
public class BoardApiController {

    private final BoardService boardService;

    @PutMapping("/api/v1/board/delete/{id}")
    public Long delete(@PathVariable Long id, @RequestBody BoardDeleteRequestDto boardDeleteRequestDto) {
        return boardService.delete(id, boardDeleteRequestDto);
    }
}
```
deleteMapping 쓰지 않고 Put으로 사용해서 useYn 값만 N으로 수정하여 게시판에 안보이게 설정하였다.  

**BoardService**  
```Java
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public Long delete(Long id, BoardDeleteRequestDto boardDeleteRequestDto) {
    	Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 id가 없습니다." + id));
    	board.delete(boardDeleteRequestDto.getUseYn());
        return id;
    }
```

**BoardDeleteRequestDto**  
```Java
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BoardDeleteRequestDto {
	private Long id;
	private String useYn;
	
	public Board toEntity() {
		return Board.builder()
			.useYn(useYn)
			.build();
	}
}

```

</details>  

## 글 수정
<details>   
<summary>접기/펼치기</summary>  

**update.html**
<details>   
<summary>접기/펼치기</summary>  

```Java
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<meta name="_csrf" th:content="${_csrf.token}">
<meta name="_csrf_header" th:content="${_csrf.headerName}">
<head th:replace="layout/header :: header" />
<body>
	<div class="container">
		<div th:replace="layout/bodyHeader2 :: bodyHeader" />
		<main class="form-signin" style="max-width: 100%;">
   			<div class="container border rounded flex-md-row mb-4 shadow-sm h-md-250">
        		<form>
            		<h1 class="h3 m-3 fw-normal">글수정</h1>
           		 	<input type="hidden" id="id" th:value="${board.id}">
            		<div class="form-floating m-3">
            			<label for="title">제목</label>
                		<input type="text" th:value="${board.title}" class="form-control" id="title" placeholder="제목을 입력하세요." required>
            		</div>
            		<div class="form-floating m-3">
            			<label for="content">내용</label>
                		<textarea class="form-control" th:text="${board.content}" rows="5" id="content" style="height: 450px;"></textarea>
            		</div>
        		</form>
        		<button class="btn btn-danger" id="btn-delete" style="margin-left: 20px;">삭제</button>
        		<button class="w-100 btn btn-lg btn-primary" id="btn-update" style="max-width: 250px; margin-left: 140px;">수정완료</button>
    		</div>
		</main>
		<br>
		<div th:replace="layout/footer :: footer" />
	</div>
<script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>
<script th:src="@{/js/board.js}"></script>
</body>
</html>
```
input 태그에 hidden값으로 id를 받았습니다.  
</details>  


**BoardController**
```Java
// 글수정 페이지 이동
	@GetMapping("/board/{id}/update")
	public String update(@PathVariable Long id, Model model) {
		model.addAttribute("board", boardService.detail(id));
		return "layout/board/update";
	}
```
글 수정 페이지 역시 id값이 필요하므로 주소 id를 받구요. Model에 데이터를 담습니다.  

**BoardApiController**
```Java
// 글수정 API
    @PutMapping("/api/v1/board/{id}")
    public Long update(@PathVariable Long id, @RequestBody BoardUpdateRequestDto boardUpdateRequestDto) {
        return boardService.update(id, boardUpdateRequestDto);
    }
```
update할 데이터를 따로 dto로 클래스로 만들어줍니다.  

**Board**
```Java
public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
```
update 메소드를 추가합니다.  
JPA에서 udpate를 진행할 때는 영속성 컨텍스트에 있는 값과 비교를 해서 변경된 값이 있으면 그 변경된 값만 update 시켜줍니다. 이것을 변경감지라 하여 더치체킹이라 부릅니다.  
즉, Entity 객체의 값만 변경시켜주면 더티체킹이 일어납니다. (Update 쿼리문을 날릴 필요가 없습니다!!)  

**BoardService**
```Java
//글수정 로직
   @Transactional
   public Long update(Long id, BoardUpdateRequestDto boardUpdateRequestDto) {
       Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 id가 없습니다. id=" + id));
       board.update(boardUpdateRequestDto.getTitle(), boardUpdateRequestDto.getContent());
       return id;
   }
```
먼저 boardRepository.findById(id)로 찾아서 Board를 영속화시킵니다. 그러면 영속성 컨텍스트에 Board 객체가 담아집니다.  
그리고 나서 Board의 값을 변경시키면 Service가 종료되는 시점에 트랜잭션이 종료되고 더티체킹이 일어납니다.  

</details>  

# 조회수, 페이징과 검색
## 조회수
<details>   
<summary>접기/펼치기</summary>  

쿠키를 이용해서 중복방지를 합니다.  

**BoardRepository**
```Java
public interface BoardRepository extends JpaRepository<Board, Long> {
	
    //조회수 증가 쿼리
    @Modifying
    @Query("update Board p set p.count = p.count + 1 where p.id = :id")
    int updateCount(@Param("id") Long id);
	
}
```
수정 반영을 위한 @Modifying를 사용합니다.
[참고](https://joojimin.tistory.com/71)  

**BoardControllre**
```Java
@GetMapping("/auth/board/{id}")
	public String detail(@PathVariable Long id, Model model, HttpServletRequest request, HttpServletResponse response) {
		// 쿠키를 이용한 조회수 중복 방지
		Cookie oldCookie = null;
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if (cookie.getName().equals("postView")) {
	                oldCookie = cookie;
	            }
	        }
	    }
	    if (oldCookie != null) {
	        if (!oldCookie.getValue().contains("[" + id.toString() + "]")) {
	        	boardService.updateCount(id);  // 조회수 증가
	            oldCookie.setValue(oldCookie.getValue() + "_[" + id + "]");
	            oldCookie.setPath("/");
	            oldCookie.setMaxAge(60 * 60 * 24);
	            response.addCookie(oldCookie);
	        }
	    } else {
	    	boardService.updateCount(id);  // 조회수 증가
	        Cookie newCookie = new Cookie("postView","[" + id + "]");
	        newCookie.setPath("/");
	        newCookie.setMaxAge(60 * 60 * 24);
	        response.addCookie(newCookie);
	    }
	    model.addAttribute("board", boardService.detail(id));
	    
		return "layout/board/detail";
	}
```
[참고](https://mighty96.github.io/til/view/)  

</details>  

## 페이징, 검색
<details>   
<summary>접기/펼치기</summary>  

```Java
public interface BoardRepository extends JpaRepository<Board, Long> {
    
	Page<Board> findByUseYn(String useYn, Pageable pageable);

	Page<Board> findByTitleContainingAndUseYnIgnoreCase(String keyword, String useYn, Pageable pageable);

	Page<Board> findByContentContainingAndUseYnIgnoreCase(String keyword, String useYn, Pageable pageable);
}
```
 
findBy <- 꼭 들어가야하는 코드  
Content <- 변수  
Containing <- %like% 문  
And  
UseYn <-변수  
IgnoreCase <- 대소문자 관계 없이 검색하는 쿼리
[참고](https://recordsoflife.tistory.com/59)  
**JpaRepository**: JPA가 기본적으로 제공하는 메서드를 사용 할 수 있다.  
**Modifying**: @Query 어노테이션을 통해 작성된 INSERT, UPDATE, DELETE(SELECT 제외) 쿼리에서 사용되는 어노테이션 이고 기본적으로 JpaRepository에서 제공하는 메서드 혹은 메서드 네이밍으로 만들어진 쿼리에는 적용되지 않습니다.  
**Query**: SQL과 유사한 JPQL (Java Persistence Query Language) 라는 객체지향 쿼리 언어를 통해 복잡한 쿼리 처리를 지원  


**BoardService**
```Java
@Transactional(readOnly = true)
	public Page<Board> selectList(Pageable pageable, String select, String keyword) {
		
		String useYn = "Y";
		
		if(select.equals("title")) {
			return boardRepository.findByTitleContainingAndUseYnIgnoreCase(keyword, useYn, pageable);
		} else if(select.equals("content")) {
			return boardRepository.findByContentContainingAndUseYnIgnoreCase(keyword, useYn, pageable);
		} else {
			return boardRepository.findByUseYn(useYn, pageable);
		}
		
	}
```
JPA에서는 Pageable 인터페이스를 사용하면 페이징을 만들 수 있습니다. (이미 페이징은 모듈화해서 쓰는 곳이 많다고 하는데 이것을 따로 공부해도 좋을 것 같습니다.)  
이 때 리턴타입은 Page로 변경합니다.  

**BoardController**
```Java
@GetMapping("/auth/board/list")
	public String list(Model model,
			@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
			@RequestParam(required = false, defaultValue = "") String keyword,
			@RequestParam(required = false, defaultValue = "") String select) {
		Page<Board> boards = boardService.selectList(pageable, select, keyword);
		int startPage = Math.max(1, boards.getPageable().getPageNumber() - 4);
		int endPage = Math.min(boards.getTotalPages(), boards.getPageable().getPageNumber() + 4);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("boards", boards);
		return "layout/board/list";
	}
```
@PageableDefault를 설정하면 페이지의 size, 정렬순을 정할 수 있습니다. 저는 한 페이지당 5 Size, 최신글을 제일 맨위로 볼 수 있게 해두었습니다.  
boards.getPageable().getPageNumber() : 현재 페이지 번호  
startPage, endPage는 페이지 목록에서 시작 페이지 번호와 끝 페이지 번호입니다.  

**list.html**
<details>   
<summary>접기/펼치기</summary>  

```Java
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<meta name="_csrf" th:content="${_csrf.token}">
<meta name="_csrf_header" th:content="${_csrf.headerName}">
<head th:replace="layout/header :: header" />
<body>
	<div class="container">
		<div th:replace="layout/bodyHeader2 :: bodyHeader" />
		<form class="form-inline d-flex justify-content-end" method="GET" th:action="@{/auth/board/list}">
            <div class="form-group mx-sm-3 mb-2">
                <select class="form-control" aria-label="Default select example" name="select">
					<option value="title">제목</option>
					<option value="content">내용</option>
				</select>
				<label for="search" class="sr-only">검색어 입력</label>
                <input type="search" placeholder="Search" class="form-control me-2" id="search" name="keyword" th:value="${param.keyword}">
            </div>
            <button type="submit" class="btn btn-outline-primary">Search</button>
        </form>
            
		<main  th:each="board : ${boards}" class="flex-shrink-0">
			<th:block th:if="${board.useYn == 'Y'}">
			<div class="container">
				<div class="p-2"></div>
				<div class="row g-0 border rounded overflow-hidden flex-md-row mb-4 shadow-sm h-md-250 position-relative">
					<div class="col p-4 d-flex flex-column position-static">
						 <a th:href="@{/auth/board/{id}(id=${board.id})}" class="a-title">
                   			 <h3 class="mb-0 title" style="padding-bottom: 10px;" th:text="${board.title}"></h3>
                		</a>
						<div class="card-text mb-auto" th:text="${board.content}"></div>
						<div class="mb-1 text-muted" style="padding-top: 15px;" th:text="${#temporals.format(board.createdDate, 'yyyy-MM-dd')}"></div>
						<div class="mb-1 text-muted" style="padding-top: 15px;" th:text="${board.count}"></div>
					</div>
				</div>
			</div>
		</main>
		</th:block>
		<br>
		<a  class="btn btn-primary" th:href="@{/auth/board/register}">글쓰기</a>

		<nav aria-label="Page navigation example">
			<ul class="pagination justify-content-center">
			    <li class="page-item" th:classappend="${1 == boards.pageable.pageNumber + 1} ? 'disabled' : '' ">
			      <a class="page-link" th:href="@{/auth/board/list/(page=${boards.pageable.pageNumber - 1}, search=${param.search})}">Previous</a>
			    </li>
			    <li class="page-item"  th:classappend="${i == boards.pageable.pageNumber + 1} ? 'active' : '' " th:each="i : ${#numbers.sequence(startPage, endPage)}">
			      <a class="page-link" th:href="@{/auth/board/list/(page=${i - 1}, search=${param.search})}" th:text="${i}"></a>
			    </li>
			    <li class="page-item" th:classappend="${boards.totalPages == boards.pageable.pageNumber + 1} ? 'disabled' : '' ">
			      <a class="page-link" th:href="@{/auth/board/list/(page=${boards.pageable.pageNumber + 1}, search=${param.search})}">Next</a>
			    </li>
			</ul>
		</nav>
		    
	</div>
	<div th:replace="layout/footer :: footer" />
</body>
</html>

```
th:each="i : ${#numbers.sequence(startPage, endPage)}" : 시작 페이지부터 끝 페이지까지 Loop를 돕니다.  
th:classappend="${i == boards.pageable.pageNumber + 1} ? 'active' : '' "  
JPA에서는 페이지 번호가 0부터 시작하므로 1부터 카운트되게 하기 위해 +1을 해줍니다. boards.pageable.pageNumber + 1
그래서 서로 비교를 해서 같으면 'active' 를 추가합니다.  
th:href="@{/(page=${i - 1})}"  
thymeleaf에서 쿼리스트링을 사용하려면 () 안에 파라미터=${} 이런식으로 값을 넣어주시면 됩니다. 페이지 번호가 0부터 시작하므로 -1을 해줍니다.  


</details>  

</details>  

# 댓글
## Reply 엔티티
<details>   
<summary>접기/펼치기</summary>  

```Java
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @ManyToOne
    @JoinColumn(name = "boardId")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    
    public void save(Board board, User user) {
        this.board = board;
        this.user = user;
    }
}
```
댓글을 누가 작성했는지와 어느 게시글에 작성했는지 알아야 하기 때문에 연관관계가 필요합니다.  
Board : Reply : User -> 1 : N : 1  
한 게시글에 여러 개의 댓글과 한 유저가 여러 개의 댓글을 달 수 있습니다.  

```ReplyRepository**
```Java
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
```

### 무한참조
**Board**
```Java
@OrderBy("id desc")
    @JsonIgnoreProperties({"board"})
    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER)
    private List<Reply> replyList;
```
이러면 Board 테이블에 댓글리스트를 추가하겠다는 건데 DB에는 하나의 raw 데이터에 하나의 값만 들어갈 수 있습니다. 만약 여러 개의 데이터가 들어간다면 원자성이 깨집니다. 그래서 replyList는 DB에 FK로 생성되면 안되기 때문에 mappedBy를 사용합니다.  
mppedBy : 연관관계의 주인이 아니므로 DB의 FK가 아니다 라는 뜻입니다.  
@OneToMany의 디폴트 fetch는 Lazy입니다. 이것을 Eager로 변경합니다.  
Board를 조회할 때 Reply를 조회하게 되고 Reply를 조회하면 Board, User를 조회하게 됩니다.  
여기서 또 Board 조회하고 또 Reply를 조회하게 되고.... (무한 반복)  
해결하려면 Board 조회하고 Reply를 조회하고 다시 Board를 조회안하게 되면 됩니다.  
@JsonIgnoreProperties({"board"}) 를 추가하면 해결이 됩니다.  
@OrderBy("id desc") : 댓글 작성시 최근 순으로 볼 수 있도록 설정  

</details>  

## 댓글 작성
<details>   
<summary>접기/펼치기</summary>  


<details>   
<summary>detail.html</summary>  

```Java
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<meta name="_csrf" th:content="${_csrf.token}">
<meta name="_csrf_header" th:content="${_csrf.headerName}">
<head th:replace="layout/header :: header" />
<script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>
<body>
    <div class="card mb-2 mt-5">

        <div class="card-header bg-light">
            <i class="fa fa-comment fa"></i> 댓글
        </div>
        <form>
            <div class="card-body">
                <input type="hidden" id="boardId" th:value="${board.id}">
                <ul class="list-group list-group-flush">
                    <li class="list-group-item">
                        <textarea class="form-control" id="reply-content" rows="1"></textarea>
                        <button id="reply-btn-save" type="button" class="btn btn-primary mt-3">등록</button>
                    </li>
                </ul>
            </div>
        </form>
    </div>
    <br/>
    <div class="card">
        <div class="card-header">댓글</div>
        <ul id="reply--box" class="list-group" th:each="reply : ${board.replyList}">
            <li th:id="'reply--' + ${reply.id}" class="list-group-item d-flex justify-content-between">
                <div th:text="${reply.content}"></div>
                <div class="d-flex" >
                    <span class="text-monospace">작성자: &nbsp;</span><div class="text-monospace" th:text="${reply.user.username}"></div>
                    <span th:if="${reply.user.id == #authentication.principal.id}">
                        <button th:onclick="|replyIndex.replyDelete('${board.id}', '${reply.id}')|" class="badge btn-danger" style="margin-left: 10px;">삭제</button>
                    </span>
                </div>
            </li>
        </ul>
    </div>
</main>
		<div th:replace="layout/footer :: footer" />
	</div>
<script th:src="@{/js/board.js}"></script>
<script th:src="@{/js/reply.js}"></script>
</body>
</html>
```

**reply.js**
```Java
'board strict';

let replyIndex = {
    init: function () {
        $("#reply-btn-save").on("click", () => {
            this.replySave();
        });
    },

    replySave: function () {
        let data = {
            content: $("#reply-content").val(),
        }
        let boardId = $("#boardId").val();
        console.log(data);
        console.log(boardId);
        $.ajax({
            type: "POST",
            url: `/api/v1/board/${boardId}/reply`,
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "text"
        }).done(function (res) {
            alert("댓글작성이 완료되었습니다.");
            location.href = `/auth/board/${boardId}`;
        }).fail(function (err) {
            alert(JSON.stringify(err));
        });
    },

    replyDelete: function (boardId, replyId) {
        $.ajax({
            type: "DELETE",
            url: `/api/v1/board/${boardId}/reply/${replyId}`,
            dataType: "text"
        }).done(function (res) {
            alert("댓글삭제가 완료되었습니다.");
            location.href = `/auth/board/${boardId}`;
        }).fail(function (err) {
            alert(JSON.stringify(err));
        });
    },

}
replyIndex.init();

var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
});
```
</details>  

**ReplyApiController**
```Java
private final ReplyService replyService;

//댓글저장
    @PostMapping("/api/v1/board/{boardId}/reply")
    public void save(@PathVariable Long boardId,
                     @RequestBody Reply reply,
                     @AuthenticationPrincipal PrincipalDetail principalDetail) {
        replyService.replySave(boardId, reply, principalDetail.getUser());
    }
```
User 정보는 @AuthenticationPrincipal, boardId는 @PathVariable 통해서, Reply는 JSON으로 보내줍니다.  

**ReplyService**
```Java
private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void replySave(Long boardId, Reply reply, User user) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("해당 boardId가 없습니다. id=" + boardId));

        reply.save(board, user);

        replyRepository.save(reply);
    }
```
댓글을 저장할 때는 Board의 Id 값을 가져와야 합니다. 그래서 Board를 영속화시켜서 Board와 User를 저장합니다.  

**Reply**
```Java
 public void save(Board board, User user) {
        this.board = board;
        this.user = user;
    }
```
</details>  

## 게시글 삭제 에러
<details>   
<summary>접기/펼치기</summary>  

이 상태에서 게시글을 삭제하는데 에러가 발생합니다.  
왜냐하면 댓글에 외래키로 잡혀서 있어서 삭제가 안되는데 옵션을 주면 됩니다.  
**Board**
```Java
@OrderBy("id desc")
    @JsonIgnoreProperties({"board"})
    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Reply> replyList;
```
</details>  

## 댓글 삭제
<details>   
<summary>접기/펼치기</summary>  

**ReplyApiController**
```Java
//댓글삭제
    @DeleteMapping("/api/v1/board/{boardId}/reply/{replyId}")
    public void delete(@PathVariable Long replyId) {
        replyService.replyDelete(replyId);
    }
```

**ReplyService**
```Java
@Transactional
    public void replyDelete(Long replyId) {
        replyRepository.deleteById(replyId);
    }
```

</details>  

# 자동로그인
<details>   
<summary>접기/펼치기</summary>  

**SecurityCongig**
```Java
@Override
    protected void configure(HttpSecurity http) throws Exception {
        ...

        http
                .rememberMe().tokenValiditySeconds(60 * 60 * 7)
                .userDetailsService(principalDetailService);
    }
```
tokenValiditySeconds : 쿠키를 얼마나 유지할 것인지 계산합니다. (7일 설정)  
그 다음에 User 정보를 넣어주면 됩니다. principalDetailService  

**login.html**
```Java
<main class="form-signin">
    <div class="container border rounded flex-md-row mb-4 shadow-sm h-md-250">
        <form action="/auth/user/login" method="post">
            ...
            <div class="checkbox mb-3">
                <input type="checkbox" name="remember-me" id="rememberMe">
                <label for="rememberMe" aria-describedby="rememberMeHelp">로그인 유지</label>
            </div>
            <button class="w-100 btn btn-lg btn-success" id="btn-login">로그인</button>
        </form>

    </div>
</main>
```

</details>  


# 예외처리
<details>   
<summary>접기/펼치기</summary>  

**CustomAccessDeniedHandler**
```Java
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	 
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        //스프링 시큐리티 로그인때 만든 객체
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //현재 접속 url를 확인
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        String originalURL = urlPathHelper.getOriginatingRequestUri(request);
 
        //로직을 짜서 상황에 따라 보내줄 주소를 설정해주면 됨
        response.sendRedirect("/error");
    }
}
```

**error.html**
```Java
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="layout/header :: header">
<title>HelloSpringBoot</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<div class="container">
		<div th:replace="layout/bodyHeader2 :: bodyHeader" />
		<div class="jumbotron">
			<h1>일반사용자는 접근할 수 없습니다.</h1>
			<button class="btn btn-secondary" onclick="history.back()">돌아가기</button>
		</div>
		<div th:replace="layout/footer :: footer" />
	</div>
</body>
</html>
```

</details>  

# 파일 업로드/ 다운로드

# 프로젝트 진행과정에서 궁금했던 점
**[JPA] 요청 응답시 Entity 대신 DTO를 사용해야하는 이유**
[참고](https://tecoble.techcourse.co.kr/post/2020-08-31-dto-vs-entity/)  

# MariaDB 계정 생성 및 권한부여
<details>   
<summary>접기/펼치기</summary>  

![image](https://user-images.githubusercontent.com/94879395/169932602-b34596a4-0e7f-45d9-ad58-a7e31c47ac94.png)  
슈퍼계정에 로그인 한다.
![image](https://user-images.githubusercontent.com/94879395/169932667-b6727686-3ea7-49f1-b236-b08f1e2ac3bd.png)
![image](https://user-images.githubusercontent.com/94879395/169932692-99507534-83c0-4c31-b241-62c2f50b4b3c.png)
![image](https://user-images.githubusercontent.com/94879395/169932706-e1e6e7ee-4d81-4abd-8aac-e93b9837d9fc.png)
[참고](https://kithub.tistory.com/12)  

</details>  

# 후기
