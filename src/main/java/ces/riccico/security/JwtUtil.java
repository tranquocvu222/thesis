package ces.riccico.security;

import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.minidev.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import ces.riccico.entities.Accounts;

@Component
public class JwtUtil {

	private static Logger logger = LoggerFactory.getLogger(JwtUtil.class);
	
	private static final long JWT_TOKEN_VALIDITY = 1728000000;
	
	private static final String SECRET = "thisisasecretkeythisisasecretkey";
	
	private static final String USER = "user";
	
	// generate token for user
	public String generateToken(AccountDetail user) {
		String token = null;
		try {
			// Define claims of the token, user, Expiration date
			JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
			builder.claim(USER, user);
			builder.expirationTime(generateExpirationDate());
			JWTClaimsSet claimsSet = builder.build();
			// Sign the JWT using the HS256 algorithm and secret key.
			SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
			JWSSigner signer = new MACSigner(SECRET.getBytes());
			signedJWT.sign(signer);
			token = signedJWT.serialize();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return token;
	}

	// generate Expiration Date
	public Date generateExpirationDate() {
		return new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY);
	}

	// for retrieving any information from token we will need the secret key
	public JWTClaimsSet getClaimsFromToken(String token) {
		JWTClaimsSet claims = null;
		try {
			SignedJWT signedJWT = SignedJWT.parse(token);
			JWSVerifier verifier = new MACVerifier(SECRET.getBytes());
			if (signedJWT.verify(verifier)) {
				claims = signedJWT.getJWTClaimsSet();
			}
		} catch (ParseException | JOSEException e) {
			logger.error(e.getMessage());
		}
		return claims;
	}

	// get accountDetail by token
	public AccountDetail getUserFromToken(String token) {
		AccountDetail user = null;
		try {
			JWTClaimsSet claims = getClaimsFromToken(token);
			if (claims != null && isTokenExpired(claims)) {
				JSONObject jsonObject = (JSONObject) claims.getClaim(USER);
				user = new ObjectMapper().readValue(jsonObject.toJSONString(), AccountDetail.class);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return user;
	}

	// retrieve expiration date from jwt token
	private Date getExpirationDateFromToken(JWTClaimsSet claims) {
		return claims != null ? claims.getExpirationTime() : new Date();
	}

	// check if the token has expired
	public boolean isTokenExpired(JWTClaimsSet claims) {
		return getExpirationDateFromToken(claims).after(new Date());
	}

	public static String getJwtTokenHeader() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
				.getHeader("Authorization").substring(6);
	}

	// validate token
	public Boolean validateToken(String token, Accounts account) {
		String username = getUserFromToken(token).getUsername();
		return (username.equals(account.getUsername()) && isTokenExpired(getClaimsFromToken(token)));
	}
}
