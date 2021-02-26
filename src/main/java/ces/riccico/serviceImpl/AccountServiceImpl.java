
package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import ces.riccico.models.Message;
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

	private static final String NEW_PASSWORD = "new_password";

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
		Message message = new Message();
		try {

			int code = (int) Math.floor(((Math.random() * 899999) + 100000));
			confirmCode = code;
			if (account.getUsername().equals("")) {
				message.setMessage(UserNotification.usernameNull);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else if (account.getEmail().equals("")) {
				message.setMessage(UserNotification.emailNull);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else if (account.getPassword().equals("")) {
				message.setMessage(UserNotification.passwordNull);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else if (!account.getUsername().matches(Validation.USERNAME_PATTERN)) {
				message.setMessage(UserNotification.invalidUsernameFormat);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else if (!account.getEmail().matches(Validation.EMAIL_PATTERN)) {
				message.setMessage(UserNotification.invalidEmailFormat);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else if (!account.getPassword().matches(Validation.PASSWORD_PATTERN)) {
				message.setMessage(UserNotification.invalidPasswordFormat);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else if (accountRepository.findByEmail(account.getEmail()) != null) {
				message.setMessage(UserNotification.emailExists);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else if (accountRepository.findByUsername(account.getUsername()) == null) {
				try {
					SimpleMailMessage messageEmail = new SimpleMailMessage();
					messageEmail.setTo(account.getEmail());
					messageEmail.setSubject("Verification Code");
					messageEmail.setText("Wellcome " + account.getEmail() + "\nYour Verification Code is: " + code

							+ "\nPlease enter code on website to complete register");
					sender.send(messageEmail);
					UUID uuid = UUID.randomUUID();
					account.setRole(roleRepository.findByRolename(ROLE_USER));
					account.setBanned(false);
					account.setIdAccount(String.valueOf(uuid));
					account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));
					account.setActive(false);
					user.setAccount(account);
					accountRepository.saveAndFlush(account);
					userRepository.saveAndFlush(user);
					message.setMessage(Notification.success);
					return ResponseEntity.ok(message);
				} catch (Exception e) {
					System.out.println("createNewServices: " + e);
				}
				message.setMessage(Notification.fail);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else {
				message.setMessage(UserNotification.usernameExists);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
		} catch (Exception e) {
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public ResponseEntity<?> login(LoginModel account) {
		try {
			String usernameOrEmail = account.getUsernameOrEmail();
			String password = account.getPassword();
			if (usernameOrEmail == null || usernameOrEmail.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, UserNotification.usernameNull));
			} else if (password == null || password.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, UserNotification.passwordNull));
			} else {
				if (usernameOrEmail.matches(Validation.EMAIL_PATTERN)) {
					if (accountRepository.findByEmail(usernameOrEmail) == null) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body(Map.of(Notification.message, UserNotification.emailNotExists));
					}
					usernameOrEmail = accountRepository.findByEmail(usernameOrEmail).getUsername();
				}
				AccountDetail accountDetail = loadUserByUsername(usernameOrEmail);
				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				if (accountRepository.findByUsername(usernameOrEmail) == null) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(Map.of(Notification.message, UserNotification.accountNotExist));
				} else {
					if (!encoder.matches(account.getPassword(), accountDetail.getPassword())) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body(Map.of(Notification.message, UserNotification.invalidAccount));
					} else if (accountRepository.findByUsername(usernameOrEmail).isBanned()) {
						return ResponseEntity.status(HttpStatus.FORBIDDEN)
								.body(Map.of(Notification.message, UserNotification.isBanned));
					} else if (!accountRepository.findByUsername(usernameOrEmail).isActive()) {
						return ResponseEntity.status(HttpStatus.FORBIDDEN)
								.body(Map.of(Notification.message, UserNotification.notActivated));
					} else {
						Token token = new Token();
						token.setToken(jwtUtil.generateToken(accountDetail));
						token.setTokenExpDate(jwtUtil.generateExpirationDate());
						tokenService.save(token);
						return ResponseEntity.ok(token.getToken());
					}
				}
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message, Notification.fail));
		}
	}

	@Override
	public ResponseEntity<?> logout() {
		try {
			String idCurrent = securityAuditorAware.getCurrentAuditor().get();
			if (idCurrent == null || idCurrent.isEmpty()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(Map.of(Notification.message, Notification.loginRequired));
			} else {
				List<Token> listToken = tokenService.getAll();
				for (Token token : listToken) {
					if (idCurrent.equals(jwtUtil.getUserFromToken(token.getToken()).getIdUser())) {
						tokenService.deleteById(token.getId());
					}
				}
				return ResponseEntity.ok(Map.of(Notification.message, Notification.success));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message, Notification.fail));
		}
	}

	@Override
	public ResponseEntity<?> changePassword(String oldPassword, String newPassword) {
		try {
			String idCurrent = securityAuditorAware.getCurrentAuditor().get();
			Accounts account = accountRepository.findById(idCurrent).get();
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			if (idCurrent == null || idCurrent.isEmpty()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(Map.of(Notification.message, Notification.loginRequired));
			} else {
				if (oldPassword == null || oldPassword.isEmpty()) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(Map.of(Notification.message, UserNotification.passwordNull));
				} else if (newPassword == null || newPassword.isEmpty()) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(Map.of(Notification.message, UserNotification.passwordNewNull));
				} else if (!encoder.matches(oldPassword, account.getPassword())) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(Map.of(Notification.message, UserNotification.wrongOldPassword));
				} else if (!newPassword.matches(Validation.PASSWORD_PATTERN)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(Map.of(Notification.message, UserNotification.invalidPasswordFormat));
				} else if (newPassword.equals(oldPassword)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(Map.of(Notification.message, UserNotification.isMactchingOldPassword));
				} else {
					account.setPassword(encoder.encode(newPassword));
					accountRepository.saveAndFlush(account);
					return ResponseEntity
							.ok(Map.of(Notification.message, Notification.success, NEW_PASSWORD, newPassword));
				}
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message, Notification.fail));
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
			return null;
		}
	}

	@Override
	public ResponseEntity<?> activeAccount(int codeInput, String username) {
		Accounts account = accountRepository.findByUsername(username);
		try {
			if (codeInput == confirmCode) {
				account.setActive(true);
				accountRepository.saveAndFlush(account);
				return ResponseEntity.ok(Map.of(Notification.message, Notification.success));
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message, Notification.fail));
	}

	@Override
	public ResponseEntity<?> banAccount(String idAccount) {
		try {
			Accounts account = accountRepository.findById(idAccount).get();
			if (account.isBanned() == IsBanned) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, UserNotification.isBanned));
			} else {
				account.setBanned(IsBanned);
				accountRepository.saveAndFlush(account);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message, Notification.fail));
		}
		return ResponseEntity.ok(Map.of(Notification.message, Notification.success));
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
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, UserNotification.emailNull));
			} else if (!email.matches(Validation.EMAIL_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, UserNotification.invalidEmailFormat));
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
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, UserNotification.emailNotExists));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message, Notification.fail));
		}
		return ResponseEntity.ok(Map.of(Notification.message, Notification.success));
	}

	@Override
	public ResponseEntity<?> resetPassword(String email, String password) {
		Accounts account = accountRepository.findByEmail(email);
		try {
			if (email == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, UserNotification.emailNull));
			} else if (!email.matches(Validation.EMAIL_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, UserNotification.invalidEmailFormat));
			} else if (password == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, UserNotification.passwordNull));
			} else if (!password.matches(Validation.PASSWORD_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, UserNotification.invalidPasswordFormat));
			} else if (!account.getEmail().isEmpty() && account.isActive()) {
				account.setPassword(new BCryptPasswordEncoder().encode(password));
				accountRepository.saveAndFlush(account);
				return ResponseEntity.ok(Map.of(Notification.message, Notification.success));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, UserNotification.emailNotExists));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of(Notification.message, UserNotification.emailNotExists));
		}
	}
}
