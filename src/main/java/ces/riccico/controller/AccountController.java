package ces.riccico.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ces.riccico.models.Accounts;
import ces.riccico.models.LoginModel;
import ces.riccico.service.AccountService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ces.riccico.models.Users;
import ces.riccico.notification.UserNotification;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.RoleService;
import ces.riccico.service.UserService;
import ces.riccico.validation.Validation;

@CrossOrigin
@RestController
public class AccountController {

	@Autowired
	AccountService accountService;

	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	@Autowired
	UserNotification notification;

	@Autowired
	public JavaMailSender sender;
	
	@Autowired
	SecurityAuditorAware securityAuditorAware;

	public static int confirmCode;

//	Show list account
	@RequestMapping(value = "/account", method = RequestMethod.GET)
	public List<Accounts> getAll() {
		try {
			List<Accounts> listAccount = new ArrayList<Accounts>();
			listAccount = accountService.findAll();
			return listAccount;
		} catch (Exception e) {
			System.out.println("getAll: " + e);
			return null;
		}
	}

	// Add account
	@RequestMapping(value = "/account/new", method = RequestMethod.POST)
	public String addAccount(@RequestBody Accounts account, Users user) {
		try {

			int code = (int) Math.floor(((Math.random() * 899999) + 100000));
			confirmCode = code;
			Validation vali = new Validation();
			if (account.getUserName().equals("")) {
				return UserNotification.usernameNull;
			} else if (account.getEmail().equals("")) {
				return UserNotification.emailNull;
			} else if (account.getPassWord().equals("")) {
				return UserNotification.passwordNull;
			} else if (!account.getUserName().matches(Validation.USERNAME_PATTERN)) {
				return UserNotification.invalidUsernameFormat;
			} else if (!account.getEmail().matches(Validation.EMAIL_PATTERN)) {
				return UserNotification.invalidEmailFormat;
			} else if (!account.getPassWord().matches(Validation.PASSWORD_PATTERN)) {
				return UserNotification.invalidPasswordFormat;
			} else if (accountService.findByEmail(account.getEmail()) != null) {
				return UserNotification.emailExists;
			} else if (accountService.findByUserName(account.getUserName()) == null) {
				UUID uuid = UUID.randomUUID();
				account.setRole(roleService.findAll().get(1));
				account.setBanded(false);
				account.setIdAccount(String.valueOf(uuid));
				account.setPassWord(new BCryptPasswordEncoder().encode(account.getPassWord()));
				account.setActive(false);
				user.setAccount(account);
				accountService.save(account);
				userService.save(user);
				SimpleMailMessage message = new SimpleMailMessage();
				message.setTo(account.getEmail());
				message.setSubject("Mã Xác Nhận");
				message.setText("Xin Chào " + account.getEmail() + "\nMã xác nhận của bạn là:" + code);
				try {
					sender.send(message);

				} catch (Exception e) {
					System.out.println("createNewServices: " + e);
				}
				return UserNotification.registerSuccess;
			} else {
				return UserNotification.usernameExists;
			}
		} catch (Exception e) {
			System.out.println("addAccount: " + e);
			return UserNotification.registerFail;
		}

	}
 
//	Confirm code
	@RequestMapping(value = "/account/activeEmail/{codeInput}/{username}", method = RequestMethod.POST)
	public String activeAccount(@PathVariable int codeInput,@PathVariable String username) {
		 
		Accounts account = accountService.findByUserName(username);
		try {
			if (codeInput == confirmCode) {
				account.setActive(true);
				accountService.save(account);
			}
		} catch (Exception e) {
			return UserNotification.confirmFail;
		}
		
		return UserNotification.confirmSuccess;
	}


//	Login
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginModel account) {
		return accountService.login(account);

	}

//	Log-out
	@DeleteMapping("/log-out")
	@PreAuthorize("hasAnyAuthority('user','admin')")
	public ResponseEntity<?> logout(){
		return accountService.logout();
	}
}
