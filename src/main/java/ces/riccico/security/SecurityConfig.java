
package ces.riccico.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ces.riccico.common.enums.Role;

//prePostEnabled = true use 2 annotation @PreAuthorize and @PostAuthorize 
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
@EnableScheduling
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final String ROLE_ADMIN = Role.ADMIN.getRole();
	private static final String ROLE_HOST = Role.HOST.getRole();
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
		.antMatchers(HttpMethod.GET, "/userDetail/**").hasAnyAuthority(ROLE_USER, ROLE_ADMIN, ROLE_HOST)
		.antMatchers(HttpMethod.PUT, "/editUser/**").hasAnyAuthority(ROLE_USER, ROLE_ADMIN, ROLE_HOST)

		// account service
		.antMatchers(HttpMethod.GET,"/accounts").hasAnyAuthority(ROLE_ADMIN)
		.antMatchers(HttpMethod.GET,"/accounts/isBanned").hasAnyAuthority(ROLE_ADMIN)
		.antMatchers(HttpMethod.GET,"/statisticOwner/**").hasAnyAuthority(ROLE_HOST)
		.antMatchers(HttpMethod.PUT,"/ban/**").hasAnyAuthority(ROLE_ADMIN)
		.antMatchers(HttpMethod.PUT,"/changePassword/**").hasAnyAuthority(ROLE_USER, ROLE_ADMIN, ROLE_HOST )
		.antMatchers(HttpMethod.DELETE,"/log-out").hasAnyAuthority(ROLE_USER, ROLE_ADMIN, ROLE_HOST)

		// house service
		.antMatchers(HttpMethod.GET,"/houses/block/**").hasAnyAuthority(ROLE_ADMIN)
		.antMatchers(HttpMethod.GET, "/houses/host/**").hasAnyAuthority(ROLE_HOST,ROLE_ADMIN)
		.antMatchers(HttpMethod.PUT, "/houses/**").hasAnyAuthority(ROLE_HOST)
		.antMatchers(HttpMethod.POST,"/houses/create").hasAnyAuthority(ROLE_HOST)
		.antMatchers(HttpMethod.GET, "/houses/unlistedHouse/**").hasAnyAuthority(ROLE_HOST)
		.antMatchers(HttpMethod.DELETE, "/houses/deactiveHouse/**").hasAnyAuthority(ROLE_HOST)
		.antMatchers(HttpMethod.POST, "/registerHost").hasAnyAuthority(ROLE_USER)

		// booking service
		.antMatchers(HttpMethod.GET,"/bookings/house/**").hasAnyAuthority(ROLE_USER,ROLE_HOST)
		.antMatchers(HttpMethod.GET,"/bookings/detail/**").hasAnyAuthority(ROLE_USER, ROLE_ADMIN, ROLE_HOST)
		.antMatchers(HttpMethod.GET,"/bookings/customer/**").hasAnyAuthority(ROLE_USER, ROLE_HOST)
		.antMatchers(HttpMethod.GET,"/bookings/host/**").hasAnyAuthority(ROLE_HOST)
		.antMatchers(HttpMethod.POST,"/bookings/**").hasAnyAuthority(ROLE_USER, ROLE_HOST)
		.antMatchers(HttpMethod.PUT,"/bookings/cancelBooking/**").hasAnyAuthority(ROLE_USER, ROLE_HOST)
		.antMatchers(HttpMethod.PUT,"/bookings/payment/**").hasAnyAuthority(ROLE_USER, ROLE_HOST)
//		.antMatchers(HttpMethod.PUT,"/bookings/revenue/**").hasAnyAuthority(ROLE_ADMIN)


		// rating service
//		.antMatchers(HttpMethod.GET, "/ratings/account/**").hasAnyAuthority(ROLE_USER)
		.antMatchers(HttpMethod.GET, "/ratings/detail/**").hasAnyAuthority(ROLE_USER, ROLE_HOST)
		.antMatchers(HttpMethod.POST, "/ratings/write/**").hasAnyAuthority(ROLE_USER, ROLE_HOST)
		.antMatchers(HttpMethod.PUT, "/ratings/**").hasAnyAuthority(ROLE_USER, ROLE_HOST)
		
		//Admin statistics
		.antMatchers(HttpMethod.GET, "/statisticsAdmin").hasAnyAuthority(ROLE_ADMIN)
		.antMatchers(HttpMethod.GET, "/listBookingsPaid").hasAnyAuthority(ROLE_ADMIN)
		.antMatchers(HttpMethod.GET, "/revenue/**").hasAnyAuthority(ROLE_ADMIN)
		
		//permit all 
//		.antMatchers(HttpMethod.GET, "/houses/listHouse/**").permitAll().
//		 anyRequest().authenticated().
//         and().
//         anonymous().disable();

//		.and()
//		.exceptionHandling().accessDeniedPage("/403")
;


	}
}