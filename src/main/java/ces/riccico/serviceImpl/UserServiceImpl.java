
package ces.riccico.serviceImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.Message;
import ces.riccico.models.Role;
import ces.riccico.common.CommonConstants;
import ces.riccico.common.UserConstants;
import ces.riccico.entities.User;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.UserRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

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

		Message message = new Message();

		User user = new User();
		logger.error("=====" + user.toString());

		try {
			user = userRepository.findById(userId).get();
		} catch (Exception e) {
			message.setMessage(UserConstants.USER_NOT_EXISTS);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		try {

			if (!userRepository.findByAccountId(idCurrent).get().getAccount().getRole().equals(Role.ADMIN.getRole())
					&& !idCurrent.equals(userRepository.findById(userId).get().getAccount().getAccountId())) {
				message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);

			}

			if (model.getFirstName().equals("")) {
				message.setMessage(UserConstants.FIRST_NAME_NULL);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			if (model.getLastName().equals("")) {
				message.setMessage(UserConstants.LAST_NAME_NULL);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			if (model.getBirthDay() == null) {
				message.setMessage(UserConstants.BIRTHDAY_NULL);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			if (model.getAddress().equals("")) {
				message.setMessage(UserConstants.ADDRESS_NULL);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			if (model.getCity().equals("")) {
				message.setMessage(UserConstants.CITY_NULL);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			if (model.getCountry().equals("")) {
				message.setMessage(UserConstants.COUNTRY_NULL);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);

			} else {
				user.setFirstName(model.getFirstName());
				user.setLastName(model.getLastName());
				user.setBirthDay(model.getBirthDay());
				user.setAddress(model.getAddress());
				user.setCity(model.getCity());
				user.setCountry(model.getCountry());
				userRepository.saveAndFlush(user);

			}

			message.setMessage(CommonConstants.SUCCESS);
			return ResponseEntity.ok(message);

		} catch (Exception e) {
			logger.error("------ " + e.getMessage());
			message.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

	}

//	Show list user
	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

//	Find user by id_account was login
	@Override
	public ResponseEntity<?> findById() {

		Message message = new Message();

		try {
			Integer idaccount = securityAuditorAware.getCurrentAuditor().get();

			User user = userRepository.findByAccountId(idaccount).get();

			message.setMessage(CommonConstants.SUCCESS);

			return ResponseEntity.ok(user);

		} catch (Exception e) {

			message.setMessage(e.getLocalizedMessage());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}
}
