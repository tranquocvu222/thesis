package ces.riccico.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ces.riccico.common.enums.Role;

//prePostEnabled = true use 2 annotation @PreAuthorize and @PostAuthorize 
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final String ROLE_ADMIN = Role.ADMIN.getRole();
	private static final String ROLE_USER = Role.USER.getRole();

	
	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Override
	public void configure(HttpSecurity http) throws Exception {

		// don't need CSRF for this project
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class).csrf().disable();
	
		http.cors().and()
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()

		// user service
		.antMatchers(HttpMethod.GET, "/users").hasAnyAuthority(ROLE_ADMIN)
		.antMatchers(HttpMethod.GET, "/userDetail/**").hasAnyAuthority(ROLE_USER, ROLE_ADMIN)
		.antMatchers(HttpMethod.PUT, "/editUser/**").hasAnyAuthority(ROLE_USER, ROLE_ADMIN)

		// account service
		.antMatchers(HttpMethod.GET,"/accounts").hasAnyAuthority(ROLE_ADMIN)
		.antMatchers(HttpMethod.GET,"/accounts/isBanned").hasAnyAuthority(ROLE_ADMIN)
		.antMatchers(HttpMethod.PUT,"/ban/**").hasAnyAuthority(ROLE_ADMIN)
		.antMatchers(HttpMethod.PUT,"/changePassword/**").hasAnyAuthority(ROLE_USER, ROLE_ADMIN)
		.antMatchers(HttpMethod.DELETE,"/log-out").hasAnyAuthority(ROLE_USER, ROLE_ADMIN)

		// house service
		.antMatchers(HttpMethod.GET,"/houses/isDeleted").hasAnyAuthority(ROLE_ADMIN)
		.antMatchers(HttpMethod.GET,"/houses/notApproved").hasAnyAuthority(ROLE_ADMIN)
		.antMatchers(HttpMethod.GET, "/houses/getAll").hasAnyAuthority(ROLE_ADMIN)
		.antMatchers(HttpMethod.PUT, "/houses/approve/**").hasAnyAuthority(ROLE_ADMIN)
		.antMatchers(HttpMethod.PUT, "/houses/**").hasAnyAuthority(ROLE_USER)
		.antMatchers(HttpMethod.POST,"/houses/create").hasAnyAuthority(ROLE_USER)
		.antMatchers(HttpMethod.DELETE, "/houses/**").hasAnyAuthority(ROLE_USER, ROLE_ADMIN)

		// booking service
		.antMatchers(HttpMethod.GET,"/bookings/account/**").hasAnyAuthority(ROLE_USER)
		.antMatchers(HttpMethod.GET,"/bookings/house/**").hasAnyAuthority(ROLE_USER)
		.antMatchers(HttpMethod.GET, "/bookings/**").hasAnyAuthority(ROLE_USER, ROLE_ADMIN)
		.antMatchers(HttpMethod.POST, "/bookings").hasAnyAuthority(ROLE_USER)
		.antMatchers(HttpMethod.PUT,"/bookings/acceptBooking/**").hasAnyAuthority(ROLE_USER)
		.antMatchers(HttpMethod.PUT,"/bookings/cancelBooking/**").hasAnyAuthority(ROLE_USER)
		.antMatchers(HttpMethod.PUT,"/bookings/completeBooking/**").hasAnyAuthority(ROLE_USER)
		.antMatchers(HttpMethod.PUT,"/bookings/payment/**").hasAnyAuthority(ROLE_USER)

		// rating service
		.antMatchers(HttpMethod.GET, "/ratings/account/**").hasAnyAuthority(ROLE_USER)
		.antMatchers(HttpMethod.GET, "/ratings/detail/**").hasAnyAuthority(ROLE_USER)
		.antMatchers(HttpMethod.POST, "/ratings/write/**").hasAnyAuthority(ROLE_USER)
		.antMatchers(HttpMethod.PUT, "/ratings/**").hasAnyAuthority(ROLE_USER)
		
		
		//permit all 
		
//		.anyRequest().authenticated()

//		.and()
//		.exceptionHandling().accessDeniedPage("/403")
;


	}
}