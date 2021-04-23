

package ces.riccico.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ces.riccico.common.constants.CommonConstants;
import ces.riccico.common.constants.UserConstants;
import ces.riccico.common.enums.Role;
import ces.riccico.entity.User;
import ces.riccico.model.MessageModel;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.UserRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	private static String IMAGE = "https://cdn.luxstay.com/users_avatar_default/default-avatar.png";
	

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	SecurityAuditorAware securityAuditorAware;

//	Update profile of user
	@Override
	public ResponseEntity<?> editUser(User model, Integer userId) {

		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		MessageModel message = new MessageModel();
		User user = new User();

		if (!userRepository.findById(userId).isPresent()) {
			message.setMessage(UserConstants.ACCOUNT_NOT_EXISTS);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		user = userRepository.findById(userId).get();

		if (!userRepository.findByAccountId(idCurrent).getAccount().getRole().equals(Role.ADMIN.getRole())
				&& !idCurrent.equals(user.getAccount().getAccountId())) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			message.setStatus(HttpStatus.UNAUTHORIZED.value());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		if (model.getFirstName().equals("")) {
			message.setMessage(UserConstants.FIRST_NAME_NULL);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (model.getLastName().equals("")) {
			message.setMessage(UserConstants.LAST_NAME_NULL);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (model.getBirthday() == null) {
			message.setMessage(UserConstants.BIRTHDAY_NULL);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (model.getAddress().equals("")) {
			message.setMessage(UserConstants.ADDRESS_NULL);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (model.getCity().equals("")) {
			message.setMessage(UserConstants.CITY_NULL);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (model.getCountry().equals("")) {
			message.setMessage(UserConstants.COUNTRY_NULL);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
		
		if (model.getImage() == null) {
			user.setImage(IMAGE);
			userRepository.saveAndFlush(user);
			message.setMessage(UserConstants.IMAGE_DEFAULT);
			message.setStatus(HttpStatus.OK.value());
			return ResponseEntity.ok(message);
		}

		user.setFirstName(model.getFirstName());
		user.setLastName(model.getLastName());
		user.setBirthday(model.getBirthday());
		user.setAddress(model.getAddress());
		user.setCity(model.getCity());
		user.setCountry(model.getCountry());
		user.setImage(model.getImage());
		userRepository.saveAndFlush(user);
		message.setMessage(UserConstants.UPDATE_SUCCESS);
		message.setData(user);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);

	}

//	Show list user
	@Override
	public ResponseEntity<?> findAll() {
		return ResponseEntity.ok(userRepository.findAll());
	}

//	Find user by id_account was login
	@Override
	public ResponseEntity<?> findById() {

		
		MessageModel message = new MessageModel();
		Integer accountId = securityAuditorAware.getCurrentAuditor().get();
		if (accountId == null) {
			message.setError("Account current null");
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.ok(message);
		}

		User user = userRepository.findByAccountId(accountId);
		message.setMessage(UserConstants.GET_INFORMATION);
		message.setData(user);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);

	}

}
