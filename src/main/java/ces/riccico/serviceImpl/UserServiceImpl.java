package ces.riccico.serviceImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.Message;
import ces.riccico.models.Users;
import ces.riccico.notification.Notification;
import ces.riccico.notification.UserNotification;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.UserRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	SecurityAuditorAware securityAuditorAware;

	@Override
	public List<Users> findAll() {
		return userRepository.findAll();
	}

	@Override
	public ResponseEntity<?> editUser(Users model) {
		Integer idaccount = securityAuditorAware.getCurrentAuditor().get();
		Message message = new Message();
		try {
			Users user = userRepository.findByIdAccount(idaccount).get();
			System.out.println("==========" + user);
			if (user != null) {
				if (model.getFirstname().equals("")) {
					message.setMessage(UserNotification.firstNameNull);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (model.getLastname().equals("")) {
					message.setMessage(UserNotification.lastNameNull);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (model.getBirthday() == null) {
					message.setMessage(UserNotification.birthDayNull);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (model.getAddress().equals("")) {
					message.setMessage(UserNotification.addressNull);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (model.getCity().equals("")) {
					message.setMessage(UserNotification.cityNull);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (model.getCountry().equals("")) {
					message.setMessage(UserNotification.countryNameNull);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else {
					user.setFirstname(model.getFirstname());
					user.setLastname(model.getLastname());
					user.setBirthday(model.getBirthday());
					user.setAddress(model.getAddress());
					user.setCity(model.getCity());
					user.setCountry(model.getCountry());
					userRepository.saveAndFlush(user);
				}
				message.setMessage(Notification.success);
				return ResponseEntity.ok(message);
			}
		} catch (Exception e) {
			System.out.println("editAdmin: " + e);
		}
		message.setMessage(Notification.fail);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}

}
