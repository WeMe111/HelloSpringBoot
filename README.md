# 목차
[프로젝트 생성](#-프로젝트-생성)  
[DB 설정](#-DB-설정)  

# 프로젝트 생성
![프로젝트 생성1](https://user-images.githubusercontent.com/94879395/165679231-659fa912-256e-4feb-8445-a8ba387edee7.PNG)  
![image](https://user-images.githubusercontent.com/94879395/165679347-25ca6249-873a-4b38-88fd-2779bfbba8ff.png)

# DB 설정  
**build.gradle**  
```
dependencies {
	runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
}
```
의존성 추가 
**application.yml**  
```
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
**open-in-view** : 
**ddl-auto** : 프로젝트 실행 시에 자동으로 DDL(create, drop, alter 등)을 생성할 것인지를 결정하는 설정입니다. 주로 create와 update를 사용하는데 create는 프로젝트 실행 시 매번 테이블을 생성해주고, update는 변경이 필요한 경우 테이블을 수정해줍니다.  
**use-new-id-generate-mappings** : JPA의 기본 numbering(넘버링) 전략을 사용할 것인지에 대한 설정입니다. 저는 Entity 클래스에서 따로 설정해줄 것이기 때문에 false로 했습니다.  
**show-sql** : 프로젝트 실행 시 sql문을 로그로 보여줍니다.  
**hibernate.format_sql** : sql을 포맷팅해서 좀 더 예쁘게 sql문을 로그로 보여줍니다.  
 
# User 테이블 생성
**User**
```
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;  
	
	@Column(nullable = false)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String name;
	
	private String role;

}
```
**@NoArgsConstructor** : Lombok 어노테이션으로 빈 생성자를 만들어줍니다.  
**@Entity** : 해당 클래스가 엔티티를 위한 클래스이며, 해당 클래스의 인스턴스들이 JPA로 관리되는 엔티티 객체라는 것을 의미합니다. 즉, 테이블을 의미합니다.  
디폴트값으로 클래스의 카멜케이스 이름을 언더스코어 네이밍(_)으로 테이블 이름을 매칭합니다.  
**@Id** : 테이블의 Primary Key(PK)  
**@GeneratedValue(strategy = GenerationType.IDENTITY)** : PK를 자동으로 생성하고자 할 때 사용합니다. 즉, auto_increment를 말합니다. 여기서는 JPA의 넘버링 전략이 아닌 이 전략을 사용합니다. (전에 application.yml 설정에서 use-new-id-generate-mappings: false로 한 것이 있습니다.)  
**@Column** : 해당 필드가 컬럼이라는 것을 말하고, @Column에는 다양한 속성을 지정할 수 있습니다. (nullable = false: null값이 되면 안된다!, length = 50: 길이 제한 등등)
**@Enumerated(EnumType.STRING)** : JPA로 DB에 저장할 때 Enum 값을 어떤 형태로 저장할지를 결정합니다.  
기본적으로는 int로 저장하지만 int로 저장하면 무슨 의미인지 알 수가 없기 때문에 문자열로 저장될 수 있도록 설정합니다.  
User 클래스 Setter가 없는 이유는 이 setter를 무작정 생성하게 되면 해당 클래스의 인스턴스가 언제 어디서 변해야하는지 코드상으로는 명확하게 알 수가 없어 나중에는 변경시에 매우 복잡해집니다.  
**Builder**를 사용하는 이유는 어느 필드에 어떤 값을 채워야하는지 명확하게 알 수 있기 때문에 실수가 나지 않습니다.    

# Security 회원가입  
**signup.html**
```
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="layout/header :: header" />
<body>
	<div class="container">
		<div th:replace="layout/bodyHeader :: bodyHeader" />
		<form th:action="@{/signupJoin}" method="POST">
		<div class="form-group">
				<label>아이디</label> 
				<input type="text" name="username" class="form-control" placeholder="아이디를 입력하세요" autocomplete="off" required>
			</div>
			<div class="form-group">
				<label>패스워드</label> 
				<input type="password" name="password" class="form-control" placeholder="패스워드를 입력하세요" autocomplete="off" required>
			</div>
			<div class="form-group">
				<label>이메일</label> 
				<input type="email" name="email" class="form-control" placeholder="이메일를 입력하세요" autocomplete="off" required>
			</div>
			<div class="form-group">
				<label>이름</label> 
				<input type="text" name="name" class="form-control" placeholder="이름을 입력하세요" autocomplete="off" required>
			</div>
			<button type="submit" class="btn btn-primary">회원가입</button>
		</form>
		<br />
		<div th:replace="layout/footer :: footer" />
	</div>
</body>
</html>
```
<form **th:action**="@{/signupJoin}" method="POST"> : th:action을 사용하면 csrf토큰이 자동으로 추가된다.  

**UserRepository**  
```
public interface UserRepository extends JpaRepository<User, Long>{
	
	// findBy 규칙 -> UserId 문법
	// ex) select * from user where userId = ?
	public User findByUsername(String username);

}
```
CRUD 함수를 JPARepository가 들고 있고 @Repository라는 어노테이션이 없어도 loc됩니다. 이유는 JpaRepositori를 상속했기 때문에..  

**SecurityConfig**  
```
@Bean
	public BCryptPasswordEncoder encoderpwd() {
		return new BCryptPasswordEncoder();
	}
```
해당 메서드의 리턴되는 오브젝트를 loc로 등록해준다.

**HomeController**  
```
@GetMapping("/signup")
	public String signup() {
		return "signup";
	}
	
	@PostMapping("/signupJoin")
	public String signupJoin(User user) {
		user.setRole("ROLE_USER");
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		userRepository.save(user);
		return "redirect:login";
	}
```
**String rawPassword = user.getPassword();  
String encPassword = bCryptPasswordEncoder.encode(rawPassword);  
user.setPassword(encPassword); :** 암호화를 하기 위해서는 Spring-security에서 제공하는 BCryptPasswordEncoder 클래스를 이용합니다.  
BCryptPasswordEncoder 클래스 객체를 생성하고 객체를 통해 encode() 메서드를 호출하여 비밀번호를 매개값으로 넣어준 뒤 인코딩합니다.  
encode()메서드는 반환타입이 String이므로 String 타입의 변수에 저장합니다.  

# Security 로그인  
**SCRF 설정**  
Cross-site request forgery의 약자로 타사이트에서 본인의 사이트로 form 데이터를 사용하여 공격하려고 할 때, 그걸 방지하기 위해 csrf 토큰 값을 사용하는 것이다.  
타임리프 템플릿으로 form 생성시 타임리프, 스프링 MVC, 스프링 시큐리티가 조합이 되어 자동으로 csrf 토큰 기능을 지원해준다.
[참고](https://wiken.io/ken/957)  

**CORS**  
Cross-Origin Resource Sharing,CORS의 약자로 다른 출처의 자원을 공유할 수 있도록 설정하는 권한 체제를 말합니다.
[참고](https://valuefactory.tistory.com/1141)  

**login.html**  
```
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="layout/header :: header" />
<body>
	<div class="container">
		<div th:replace="layout/bodyHeader :: bodyHeader" />
		<form action="/login" method="POST">
		<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
		<div class="form-group">
				<label>아이디</label> 
				<input type="text" name="username" class="form-control" placeholder="아이디를 입력하세요" autocomplete="off" required>
			</div>
			<div class="form-group">
				<label>패스워드</label> 
				<input type="password" name="password" class="form-control" placeholder="패스워드를 입력하세요" autocomplete="off" required>
			</div>
			<button type="submit" class="btn btn-primary">로그인</button>
		</form>
		<br />
		<div th:replace="layout/footer :: footer" />
	</div>
</body>
</html>
```
```<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />```: 이렇게 해줘도 토큰값을 받을 수 있다.  

**SecurityConfig**  
```
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Bean
	public BCryptPasswordEncoder encoderpwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception{
		http.authorizeRequests()
			.antMatchers("/index").authenticated()
			.antMatchers("/user/home").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
			.antMatchers("/admin/userList").access("hasRole('ROLE_ADMIN')")
			.anyRequest().permitAll()
			.and()  
			.formLogin()
			.loginPage("/login")
			.loginProcessingUrl("/login") 
			.defaultSuccessUrl("/user/home");
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
```
// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행 시킨다.
// 로그인을 진행이 완료가 되면 시큐리티 session을 만들어줍니다.(Security ContextHolder)
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
```

**PrincipalDetalisService**  
```
// 시큐리티 설정에서 loginProcessingUrl("/login");
// login 요청이 오면 자동으로 UserDatailisService 타입으로 loC 되어 있는 loadUserByUser 함수 실행
@Service
public class PrincipalDetalisService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userEntity = userRepository.findByUsername(username);
		
		if (userEntity == null) {
			return null;
		} else {
			return new PrincipalDetails(userEntity);
		}
	}
}
```
처음에 userId로 했는데 값이 들어가지 않아 오류가 났지만, username으로 변경하니 잘 작동되었다.  
정확한 이유는 모르겠지만 username으로 고정으로 사용해야 겠다.. 다음 또 시큐리티를 사용하게 되면 다시 시도 해봐야겠다.  

# 회원목록
```
http.authorizeRequests()  //권한 
    .antMatchers("/admin/userList").access("hasRole('ROLE_ADMIN')");
```
시큐리티에 ADMIN 만 볼 수 있게 주소를 설정

```
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
						<td th:text="${user.name}"></td>
						<td th:text="${user.createDate}"></td>
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
```
@GetMapping(value = "/admin/userList")
	public String userList(Model model) {
		List<User> user = userService.findUser();
		model.addAttribute("user", user);
		return "admin/userList";
	}
```
JpaRepository에 기본으로 재공하는 findAll() 사용하여 회원 정보를 가져옴.
