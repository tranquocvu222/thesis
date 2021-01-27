package ces.riccico.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ces.riccico.models.Accounts;
import ces.riccico.models.Token;
import ces.riccico.repository.AccountRepository;
import ces.riccico.security.AccountDetail;
import ces.riccico.service.AccountService;
import ces.riccico.security.JwtUtil;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.TokenService;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TokenService tokenService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private SecurityAuditorAware securityAuditorAware;

	@Override
	public AccountDetail loadUserByUsername(String username) {
		Accounts account = accountRepository.findByUsername(username);
		AccountDetail accountDetail = new AccountDetail();
		if (account == null) {
			return null;
		} else {
			Set<String> authorities = new HashSet<>();
			if (account.getRole() != null) {
				authorities.add(account.getRole().getRoleName());
			}
			accountDetail.setIdUser(account.getIdAccount());
			accountDetail.setUsername(account.getUsername());
			accountDetail.setPassword(account.getPassword());
			accountDetail.setAuthorities(authorities);
		}
		return accountDetail;
	}

	@Override
	public ResponseEntity<?> login(Accounts account) {
		if (account.getUsername() == null || account.getUsername().isEmpty()) {
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is empty");
		}
		if (account.getPassword() == null || account.getPassword().isEmpty()) {
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is empty");
		}
		AccountDetail accountDetail = loadUserByUsername(account.getUsername());
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//		if (accountRepository.findByUsername(account.getUsername()) == null
//				|| !encoder.matches(account.getPassword(), accountDetail.getPassword())) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password is wrong");
//		}
		if(accountRepository.findByUsername(account.getUsername()) == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password is wrong");
		}
		if (accountRepository.findByUsername(account.getUsername()).isBanded()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Your account is banned");
		}
		Token token = new Token();
		token.setToken(jwtUtil.generateToken(accountDetail));
		token.setTokenExpDate(jwtUtil.generateExpirationDate());
		tokenService.save(token);
		return ResponseEntity.ok(token.getToken());
	}

	@Override
	public List<Accounts> getAll() {
		return accountRepository.findAll();
	}

}
