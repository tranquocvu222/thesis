package ces.riccico.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import ces.riccico.entities.Token;
import ces.riccico.repository.TokenRepository;



@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	public static final String AUTHORIZATION = "Authorization";
	public static final String START_TOKEN = "Token";
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private TokenRepository verificationTokenService;

	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//get Jwt from request. 
		//get the authentication header.
		try {
		final String authorizationHeader = request.getHeader(AUTHORIZATION);
		AccountDetail account = null;
		Token token = null;
		//validate the header and check the prefix
		if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(START_TOKEN)) {
			String jwt = authorizationHeader.substring(6);
			account = jwtUtil.getUserFromToken(jwt);
			token = verificationTokenService.findByToken(jwt);
		}
		if (null != account && null != token && token.getTokenExpDate().after(new Date())) {
			Set<GrantedAuthority> authorities = new HashSet<>();
			account.getAuthorities().forEach(p -> authorities.add(new SimpleGrantedAuthority((String) p)));
			// list of authorities, which has type of GrantedAuthority 
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(account, null,
					authorities);
			//setting the Authentication in the context
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			//Authenticate the account
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		}catch(Exception e){
			 logger.error("failed on set user authentication", e);
		}

		filterChain.doFilter(request, response);
	}

}