package ces.riccico.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ces.riccico.models.Accounts;
import ces.riccico.models.LoginModel;
import ces.riccico.models.Token;
import ces.riccico.notification.UserNotification;

import ces.riccico.repository.AccountRepository;
import ces.riccico.security.AccountDetail;
import ces.riccico.service.AccountService;
import ces.riccico.security.JwtUtil;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.TokenService;
import ces.riccico.validation.Validation;

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
			accountDetail.setRole(account.getRole().getRoleName());
			accountDetail.setEmail(account.getEmail());
			accountDetail.setAuthorities(authorities);
		}
		return accountDetail;
	}

	@Override
	public ResponseEntity<?> login(LoginModel account) {
		try {
			String usernameOrEmail = account.getUsernameOrEmail();
			String password = account.getPassword();
			if (usernameOrEmail == null || usernameOrEmail.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.usernameNull);
			} else if (password == null || password.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.passwordNull);
			} else {
				if (!usernameOrEmail.matches(Validation.USERNAME_PATTERN)) {
					if (accountRepository.findByEmail(usernameOrEmail) == null) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailNotExists);
					}
					usernameOrEmail = accountRepository.findByEmail(usernameOrEmail).getUsername();
				}
				AccountDetail accountDetail = loadUserByUsername(usernameOrEmail);
				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				if (accountRepository.findByUsername(usernameOrEmail) == null
						|| !encoder.matches(account.getPassword(), accountDetail.getPassword())) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.invalidAccount);
				} else if (accountRepository.findByUsername(usernameOrEmail).isBanded()) {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UserNotification.isBanned);
				} else if (!accountRepository.findByUsername(usernameOrEmail).isActive()) {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UserNotification.notActivated);
				} else {
					Token token = new Token();
					token.setToken(jwtUtil.generateToken(accountDetail));
					token.setTokenExpDate(jwtUtil.generateExpirationDate());
					tokenService.save(token);
					return ResponseEntity.ok(token.getToken());
				}
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.loginFail);
		}
	}

	@Override
	public ResponseEntity<?> logout() {
		try {
			String idCurrent = securityAuditorAware.getCurrentAuditor().get();
			if (idCurrent == null || idCurrent.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must login");
			} else {
				List<Token> listToken = tokenService.getAll();
				for (Token token : listToken) {
					if (idCurrent.equals(jwtUtil.getUserFromToken(token.getToken()).getIdUser())) {
						tokenService.deleteById(token.getId());
					}
				}
				return ResponseEntity.ok("Logout success");
			}
		} catch (Exception e) {
			return ResponseEntity.ok("Logout fail");
		}
	}

	@Override
	public Accounts save(Accounts entity) {
		return accountRepository.save(entity);
	}

	@Override
	public Optional<Accounts> findById(String id) {
		return accountRepository.findById(id);
	}

	@Override
	public List<Accounts> findAll() {
		return (List<Accounts>) accountRepository.findAll();
	}


	@Override
	public void deleteById(String id) {
		accountRepository.deleteById(id);
	}

	@Override
	public void delete(Accounts entity) {
		accountRepository.delete(entity);
	}

	@Override
	public Accounts findByUserName(String username) {
		return accountRepository.findByUsername(username);
	}

	@Override
	public Accounts findByEmail(String email) {
		return accountRepository.findByEmail(email);
	}
	
	


}
