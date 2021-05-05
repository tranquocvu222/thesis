
package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.decimal4j.util.DoubleRounder;
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
import ces.riccico.common.constants.HouseConstants;
import ces.riccico.common.constants.TokenConstants;
import ces.riccico.common.constants.UserConstants;
import ces.riccico.common.constants.Validation;
import ces.riccico.common.enums.Role;
import ces.riccico.common.enums.StatusAccount;
import ces.riccico.common.enums.StatusBooking;
import ces.riccico.common.enums.StatusHouse;
import ces.riccico.entity.Account;
import ces.riccico.entity.Booking;
import ces.riccico.entity.Token;
import ces.riccico.entity.User;
import ces.riccico.model.AccountModel;
import ces.riccico.model.BookingDetailModel;
import ces.riccico.model.LoginModel;
import ces.riccico.model.MessageModel;
import ces.riccico.model.PaginationModel;
import ces.riccico.model.StatisticOwner;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.BookingRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.repository.RatingRepository;
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

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private RatingRepository ratingRepository;

//	Active account by email
	@Override
	public ResponseEntity<?> activeAccount(int codeInput, String email) {

		MessageModel message = new MessageModel();
		Account account = accountRepository.findByEmail(email);

		if (account == null) {
			message.setMessage(UserConstants.EMAIL_NOT_EXISTS);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (codeInput != CONFIRM_CODE) {
			message.setMessage(UserConstants.INVALID_CODE);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		} else {
			account.setActive(true);
			accountRepository.saveAndFlush(account);
//			message.setData(account);
			message.setMessage(CommonConstants.ACTIVE_SUCCESS);
			message.setStatus(HttpStatus.OK.value());
			return ResponseEntity.ok(message);
		}
	}

//	Banned account 
	@Override
	public ResponseEntity<?> banAccount(int accountId) {

		MessageModel message = new MessageModel();
		Integer currentId = securityAuditorAware.getCurrentAuditor().get();
		Account account = accountRepository.findById(accountId).get();

		if (!accountRepository.findById(currentId).get().getRole().equals(Role.ADMIN.getRole())) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			message.setStatus(HttpStatus.FORBIDDEN.value());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
		}

		if (account.isBanned() == IS_BANNED) {
			account.setBanned(IS_NOT_BANNED);
			accountRepository.saveAndFlush(account);
			message.setData(account);
			message.setMessage(CommonConstants.UNBAN_SUCCESS);
			message.setStatus(HttpStatus.OK.value());
			return ResponseEntity.ok(message);
		}

		account.setBanned(IS_BANNED);
		accountRepository.saveAndFlush(account);
//		message.setData(account);
		message.setMessage(CommonConstants.BAN_SUCCESS);
		message.setStatus(HttpStatus.OK.value());
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
			message.setStatus(HttpStatus.FORBIDDEN.value());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
		} else {
			if (oldPassword == null || oldPassword.isEmpty()) {
				message.setMessage(UserConstants.PASSWORD_NULL);
				message.setStatus(HttpStatus.BAD_REQUEST.value());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			if (newPassword == null || newPassword.isEmpty()) {
				message.setMessage(UserConstants.PASSWORD_NEW_NULL);
				message.setStatus(HttpStatus.BAD_REQUEST.value());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			if (!encoder.matches(oldPassword, account.getPassword())) {
				message.setMessage(UserConstants.WRONG_OLD_PASSWORD);
				message.setStatus(HttpStatus.BAD_REQUEST.value());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			if (!newPassword.matches(Validation.PASSWORD_PATTERN)) {
				message.setMessage(UserConstants.INVALID_PASSWORD_FORMAT);
				message.setStatus(HttpStatus.BAD_REQUEST.value());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			if (newPassword.equals(oldPassword)) {
				message.setMessage(UserConstants.IS_MATCHING_OLD_PASSWORD);
				message.setStatus(HttpStatus.BAD_REQUEST.value());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else {
				account.setPassword(encoder.encode(newPassword));
				accountRepository.saveAndFlush(account);
				message.setMessage(CommonConstants.CHANGEPASSWORD_SUCCESS);
				message.setStatus(HttpStatus.OK.value());
				return ResponseEntity.ok(message);
			}

		}
	}

//	Show list account was banned
//	@Override
//	public ResponseEntity<?> findAllIsBanned() {
//		List<Account> listAccount = new ArrayList<Account>();
//		MessageModel message = new MessageModel();
//
//		for (Account account : accountRepository.findAll()) {
//			if (account.isBanned() == IS_BANNED) {
//				listAccount.add(account);
//			}
//		}
//
//		if (listAccount.size() == 0) {
//			message.setError("List Account was banned empty");
//			message.setMessage(CommonConstants.LIST_EMPTY);
//			message.setStatus(HttpStatus.NOT_FOUND.value());
//			ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
//		}
//
//		message.setData(listAccount);
//		message.setMessage(UserConstants.GET_INFORMATION);
//		message.setStatus(HttpStatus.OK.value());
//		return ResponseEntity.ok(message);
//	}

////	Show list account was not banned
//	@Override
//	public ResponseEntity<?> findAll() {
//		List<Account> listAccount = new ArrayList<Account>();
//		MessageModel message = new MessageModel();
//
//		for (Account account : accountRepository.findAll()) {
//			if (account.isBanned() == IS_NOT_BANNED) {
//				listAccount.add(account);
//			}
//		}
//
//		if (listAccount.size() == 0) {
//			message.setError("List Account was not banned empty");
//			message.setMessage(CommonConstants.LIST_EMPTY);
//			message.setStatus(HttpStatus.NOT_FOUND.value());
//			ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
//		}
//
//		message.setData(listAccount);
//		message.setMessage(UserConstants.GET_INFORMATION);
//		message.setStatus(HttpStatus.OK.value());
//		return ResponseEntity.ok(message);
//	}
	@Override
	public ResponseEntity<?> findAllAccountPageAndSize(String status, int page, int size) {

		List<Object> listAccountModel = new ArrayList<Object>();
		List<Account> listAccount = new ArrayList<Account>();
		PaginationModel paginationModel = new PaginationModel();
		MessageModel message = new MessageModel();
		Pageable paging = PageRequest.of(page, size);
		int pageMax = 0;
		boolean statusCurrent = Boolean.parseBoolean(status);

		if (status == null || status.isEmpty()) {
			listAccount = accountRepository.findAll(paging).getContent();
			pageMax = accountRepository.findAll(paging).getTotalPages();
		
		} else {
			listAccount = accountRepository.getAccountsForAdmin(statusCurrent, paging).getContent();
			pageMax = accountRepository.getAccountsForAdmin(statusCurrent, paging).getTotalPages();
			
		}

		if (listAccount.size() == 0) {
			message.setMessage(CommonConstants.LIST_ACCOUNT_EMPTY);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		for (Account account : listAccount) {
			AccountModel accountModel = mapper.map(account, AccountModel.class);
			accountModel.setId(account.getAccountId());
			accountModel.setFirstName(account.getUser().getFirstName());
			accountModel.setLastName(account.getUser().getLastName());
			accountModel.setBirthday(account.getUser().getBirthday());
			accountModel.setCity(account.getUser().getCity());
			accountModel.setAddress(account.getUser().getAddress());
			accountModel.setImage(account.getUser().getImage());
			accountModel.setTotalHouse(accountRepository.countHouse(account.getAccountId()));
			listAccountModel.add(accountModel);

		}

		if (page >= pageMax) {
			message.setMessage(CommonConstants.INVALID_PAGE);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		paginationModel.setListObject(listAccountModel);
		paginationModel.setPageMax(pageMax);
		message.setData(paginationModel);
		message.setMessage(UserConstants.GET_INFORMATION);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);
	}

//  Recover password by email when forget 
	@Override
	public ResponseEntity<?> forgetPassword(String email) {

		MessageModel message = new MessageModel();
		Account account = accountRepository.findByEmail(email);

		if (account == null) {
			message.setMessage(UserConstants.EMAIL_NOT_EXISTS);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (account.isActive() == false) {
			message.setMessage(UserConstants.NOT_ACTIVATED);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (!email.matches(Validation.EMAIL_PATTERN)) {
			message.setMessage(UserConstants.INVALID_EMAIL_FORMAT);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
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
		message.setStatus(HttpStatus.OK.value());
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
			accountDetail.setUserId(account.getAccountId());
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
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (password == null || password.isEmpty()) {
			message.setMessage(UserConstants.PASSWORD_NULL);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (usernameOrEmail.matches(Validation.EMAIL_PATTERN)) {
			if (accountRepository.findByEmail(usernameOrEmail) == null) {
				message.setMessage(UserConstants.EMAIL_NOT_EXISTS);
				message.setStatus(HttpStatus.NOT_FOUND.value());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			}
			usernameOrEmail = accountRepository.findByEmail(usernameOrEmail).getUsername();
		}

		AccountDetail accountDetail = loadUserByUsername(usernameOrEmail);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		if (accountRepository.findByUsername(usernameOrEmail) == null) {
			message.setMessage(UserConstants.ACCOUNT_NOT_EXISTS);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		} else {
			if (!encoder.matches(account.getPassword(), accountDetail.getPassword())) {
				message.setMessage(UserConstants.INVALID_ACCOUNT);
				message.setStatus(HttpStatus.BAD_REQUEST.value());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			if (accountRepository.findByUsername(usernameOrEmail).isBanned()) {
				message.setMessage(UserConstants.ACCOUNT_BANNED);
				message.setStatus(HttpStatus.FORBIDDEN.value());
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			}

			if (!accountRepository.findByUsername(usernameOrEmail).isActive()) {
				message.setMessage(UserConstants.NOT_ACTIVATED);
				message.setStatus(HttpStatus.FORBIDDEN.value());
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			} else {
				Token token = new Token();
				token.setToken(jwtUtil.generateToken(accountDetail));
				token.setTokenExpDate(jwtUtil.generateExpirationDate());
				tokenRepository.saveAndFlush(token);
				message.setData(token.getToken());
				message.setMessage(CommonConstants.LOGIN_SUCCESS);
				message.setStatus(HttpStatus.OK.value());
				return ResponseEntity.ok(message);
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
			message.setStatus(HttpStatus.FORBIDDEN.value());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
		}

		if (!jwtUtil.isTokenExpired(claims)) {
			message.setMessage(TokenConstants.IS_TOKEN_EXPIRED);
			message.setStatus(HttpStatus.FORBIDDEN.value());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
		}

		if (!jwtUtil.validateToken(tokenCurrent, account)) {
			message.setMessage(TokenConstants.INVALID_TOKEN);
			message.setStatus(HttpStatus.FORBIDDEN.value());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
		} else {
			Token token = tokenRepository.findByToken(tokenCurrent);
			tokenRepository.delete(token);
//			message.setData(token.getToken());
			message.setMessage(CommonConstants.SUCCESS);
			message.setStatus(HttpStatus.OK.value());
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
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (account.getEmail().equals("")) {
			message.setMessage(UserConstants.EMAIL_NULL);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (account.getPassword().equals("")) {
			message.setMessage(UserConstants.PASSWORD_NULL);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (!account.getUsername().matches(Validation.USERNAME_PATTERN)) {
			message.setMessage(UserConstants.INVALID_USERNAME_FORMAT);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (!account.getEmail().matches(Validation.EMAIL_PATTERN)) {
			message.setMessage(UserConstants.INVALID_EMAIL_FORMAT);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (!account.getPassword().matches(Validation.PASSWORD_PATTERN)) {
			message.setMessage(UserConstants.INVALID_PASSWORD_FORMAT);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (accountRepository.findByEmail(account.getEmail()) != null) {
			message.setMessage(UserConstants.EMAIL_EXISTS);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (accountRepository.findByUsername(account.getUsername()) != null) {
			message.setMessage(UserConstants.USERNAME_EXISTS);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
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
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);

	}

//	Reset password when forgot password
	@Override
	public ResponseEntity<?> resetPassword(String email, String password) {

		MessageModel message = new MessageModel();
		Account account = accountRepository.findByEmail(email);

		if (accountRepository.findByEmail(email) == null) {
			message.setMessage(UserConstants.EMAIL_NOT_EXISTS);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (account.isActive() == false) {
			message.setMessage(UserConstants.NOT_ACTIVATED);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (!email.matches(Validation.EMAIL_PATTERN)) {
			message.setMessage(UserConstants.INVALID_EMAIL_FORMAT);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (password == null) {
			message.setMessage(UserConstants.PASSWORD_NULL);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (!password.matches(Validation.PASSWORD_PATTERN)) {
			message.setMessage(UserConstants.INVALID_PASSWORD_FORMAT);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		account.setPassword(new BCryptPasswordEncoder().encode(password));
		accountRepository.saveAndFlush(account);
		message.setMessage(CommonConstants.SUCCESS);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);

	}

	@Override
	public ResponseEntity<?> getStatisticOwner(int accountId) {
		StatisticOwner statisticOwner = new StatisticOwner();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		MessageModel message = new MessageModel();

		if (!idCurrent.equals(accountId)) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			message.setStatus(HttpStatus.FORBIDDEN.value());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
		}
		Long revenue = 0l;
		try {
			revenue = (bookingRepository.sumByAccountId(accountId) * 85) / 100;
		} catch (NullPointerException e) {
			logger.error(e.getMessage());
		}
		Integer totalBooking = bookingRepository.countByAccountId(accountId);
		Integer totalRating = ratingRepository.countByAccountId(accountId);
		Float averageRating = 0f;
		try {
			averageRating = (float) DoubleRounder.round(ratingRepository.averageRatingByAccountId(accountId), 1);
		} catch (NullPointerException e) {
			logger.error(e.getMessage());
		}

		List<Booking> listBookingDb = new ArrayList<Booking>();
		List<BookingDetailModel> listBooking =  new ArrayList<BookingDetailModel>();
		try {
			for (Booking booking : bookingRepository.findAccountId(accountId)) {
				if (booking.getStatus().equals(StatusBooking.PAID.getStatusName())
						|| booking.getStatus().equals(StatusBooking.COMPLETED.getStatusName())) {
					BookingDetailModel bookingModel = mapper.map(booking, BookingDetailModel.class);
					bookingModel.setCustomerId(booking.getAccount().getAccountId());
					bookingModel.setCustomerName(booking.getAccount().getUsername());
					bookingModel.setHouseName(booking.getHouse().getTitle());
					bookingModel.setHouseId(booking.getHouse().getId());
					listBooking.add(bookingModel);
				}
			}

		} catch (NullPointerException e) {
			logger.error(e.getMessage());
		}

		statisticOwner.setRevenue(revenue);
		statisticOwner.setTotalBooking(totalBooking);
		statisticOwner.setTotalRating(totalRating);
		statisticOwner.setAverageRating(averageRating);
		statisticOwner.setListBooking(listBooking);
		message.setData(statisticOwner);
		message.setMessage(UserConstants.GET_INFORMATION);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);
	}
}
