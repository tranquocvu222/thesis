
package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jwt.JWTClaimsSet;

import ces.riccico.common.CommonConstants;
import ces.riccico.common.TokenConstants;
import ces.riccico.common.UserConstants;
import ces.riccico.entities.Account;
import ces.riccico.entities.House;
import ces.riccico.models.HouseModel;
import ces.riccico.models.LoginModel;
import ces.riccico.models.Message;
import ces.riccico.models.PaginationModel;
import ces.riccico.models.Role;
import ces.riccico.entities.Token;
import ces.riccico.entities.User;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.TokenRepository;
import ces.riccico.repository.UserRepository;
import ces.riccico.security.AccountDetail;
import ces.riccico.service.AccountService;
import ces.riccico.security.JwtUtil;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.validation.Validation;

@Service
public class AccountServiceImpl implements AccountService {

	public static int CONFIRM_CODE;

	private static final boolean IS_BANNED = true;

	private static final boolean IS_NOT_BANNED = false;

	private static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private SecurityAuditorAware securityAuditorAware;

	@Autowired
	private JavaMailSender sender;

	@Autowired
	private UserRepository userRepository;

//	Active account by email
	@Override
	public ResponseEntity<?> activeAccount(int codeInput, String email) {

		Message message = new Message();

		try {
			Account account = accountRepository.findByEmail(email);

			if (account == null) {
				message.setMessage(UserConstants.EMAIL_NOT_EXISTS);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
			if (codeInput != CONFIRM_CODE) {
				message.setMessage(UserConstants.INVALID_CODE);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else {
				account.setActive(true);
				accountRepository.saveAndFlush(account);
				message.setMessage(CommonConstants.SUCCESS);
				return ResponseEntity.ok(message);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		message.setMessage(CommonConstants.FAIL);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}

//	Banned account 
	@Override
	public ResponseEntity<?> banAccount(int accountId) {

		Message message = new Message();

		try {
			Account account = accountRepository.findById(accountId).get();

			if (account.isBanned() == IS_BANNED) {
				message.setMessage(UserConstants.ACCOUNT_BANNED);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else {
				account.setBanned(IS_BANNED);
				accountRepository.saveAndFlush(account);
			}
		} catch (Exception e) {
			message.setMessage(CommonConstants.FAIL);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		message.setMessage(CommonConstants.SUCCESS);

		return ResponseEntity.ok(message);
	}

//	Change password  
	@Override
	public ResponseEntity<?> changePassword(String oldPassword, String newPassword) {

		Message message = new Message();

		try {

			Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();

			Account account = accountRepository.findById(idCurrent).get();

			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

			if (account == null) {
				message.setMessage(UserConstants.ACCOUNT_NOT_EXISTS);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			} else {
				if (oldPassword == null || oldPassword.isEmpty()) {
					message.setMessage(UserConstants.PASSWORD_NULL);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} 
				if (newPassword == null || newPassword.isEmpty()) {
					message.setMessage(UserConstants.PASSWORD_NEW_NULL);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} 
				if (!encoder.matches(oldPassword, account.getPassword())) {
					message.setMessage(UserConstants.WRONG_OLD_PASSWORD);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} 
				if (!newPassword.matches(Validation.PASSWORD_PATTERN)) {
					message.setMessage(UserConstants.INVALID_PASSWORD_FORMAT);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} 
				if (newPassword.equals(oldPassword)) {
					message.setMessage(UserConstants.IS_MATCHING_OLD_PASSWORD);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else {
					message.setMessage(CommonConstants.SUCCESS);
					account.setPassword(encoder.encode(newPassword));
					accountRepository.saveAndFlush(account);
					return ResponseEntity.ok(message);
				}
			}

		} catch (Exception e) {

			message.setMessage(CommonConstants.FAIL);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

//	Show list account was banned
	@Override
	public List<Account> findAllIsBanned() {

		List<Account> listAccount = new ArrayList<Account>();

		try {

			for (Account account : accountRepository.findAll()) {
				if (account.isBanned() == IS_BANNED) {
					listAccount.add(account);
				}
			}

			return listAccount;

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return listAccount;
	}

//	Find account by idAccount
	@Override
	public Optional<Account> findById(int id) {
		return accountRepository.findById(id);
	}

//	Show list account was not banned
	@Override
	public List<Account> findAll() {

		List<Account> listAccount = new ArrayList<Account>();

		try {
			for (Account account : accountRepository.findAll()) {
				if (account.isBanned() == IS_NOT_BANNED) {
					listAccount.add(account);
				}
			}

			return listAccount;

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return listAccount;
	}
	
	@Override
	public ResponseEntity<?> findByPageAndSize(int page, int size) {
		List<Object> listAccountModel = new ArrayList<Object>();
		List<Account> listAccount = new ArrayList<Account>();
		PaginationModel paginationModel = new PaginationModel();
		Message message = new Message();
		
		try {
			Pageable paging = PageRequest.of(page, size);
			listAccount = accountRepository.findAll(paging).getContent();
			int pageMax = accountRepository.findAll(paging).getTotalPages();
			
			for (Account account : listAccount) {
				Account accountModel = mapper.map(account, Account.class);
				listAccountModel.add(accountModel);
			}
			
			paginationModel.setListHouse(listAccountModel);
			paginationModel.setPageMax(pageMax);
			return ResponseEntity.ok(paginationModel);
			
		} catch (Exception e) {
			message.setMessage(e.getLocalizedMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

//  Recover password by email when forget 
	@Override
	public ResponseEntity<?> forgetPassword(String email) {

		Message message = new Message();
		Account accounts = accountRepository.findByEmail(email);

		try {
			if (email == null) {
				message.setMessage(UserConstants.EMAIL_NULL);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			if (!email.matches(Validation.EMAIL_PATTERN)) {
				message.setMessage(UserConstants.INVALID_EMAIL_FORMAT);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			if (!accounts.getEmail().isEmpty() && accounts.isActive()) {
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
				message.setMessage(UserConstants.EMAIL_NOT_EXISTS);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

		} catch (Exception e) {
			message.setMessage(CommonConstants.FAIL);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		message.setMessage(CommonConstants.SUCCESS);
		return ResponseEntity.ok(message);
	}
	

//	Load user by username
	@Override
	public AccountDetail loadUserByUsername(String username) {

		Account account = accountRepository.findByUsername(username);
		AccountDetail accountDetail = mapper.map(account, AccountDetail.class);

		if (account == null) {
			return null;
		} else {
			Set<String> authorities = new HashSet<>();
			if (account.getRole() != null) {
				authorities.add(account.getRole());
			}
			accountDetail.setIdUser(account.getAccountId());
			accountDetail.setAuthorities(authorities);
		}

		return accountDetail;
	}

//	Login by username or email and password
	@Override
	public ResponseEntity<?> login(LoginModel account) {

		Message message = new Message();

		try {

			String usernameOrEmail = account.getUsernameOrEmail();
			String password = account.getPassword();

			if (usernameOrEmail == null || usernameOrEmail.isEmpty()) {
				message.setMessage(UserConstants.USERNAME_NULL);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
			if (password == null || password.isEmpty()) {
				message.setMessage(UserConstants.PASSWORD_NULL);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else {
				if (usernameOrEmail.matches(Validation.EMAIL_PATTERN)) {
					if (accountRepository.findByEmail(usernameOrEmail) == null) {
						message.setMessage(UserConstants.EMAIL_NOT_EXISTS);
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
					}
					usernameOrEmail = accountRepository.findByEmail(usernameOrEmail).getUsername();
				}

				AccountDetail accountDetail = loadUserByUsername(usernameOrEmail);

				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

				if (accountRepository.findByUsername(usernameOrEmail) == null) {
					message.setMessage(UserConstants.ACCOUNT_NOT_EXISTS);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else {
					if (!encoder.matches(account.getPassword(), accountDetail.getPassword())) {
						message.setMessage(UserConstants.INVALID_ACCOUNT);
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
					} 
					if (accountRepository.findByUsername(usernameOrEmail).isBanned()) {
						message.setMessage(UserConstants.ACCOUNT_BANNED);
						return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
					} 
					if (!accountRepository.findByUsername(usernameOrEmail).isActive()) {
						message.setMessage(UserConstants.NOT_ACTIVATED);
						return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
					} else {
						Token token = new Token();
						token.setToken(jwtUtil.generateToken(accountDetail));
						token.setTokenExpDate(jwtUtil.generateExpirationDate());
						tokenRepository.saveAndFlush(token);
						return ResponseEntity.ok(token.getToken());
					}
				}
			}

		} catch (Exception e) {
			message.setMessage(CommonConstants.FAIL);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

//	Logout account
	@Override
	public ResponseEntity<?> logout() {

		Message message = new Message();
		String tokenCurrent;

		try {
			tokenCurrent = jwtUtil.getJwtTokenHeader();
			JWTClaimsSet claims = jwtUtil.getClaimsFromToken(tokenCurrent);
			Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
			Account account = accountRepository.findById(idCurrent).get();

			if (tokenRepository.findByToken(tokenCurrent) == null) {
				message.setMessage(TokenConstants.TOKEN_NOT_EXIST);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			}
			
			if (!jwtUtil.isTokenExpired(claims)) {
				message.setMessage(TokenConstants.IS_TOKEN_EXPIRED);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			}
			
			if (!jwtUtil.validateToken(tokenCurrent, account)) {
				message.setMessage(TokenConstants.INVALID_TOKEN);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			} else {
				Token token = tokenRepository.findByToken(tokenCurrent);
				tokenRepository.delete(token);
				message.setMessage(CommonConstants.SUCCESS);

				return ResponseEntity.ok(message);
			}

		} catch (Exception e) {
			message.setMessage(e.getLocalizedMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

//	Register accountby username, email, password	
	@Override
	public ResponseEntity<?> register(Account account, User user) {

		Message message = new Message();

		try {
			int code = (int) Math.floor(((Math.random() * 899999) + 100000));
			CONFIRM_CODE = code;

			if (account.getUsername().equals("")) {
				message.setMessage(UserConstants.USERNAME_NULL);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
			
			if (account.getEmail().equals("")) {
				message.setMessage(UserConstants.EMAIL_NULL);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
			
			if (account.getPassword().equals("")) {
				message.setMessage(UserConstants.PASSWORD_NULL);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
			
			if (!account.getUsername().matches(Validation.USERNAME_PATTERN)) {
				message.setMessage(UserConstants.INVALID_USERNAME_FORMAT);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
			
			if (!account.getEmail().matches(Validation.EMAIL_PATTERN)) {
				message.setMessage(UserConstants.INVALID_EMAIL_FORMAT);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
			
			if (!account.getPassword().matches(Validation.PASSWORD_PATTERN)) {
				message.setMessage(UserConstants.INVALID_PASSWORD_FORMAT);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
			
			if (accountRepository.findByEmail(account.getEmail()) != null) {
				message.setMessage(UserConstants.EMAIL_EXISTS);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
			
			if (accountRepository.findByUsername(account.getUsername()) == null) {
				try {
					SimpleMailMessage messageEmail = new SimpleMailMessage();
					messageEmail.setTo(account.getEmail());
					messageEmail.setSubject("Verification Code");
					messageEmail.setText("Wellcome " + account.getEmail() + "\nYour Verification Code is: " + code
							+ "\nPlease enter code on website to complete register");
					sender.send(messageEmail);

					Account accountNew = mapper.map(account, Account.class);
					accountNew.setRole(Role.USER.getRole());
					accountNew.setBanned(false);
					accountNew.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));
					accountNew.setActive(false);
					user.setAccount(accountNew);
					accountRepository.saveAndFlush(accountNew);
					userRepository.saveAndFlush(user);
					message.setMessage(CommonConstants.SUCCESS);
					return ResponseEntity.ok(message);

				} catch (Exception e) {

					System.out.println("createNewServices: " + e);
				}

				message.setMessage(CommonConstants.FAIL);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);

			} else {
				message.setMessage(UserConstants.USERNAME_EXISTS);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

		} catch (Exception e) {

			message.setMessage(CommonConstants.FAIL);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

//	Reset password when forgot password
	@Override
	public ResponseEntity<?> resetPassword(String email, String password) {

		Message message = new Message();
		Account account = accountRepository.findByEmail(email);

		try {
			if (email == null) {
				message.setMessage(UserConstants.EMAIL_NULL);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
			
			if (!email.matches(Validation.EMAIL_PATTERN)) {
				message.setMessage(UserConstants.INVALID_EMAIL_FORMAT);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
			
			if (password == null) {
				message.setMessage(UserConstants.PASSWORD_NULL);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
			
			if (!password.matches(Validation.PASSWORD_PATTERN)) {
				message.setMessage(UserConstants.INVALID_PASSWORD_FORMAT);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
			
			if (!account.getEmail().isEmpty() && account.isActive()) {
				account.setPassword(new BCryptPasswordEncoder().encode(password));
				accountRepository.saveAndFlush(account);
				message.setMessage(CommonConstants.SUCCESS);
				return ResponseEntity.ok(message);

			} else {
				message.setMessage(UserConstants.EMAIL_NOT_EXISTS);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

		} catch (Exception e) {

			message.setMessage(UserConstants.EMAIL_NOT_EXISTS);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

}
