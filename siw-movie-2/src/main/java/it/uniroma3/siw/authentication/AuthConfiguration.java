package it.uniroma3.siw.authentication;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class AuthConfiguration{

	@Autowired
	DataSource dataSource;
	
	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http
		.csrf().and().cors().disable()
		//Chi accede a cosa?
		.authorizeHttpRequests()
		//chiunque (autenticato o no) può accedere alle pagine:
		//index, login, register, ai css e alle immagini
		.requestMatchers(HttpMethod.GET, "/", "/artists", "/moviesList", "/index", "/login", "/register", "/**","favicon.ico", "/movie-photos", "/artist-photos").permitAll()
		//chiunque (autenticato o no) può mandare richieste POST agli url per login e register
		.requestMatchers(HttpMethod.POST, "/login", "/register").permitAll()
		//solo gli utenti autenticati con ruolo ADMIN possono accedere a risorse con path /admin/**
		.requestMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(ADMIN_ROLE)
		.requestMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(ADMIN_ROLE)
		//tutti gli utenti autenticati possono accedere alle pagine rimanenti
		.anyRequest().authenticated().and().exceptionHandling().accessDeniedPage("/index")
		.and().formLogin()
		.loginPage("/login")
		.defaultSuccessUrl("/success", true)
		
		.and()
		.logout()
		.logoutUrl("/logout")
		.logoutSuccessUrl("/")
		.invalidateHttpSession(true)
		.deleteCookies("JSESSIONID")
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		.clearAuthentication(true).permitAll();
		
		return http.build();
	}
	
	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(this.dataSource)
		//recuperiamo username e ruolo
		.authoritiesByUsernameQuery("SELECT username, role " + "FROM credentials WHERE username=?")
		//query per username e password
		.usersByUsernameQuery("SELECT username, password, 1 as enabled " + "FROM credentials WHERE username=?");
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
		return authenticationConfiguration.getAuthenticationManager();
	}
}
