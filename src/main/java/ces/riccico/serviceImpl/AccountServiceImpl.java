
package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ces.riccico.entities.Accounts;
import ces.riccico.models.LoginModel;
import ces.riccico.models.Message;
import ces.riccico.models.Role;
import ces.riccico.entities.Token;
import ces.riccico.entities.Users;
import ces.riccico.notification.Notification;
import ces.riccico.notification.UserNotification;

import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.UserRepository;
import ces.riccico.security.AccountDetail;
import ces.riccico.service.AccountService;
import ces.riccico.security.JwtUtil;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.TokenService;
import ces.riccico.validation.Validation;

@Service
public class AccountServiceImpl implements AccountService {
	private static final boolean IsBanned = true;
	private static final boolean IsNotBanned = false;


	public static int confirmCode;

	@Autowired
	private UserRepository userRepository;

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
	
	@Autowired
	private ModelMapper mapper;

	@Override
	public AccountDetail loadUserByUsername(String username) {
		Accounts account = accountRepository.findByUsername(username);
		AccountDetail accountDetail =  mapper.map(account, AccountDetail.class);
		if (account == null) {
			return null;
		} else {
			Set<String> authorities = new HashSet<>();
			if (account.getRole() != null) {
				authorities.add(account.getRole());
			}
			accountDetail.setIdUser(account.getIdAccount());
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
					Accounts accountNew = mapper.map(account, Accounts.class);
					accountNew.setRole(Role.USER.getRole());
					accountNew.setBanned(false);
					accountNew.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));
					accountNew.setActive(false);
					user.setAccount(accountNew);
					accountRepository.saveAndFlush(accountNew);
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
		Message message = new Message();
		try {
			String usernameOrEmail = account.getUsernameOrEmail();
			String password = account.getPassword();
			if (usernameOrEmail == null || usernameOrEmail.isEmpty()) {
				message.setMessage(UserNotification.usernameNull);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else if (password == null || password.isEmpty()) {
				message.setMessage(UserNotification.passwordNull);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else {
				if (usernameOrEmail.matches(Validation.EMAIL_PATTERN)) {
					if (accountRepository.findByEmail(usernameOrEmail) == null) {
						message.setMessage(UserNotification.emailNotExists);
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
					}
					usernameOrEmail = accountRepository.findByEmail(usernameOrEmail).getUsername();
				}
				AccountDetail accountDetail = loadUserByUsername(usernameOrEmail);
				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				if (accountRepository.findByUsername(usernameOrEmail) == null) {
					message.setMessage(UserNotification.accountNotExist);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else {
					if (!encoder.matches(account.getPassword(), accountDetail.getPassword())) {
						message.setMessage(UserNotification.invalidAccount);
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
					} else if (accountRepository.findByUsername(usernameOrEmail).isBanned()) {
						message.setMessage(UserNotification.isBanned);
						return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
					} else if (!accountRepository.findByUsername(usernameOrEmail).isActive()) {
						message.setMessage(UserNotification.notActivated);
						return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
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
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public ResponseEntity<?> logout() {
		Message message = new Message();
		try {
			Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
			Accounts account = accountRepository.findById(idCurrent).get();
			if (account == null) {
				message.setMessage(UserNotification.accountNotExist);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			} else {
				List<Token> listToken = tokenService.getAll();
				for (Token token : listToken) {
					if (idCurrent.equals(jwtUtil.getUserFromToken(token.getToken()).getIdUser())) {
						tokenService.deleteById(token.getId());
					}
				}
				message.setMessage(Notification.success);
				return ResponseEntity.ok(message);
			}
		} catch (Exception e) {
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public ResponseEntity<?> changePassword(String oldPassword, String newPassword) {
		Message message = new Message();
		try {
			Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
			Accounts account = accountRepository.findById(idCurrent).get();
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			if (account == null) {
				message.setMessage(UserNotification.accountNotExist);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			} else {
				if (oldPassword == null || oldPassword.isEmpty()) {
					message.setMessage(UserNotification.passwordNull);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (newPassword == null || newPassword.isEmpty()) {
					message.setMessage(UserNotification.passwordNewNull);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (!encoder.matches(oldPassword, account.getPassword())) {
					message.setMessage(UserNotification.wrongOldPassword);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (!newPassword.matches(Validation.PASSWORD_PATTERN)) {
					message.setMessage(UserNotification.invalidPasswordFormat);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (newPassword.equals(oldPassword)) {
					message.setMessage(UserNotification.isMactchingOldPassword);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else {
					message.setMessage(Notification.success);
					account.setPassword(encoder.encode(newPassword));
					accountRepository.saveAndFlush(account);
					return ResponseEntity.ok(message);
				}
			}
		} catch (Exception e) {
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public Optional<Accounts> findById(int id) {
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
	public ResponseEntity<?> activeAccount(int codeInput, String email) {
		Message message = new Message();
		try {
			Accounts account = accountRepository.findByEmail(email);

			if (account == null) {
				message.setMessage(UserNotification.emailNotExists);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else if (codeInput != confirmCode) {
				message.setMessage(UserNotification.invalidCode);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else {
				account.setActive(true);
				accountRepository.saveAndFlush(account);
				message.setMessage(Notification.success);
				return ResponseEntity.ok(message);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		message.setMessage(Notification.fail);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}

	@Override
	public ResponseEntity<?> banAccount(int idAccount) {
		Message message = new Message();
		try {
			Accounts account = accountRepository.findById(idAccount).get();
			if (account.isBanned() == IsBanned) {
				message.setMessage(UserNotification.isBanned);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else {
				account.setBanned(IsBanned);
				accountRepository.saveAndFlush(account);
			}
		} catch (Exception e) {
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
		message.setMessage(Notification.success);
		return ResponseEntity.ok(message);
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
		Message message = new Message();
		Accounts accounts = accountRepository.findByEmail(email);
		try {
			if (email == null) {
				message.setMessage(UserNotification.emailNull);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else if (!email.matches(Validation.EMAIL_PATTERN)) {
				message.setMessage(UserNotification.invalidEmailFormat);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else if (!accounts.getEmail().isEmpty() && accounts.isActive()) {
				SimpleMailMessage messageEmail = new SimpleMailMessage();
				messageEmail.setTo(accounts.getEmail());
				messageEmail.setSubject("Reset Password");
				messageEmail
						.setText("Wellcome " + accounts.getEmail() + "\nYour Password is: " + accounts.getPassword());
				try {
					sender.send(messageEmail);
				} catch (Exception e) {
					System.out.println("createNewServices: " + e);
				}
			} else {
				message.setMessage(UserNotification.emailNotExists);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
		} catch (Exception e) {
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
		message.setMessage(Notification.success);
		return ResponseEntity.ok(message);
	}

	@Override
	public ResponseEntity<?> resetPassword(String email, String password) {
		Message message = new Message();
		Accounts account = accountRepository.findByEmail(email);
		try {
			if (email == null) {
				message.setMessage(UserNotification.emailNull);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else if (!email.matches(Validation.EMAIL_PATTERN)) {
				message.setMessage(UserNotification.invalidEmailFormat);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else if (password == null) {
				message.setMessage(UserNotification.passwordNull);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else if (!password.matches(Validation.PASSWORD_PATTERN)) {
				message.setMessage(UserNotification.invalidPasswordFormat);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else if (!account.getEmail().isEmpty() && account.isActive()) {
				account.setPassword(new BCryptPasswordEncoder().encode(password));
				accountRepository.saveAndFlush(account);
				message.setMessage(Notification.success);
				return ResponseEntity.ok(message);
			} else {
				message.setMessage(UserNotification.emailNotExists);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
		} catch (Exception e) {
			message.setMessage(UserNotification.emailNotExists);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}
}
