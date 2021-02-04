package ces.riccico.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


//prePostEnabled = true use 2 annotation @PreAuthorize and @PostAuthorize 
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtRequestFilter jwtRequestFilter;
	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		// don't need CSRF for this project
		  http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
          .csrf().disable();
	}
}