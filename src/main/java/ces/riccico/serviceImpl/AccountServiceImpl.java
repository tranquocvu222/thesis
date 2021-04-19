
package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

import ces.riccico.common.constants.CommonConstants;
import ces.riccico.common.constants.TokenConstants;
import ces.riccico.common.constants.UserConstants;
import ces.riccico.common.constants.Validation;
import ces.riccico.common.enums.Role;
import ces.riccico.entity.Account;
import ces.riccico.entity.Token;
import ces.riccico.entity.User;
import ces.riccico.model.LoginModel;
import ces.riccico.model.MessageModel;
import ces.riccico.model.PaginationModel;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.TokenRepository;
import ces.riccico.repository.UserRepository;
import ces.riccico.security.AccountDetail;
import ces.riccico.service.AccountService;
import ces.riccico.security.JwtUtil;
import ces.riccico.security.SecurityAuditorAware;

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

		MessageModel message = new MessageModel();
		Account account = accountRepository.findByEmail(email);

		if (account == null) {
			message.setMessage(UserConstants.EMAIL_NOT_EXISTS);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
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
	}

//	Banned account 
	@Override
	public ResponseEntity<?> banAccount(int accountId) {
		MessageModel message = new MessageModel();
		Account account = accountRepository.findById(accountId).get();

		if (account.isBanned() == IS_BANNED) {
			message.setMessage(UserConstants.ACCOUNT_BANNED);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		account.setBanned(IS_BANNED);
		accountRepository.saveAndFlush(account);
		message.setMessage(CommonConstants.SUCCESS);
		return ResponseEntity.ok(message);
	}

//	Change password  
	@Override
	public ResponseEntity<?> changePassword(String oldPassword, String newPassword) {
		MessageModel message = new MessageModel();
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
	}

//	Show list account was banned
	@Override
	public ResponseEntity<?> findAllIsBanned() {
		List<Account> listAccount = new ArrayList<Account>();
		MessageModel message = new MessageModel();

		for (Account account : accountRepository.findAll()) {
			if (account.isBanned() == IS_BANNED) {
				listAccount.add(account);
			}
		}

		if (listAccount.size() == 0) {
			message.setMessage(CommonConstants.LIST_EMPTY);
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		return ResponseEntity.ok(listAccount);
	}

//	Show list account was not banned
	@Override
	public ResponseEntity<?> findAll() {
		List<Account> listAccount = new ArrayList<Account>();
		MessageModel message = new MessageModel();

		for (Account account : accountRepository.findAll()) {
			if (account.isBanned() == IS_NOT_BANNED) {
				listAccount.add(account);
			}
		}

		if (listAccount.size() == 0) {
			message.setMessage(CommonConstants.LIST_EMPTY);
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		return ResponseEntity.ok(listAccount);
	}
	
	@Override
	public ResponseEntity<?> findByPageAndSize(int page, int size) {
		
		List<Object> listAccountModel = new ArrayList<Object>();
		List<Account> listAccount = new ArrayList<Account>();
		PaginationModel paginationModel = new PaginationModel();
		MessageModel message = new MessageModel();
		Pageable paging = PageRequest.of(page, size);
		listAccount = accountRepository.findAll(paging).getContent();
		int pageMax = accountRepository.findAll(paging).getTotalPages();

		if (listAccount.size() == 0) {
			message.setMessage(CommonConstants.LIST_EMPTY);
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		for (Account account : listAccount) {
			Account accountModel = mapper.map(account, Account.class);
			listAccountModel.add(accountModel);

		}

		paginationModel.setListObject(listAccountModel);
		paginationModel.setPageMax(pageMax);
		return ResponseEntity.ok(paginationModel);
	}


//  Recover password by email when forget 
	@Override
	public ResponseEntity<?> forgetPassword(String email) {
		
		MessageModel message = new MessageModel();
		Account account = accountRepository.findByEmail(email);
		
		if (account == null) {
			message.setMessage(UserConstants.EMAIL_NOT_EXISTS);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (account.isActive() == false) {
			message.setMessage(UserConstants.NOT_ACTIVATED);
		}

		if (!email.matches(Validation.EMAIL_PATTERN)) {
			message.setMessage(UserConstants.INVALID_EMAIL_FORMAT);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		SimpleMailMessage messageEmail = new SimpleMailMessage();
		messageEmail.setTo(account.getEmail());
		messageEmail.setSubject("Reset Password");
		messageEmail.setText("Wellcome " + account.getEmail() + "\nYour Password is: " + account.getPassword());

		try {
			sender.send(messageEmail);
		} catch (Exception e) {
			logger.error(e.getMessage());
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

		MessageModel message = new MessageModel();
		String usernameOrEmail = account.getUsernameOrEmail();
		String password = account.getPassword();

		if (usernameOrEmail == null || usernameOrEmail.isEmpty()) {
			message.setMessage(UserConstants.USERNAME_NULL);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (password == null || password.isEmpty()) {
			message.setMessage(UserConstants.PASSWORD_NULL);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

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

//	Logout account
	@Override
	public ResponseEntity<?> logout() {
		MessageModel message = new MessageModel();
		String tokenCurrent;
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
	}

//	Register accountby username, email, password	
	@Override
	public ResponseEntity<?> register(Account account, User user) {
		MessageModel message = new MessageModel();
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

		if (accountRepository.findByUsername(account.getUsername()) != null) {
			message.setMessage(UserConstants.USERNAME_EXISTS);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
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

	}

//	Reset password when forgot password
	@Override
	public ResponseEntity<?> resetPassword(String email, String password) {

		MessageModel message = new MessageModel();
		Account account = accountRepository.findByEmail(email);
		
		if (accountRepository.findByEmail(email) == null) {
			message.setMessage(UserConstants.EMAIL_NOT_EXISTS);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (account.isActive() == false) {
			message.setMessage(UserConstants.NOT_ACTIVATED);
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

		account.setPassword(new BCryptPasswordEncoder().encode(password));
		accountRepository.saveAndFlush(account);
		message.setMessage(CommonConstants.SUCCESS);
		return ResponseEntity.ok(message);

	}

}
