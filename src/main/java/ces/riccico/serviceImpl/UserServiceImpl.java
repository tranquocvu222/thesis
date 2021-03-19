
package ces.riccico.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.Message;
import ces.riccico.entities.Users;
import ces.riccico.notification.Notification;
import ces.riccico.notification.UserNotification;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.UserRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	SecurityAuditorAware securityAuditorAware;

//	Update profile of user
	@Override
	public ResponseEntity<?> editUser(Users model) {

		Integer idaccount = securityAuditorAware.getCurrentAuditor().get();

		Message message = new Message();
		try {

			Users user = userRepository.findByIdAccount(idaccount).get();

			if (user != null) {
				if (model.getFirstname().equals("")) {
					message.setMessage(UserNotification.FIRST_NAME_NULL);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (model.getLastname().equals("")) {
					message.setMessage(UserNotification.LAST_NAME_NULL);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (model.getBirthday() == null) {
					message.setMessage(UserNotification.BIRTHDAY_NULL);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (model.getAddress().equals("")) {
					message.setMessage(UserNotification.ADDRESS_NULL);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (model.getCity().equals("")) {
					message.setMessage(UserNotification.CITY_NULL);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (model.getCountry().equals("")) {
					message.setMessage(UserNotification.COUNTRY_NULL);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else {
					user.setFirstname(model.getFirstname());
					user.setLastname(model.getLastname());
					user.setBirthday(model.getBirthday());
					user.setAddress(model.getAddress());
					user.setCity(model.getCity());
					user.setCountry(model.getCountry());
					userRepository.saveAndFlush(user);
					System.out.println("==========" + user);
				}

				message.setMessage(Notification.SUCCESS);
				return ResponseEntity.ok(message);
			}

		} catch (Exception e) {

			System.out.println("editAdmin: " + e);
		}

		message.setMessage(Notification.FAIL);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}

//	Show list user
	@Override
	public List<Users> findAll() {
		return userRepository.findAll();
	}

//	Find user by id_account was login
	@Override
	public ResponseEntity<?> findById() {
		
		Message message = new Message();
		
		try {
			
			Integer idaccount = securityAuditorAware.getCurrentAuditor().get();
			Users user = userRepository.findById(idaccount).get();
			message.setMessage(Notification.SUCCESS);
			
			return ResponseEntity.ok(user);
			
		} catch (Exception e) {
			
			message.setMessage(e.getLocalizedMessage());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
		
	
	}


}
