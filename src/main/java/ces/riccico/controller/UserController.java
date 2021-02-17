package ces.riccico.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ces.riccico.models.Users;
import ces.riccico.notification.AuthNotification;
import ces.riccico.notification.UserNotification;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.AccountService;
import ces.riccico.service.UserService;


@CrossOrigin
@RestController
public class UserController {

	@Autowired
	UserService userService;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	SecurityAuditorAware securityAuditorAware;
	
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public List<Users> getAll() {
		try {
			List<Users> listUsers = new ArrayList<Users>();
			listUsers = userService.findAll();
			return listUsers;
		} catch (Exception e) {
			System.out.println("getAll: " + e);
			return null;
		}
	}
	
//	@RequestMapping(value = "/user/new", method = RequestMethod.POST)
//	public void addUsers(@RequestBody Users model) {
//		try {
//			userService.save(model);
//		} catch (Exception e) {
//			System.out.println("addUsers: " + e);
//		}
//	}
	
	
//	Edit Profile
	@RequestMapping(value = "/editUser", method = RequestMethod.POST)
	public ResponseEntity<?> editUser(@RequestBody Users model) {
		String idaccount = securityAuditorAware.getCurrentAuditor().get();
		try {
			Optional<Users> user = userService.findByIdAccount(idaccount);
			if (user != null) {
				if (model.getFirstname().equals("")) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.firstNameNull);
				}else if (model.getLastname().equals("")) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.lastNameNull);
				}else if (model.getBirthday() == null) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.birthDayNull);
				}else if (model.getAddress().equals("")) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.addressNull);
				}else if (model.getCity().equals("")) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.cityNull);
				}else if (model.getCountry().equals("")) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.countryNameNull);
				}else {
				user.get().setFirstname(model.getFirstname());
				user.get().setLastname(model.getLastname());
				user.get().setBirthday(model.getBirthday());
				user.get().setAddress(model.getAddress());
				user.get().setCity(model.getCity());
				user.get().setCountry(model.getCountry());
				userService.save(user.get());
			}
			}
		} catch (Exception e) {
			System.out.println("editAdmin: " + e);
		}
		return ResponseEntity.ok(AuthNotification.success);
	}
	
	


}
