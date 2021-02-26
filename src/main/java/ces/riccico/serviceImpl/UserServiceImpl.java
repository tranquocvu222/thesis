package ces.riccico.serviceImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
		String idaccount = securityAuditorAware.getCurrentAuditor().get();
		try {
			Users user = userRepository.findByIdAccount(idaccount).get();
			System.out.println("==========" +user);
			if (user != null) {
				if (model.getFirstname().equals("")) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,UserNotification.firstNameNull));
				}else if (model.getLastname().equals("")) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,UserNotification.lastNameNull));
				}else if (model.getBirthday() == null) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,UserNotification.birthDayNull));
				}else if (model.getAddress().equals("")) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,UserNotification.addressNull));
				}else if (model.getCity().equals("")) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,UserNotification.cityNull));
				}else if (model.getCountry().equals("")) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,UserNotification.countryNameNull));
				}else {
				user.setFirstname(model.getFirstname());
				user.setLastname(model.getLastname());
				user.setBirthday(model.getBirthday());
				user.setAddress(model.getAddress());
				user.setCity(model.getCity());
				user.setCountry(model.getCountry());
				userRepository.saveAndFlush(user);
			}
			}
		} catch (Exception e) {
			System.out.println("editAdmin: " + e);
		}
		return ResponseEntity.ok(Map.of(Notification.message,Notification.success));
	}
	


}
