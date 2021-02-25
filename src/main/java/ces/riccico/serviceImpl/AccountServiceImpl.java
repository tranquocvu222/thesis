

package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ces.riccico.models.Accounts;
import ces.riccico.models.LoginModel;

import ces.riccico.models.Token;
import ces.riccico.models.Users;
import ces.riccico.notification.Notification;
import ces.riccico.notification.UserNotification;

import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.RoleRepository;
import ces.riccico.repository.UserRepository;
import ces.riccico.security.AccountDetail;
import ces.riccico.service.AccountService;
import ces.riccico.security.JwtUtil;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.TokenService;
import ces.riccico.validation.Validation;

@Service
public class AccountServiceImpl implements AccountService {

	private static final String ROLE_USER = "user";
	private static final boolean IsBanned = true;
	private static final boolean IsNotBanned = false;

	public static int confirmCode;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private SecurityAuditorAware securityAuditorAware;

	@Autowired
	public JavaMailSender sender;

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
	public ResponseEntity<?> register(Accounts account, Users user) {
		boolean verify = true;
		 HashMap<String, String> map = new HashMap<>();
		 map.put("404", "Register Success");
		try {
			int code = (int) Math.floor(((Math.random() * 899999) + 100000));
			confirmCode = code;
			if (account.getUsername().equals("")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.usernameNull);
			} else if (account.getEmail().equals("")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailNull);
			} else if (account.getPassword().equals("")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.passwordNull);
			} else if (!account.getUsername().matches(Validation.USERNAME_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.invalidUsernameFormat);
			} else if (!account.getEmail().matches(Validation.EMAIL_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.invalidEmailFormat);
			} else if (!account.getPassword().matches(Validation.PASSWORD_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.invalidPasswordFormat);
			} else if (accountRepository.findByEmail(account.getEmail()) != null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailExists);
			} else if (accountRepository.findByUsername(account.getUsername()) == null) {
				try {
					SimpleMailMessage message = new SimpleMailMessage();
					message.setTo(account.getEmail());
					message.setSubject("Verification Code");
					message.setText("Wellcome " + account.getEmail() + "\nYour Verification Code is: " + code
							+ "\nPlease enter code on website to complete register");
					sender.send(message);
					UUID uuid = UUID.randomUUID();
					account.setRole(roleRepository.findByRolename(ROLE_USER));
					account.setBanned(false);
					account.setIdAccount(String.valueOf(uuid));
					account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));
					account.setActive(false);
					user.setAccount(account);
					accountRepository.saveAndFlush(account);
					userRepository.saveAndFlush(user);
//					return ResponseEntity.ok(verify);
					return ResponseEntity.ok(Notification.success);
				} catch (Exception e) {
					System.out.println("createNewServices: " + e);
				}
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(!verify);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.usernameExists);
			}
		} catch (Exception e) {
			System.out.println("addAccount: " + e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(!verify);
		}
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
				} else if (accountRepository.findByUsername(usernameOrEmail).isBanned()) {
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
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.fail);
		}
	}

	@Override
	public ResponseEntity<?> logout() {
		try {
			String idCurrent = securityAuditorAware.getCurrentAuditor().get();
			if (idCurrent == null || idCurrent.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.loginRequired);
			} else {
				List<Token> listToken = tokenService.getAll();
				for (Token token : listToken) {
					if (idCurrent.equals(jwtUtil.getUserFromToken(token.getToken()).getIdUser())) {
						tokenService.deleteById(token.getId());
					}
				}
				return ResponseEntity.ok(Notification.success);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.fail);
		}
	}

	@Override
	public ResponseEntity<?> changePassword(String oldPassword, String newPassword) {
		try {
			String idCurrent = securityAuditorAware.getCurrentAuditor().get();
			Accounts account = accountRepository.findById(idCurrent).get();
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			if (idCurrent == null || idCurrent.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.loginRequired);
			} else {
				if (oldPassword == null || oldPassword.isEmpty()) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.passwordNull);
				} else if (newPassword == null || newPassword.isEmpty()) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.passwordNewNull);
				} else if (!encoder.matches(oldPassword, account.getPassword())) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.wrongOldPassword);
				} else if (!newPassword.matches(Validation.PASSWORD_PATTERN)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.invalidPasswordFormat);
				} else if (newPassword.equals(oldPassword)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.isMactchingOldPassword);
				} else {
					account.setPassword(encoder.encode(newPassword));
					accountRepository.saveAndFlush(account);
					return ResponseEntity.ok(Notification.success + " " + newPassword);
				}
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.fail);
		}
	}

	@Override
	public Optional<Accounts> findById(String id) {
		return accountRepository.findById(id);
	}

	@Override
	public List<Accounts> findAll() {
		try {
			List<Accounts> listAccount = new ArrayList<Accounts>();
			for (Accounts account : accountRepository.findAll()) {
				if (account.isBanned() == IsNotBanned) {
					listAccount.add(account);
				}
			}
			return listAccount;
		} catch (Exception e) {
			System.out.println("getAll: " + e);
			return null;
		}
	}

	@Override
	public ResponseEntity<?> activeAccount(int codeInput, String email) {
		boolean verify = true;
		
		try {
			Accounts account = accountRepository.findByEmail(email);
	
			if (account == null) {
			return	ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailNotExists);  
			}else if (codeInput != confirmCode) {
				return	ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
			}else {
				account.setActive(true);
				accountRepository.saveAndFlush(account);
				return ResponseEntity.ok(verify);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(!verify);
	}

	@Override
	public ResponseEntity<?> banAccount(String idAccount) {
		try {
			Accounts account = accountRepository.findById(idAccount).get();
			if (account.isBanned() == IsBanned) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.isBanded);
			} else {
				account.setBanned(IsBanned);
				accountRepository.saveAndFlush(account);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.fail);
		}
		return ResponseEntity.ok(Notification.success);
	}

	@Override
	public List<Accounts> findAllIsBanned() {
		try {
			List<Accounts> listAccount = new ArrayList<Accounts>();
			for (Accounts account : accountRepository.findAll()) {
				if (account.isBanned() == IsBanned) {
					listAccount.add(account);
				}
			}
			return listAccount;
		} catch (Exception e) {
			System.out.println("getAll: " + e);
			return null;
		}
	}

	@Override
	public ResponseEntity<?> forgetPassword(String email) {
		Accounts accounts = accountRepository.findByEmail(email);
		try {
			if (email == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailNull);
			} else if (!email.matches(Validation.EMAIL_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.invalidEmailFormat);
			} else if (!accounts.getEmail().isEmpty() && accounts.isActive()) {
				SimpleMailMessage message = new SimpleMailMessage();
				message.setTo(accounts.getEmail());
				message.setSubject("Reset Password");
				message.setText("Wellcome " + accounts.getEmail() + "\nYour Password is: " + accounts.getPassword());
				try {
					sender.send(message);
				} catch (Exception e) {
					System.out.println("createNewServices: " + e);
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailNotExists);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.fail);
		}
		return ResponseEntity.ok(Notification.success);
	}

	@Override
	public ResponseEntity<?> resetPassword(String email, String password) {
		Accounts account = accountRepository.findByEmail(email);
		try {
			if (email == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailNull);
			} else if (!email.matches(Validation.EMAIL_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.invalidEmailFormat);
			} else if (password == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.passwordNull);
			} else if (!password.matches(Validation.PASSWORD_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.invalidPasswordFormat);
			} else if (!account.getEmail().isEmpty() && account.isActive()) {
				account.setPassword(new BCryptPasswordEncoder().encode(password));
				accountRepository.saveAndFlush(account);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailNotExists);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailNotExists);
		}
		return ResponseEntity.ok(Notification.success);
	}

}
