# 목차

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
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

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

**Role**
```
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

# Security 설정
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
		http.csrf().disable();
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
**csrf()** : 공격을 방지하는 기능을 지원  
**authorizeRequests()** : URL별 권환 관리를 설정하는 옵션  

