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
<<<<<<< HEAD
import ces.riccico.models.Token;
=======
import ces.riccico.models.Roles;
>>>>>>> 5829613 (update register and setup validation register)
import ces.riccico.repository.AccountRepository;
import ces.riccico.security.AccountDetail;
import ces.riccico.service.AccountService;
import ces.riccico.security.JwtUtil;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.TokenService;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	AccountRepository accountRepository;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private SecurityAuditorAware securityAuditorAware;

	@Override
	public AccountDetail loadUserByUsername(String username) {
		Accounts account = accountRepository.findByUserName(username);
		AccountDetail accountDetail = new AccountDetail();
		if (account == null) {
			return null;
		} else {
			Set<String> authorities = new HashSet<>();
			if (account.getRole() != null) {
				authorities.add(account.getRole().getRoleName());
			}
			accountDetail.setIdUser(account.getIdAccount());
			accountDetail.setUsername(account.getUserName());
			accountDetail.setPassword(account.getPassWord());
			accountDetail.setAuthorities(authorities);
		}
		return accountDetail;
	}

	@Override
	public ResponseEntity<?> login(Accounts account) {
		if (account.getUserName() == null || account.getUserName().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please enter your username");
		}
		if (account.getPassWord() == null || account.getPassWord().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please enter your password");
		}
		AccountDetail accountDetail = loadUserByUsername(account.getUserName());
//	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//	if (accountRepository.findByUsername(account.getUsername()) == null
//			|| !encoder.matches(account.getPassword(), accountDetail.getPassword())) {
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password is wrong");
//	}
		if (account == null || !account.getPassWord().equals(accountDetail.getPassword())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password is wrong");
		}
		if (accountRepository.findByUserName(account.getUserName()).isBanded()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Your account is banned");
		}
		Token token = new Token();
		token.setToken(jwtUtil.generateToken(accountDetail));
		token.setTokenExpDate(jwtUtil.generateExpirationDate());
		tokenService.save(token);
		return ResponseEntity.ok(token);
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
	public List<Accounts> findAllById(Iterable<String> ids) {
		return (List<Accounts>) accountRepository.findAllById(ids);
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
		return accountRepository.findByUserName(username);
	}

<<<<<<< HEAD
=======
	@Override
	public List<Accounts> findByUsername(String username) {
		return ar.findByUsername(username);
	}

	
	
>>>>>>> 5829613 (update register and setup validation register)
}

