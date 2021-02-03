package ces.riccico.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ces.riccico.models.Accounts;
import ces.riccico.models.Users;
import ces.riccico.notification.UserNotification;
import ces.riccico.service.AccountService;
import ces.riccico.service.RoleService;
import ces.riccico.service.UserService;
import ces.riccico.validation.Validation;

@CrossOrigin(origins = "http://127.0.0.1:5500")
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
	
	public static int confirmCode;
	
	
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
	
	//Validation form
//	public boolean validation() {
//		try {
//			Accounts account = new Accounts();
//			String validationUsername = "^[a-z0-9._-]{6,12}$"; 
//			String validationPassword = "((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!.#$@_+,?-]).{6,30})";
//			if (account.getUserName().equals("")) {
//				boolean usernameNull = Boolean.parseBoolean(Notification.usernameNull);
//				return usernameNull ;
//			}else if (account.getPassWord().equals("")) {
//				boolean passwordNull = Boolean.parseBoolean(Notification.passwordNull);
//				return passwordNull ;
//			}else if (! account.getUserName().matches(validationUsername)) {
//				boolean invalidUsernameFormat = Boolean.parseBoolean(Notification.invalidUsernameFormat);
//				return invalidUsernameFormat ;
//			}else if (! account.getPassWord().matches(validationPassword)) {
//				boolean invalidPasswordFormat = Boolean.parseBoolean(Notification.invalidPasswordFormat);
//				return invalidPasswordFormat ;
//			}
//		} catch (Exception e) {
//			return false;
//		}
//		return true;
//	}
	
	//Thêm mới tài khoản
	@RequestMapping(value = "/account/new", method = RequestMethod.POST)
	public String addAccount(@RequestBody Accounts account, Users user) {
		try {
//			List<Accounts> checkAccount = accountService.findByUsername(model.getUserName()) ;
//			String validationUsername = "^[a-z0-9._-]{6,12}$"; 
//			String validationPassword = "((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!.#$@_+,?-]).{6,30})";
			int code = (int) Math.floor(((Math.random() * 899999) + 100000));
			confirmCode = code;
			Validation vali = new Validation();
			if (account.getUserName().equals("")) {
				return UserNotification.usernameNull;
			}else if (account.getEmail().equals("")) {
				return UserNotification.emailNull;
			}else if (account.getPassWord().equals("")) {
				return UserNotification.passwordNull;
			}else if (! account.getUserName().matches(Validation.USERNAME_PATTERN)) {
				return UserNotification.invalidUsernameFormat;
			}else if (! account.getEmail().matches(Validation.EMAIL_PATTERN)) {
				return UserNotification.invalidEmailFormat;
			}else if (! account.getPassWord().matches(Validation.PASSWORD_PATTERN)) {
				return UserNotification.invalidPasswordFormat;
			}else if (accountService.findByEmail(account.getEmail()) != null) {
				return UserNotification.emailExists;
			}else if (accountService.findByUsername(account.getUserName()) == null )  {
				UUID uuid = UUID.randomUUID();
				account.setRole(roleService.findAll().get(1));
				account.setBanded(false);
				account.setIdAccount(String.valueOf(uuid));
				account.setActive(false);
				user.setAccount(account);
				accountService.save(account);
				userService.save(user);
				SimpleMailMessage message = new SimpleMailMessage();
				message.setTo(account.getEmail());
				message.setSubject("Mã Xác Nhận");
				message.setText("Xin Chào "+account.getEmail()+"\nMã xác nhận của bạn là:"+ code);		
				try {		
					sender.send(message);

				} catch (Exception e) {
					System.out.println("createNewServices: " + e);
				}
				return UserNotification.registerSuccess;
			}else {
				return UserNotification.usernameExists;
			}
		} catch (Exception e) {
			System.out.println("addAccount: " + e);
			return UserNotification.registerFail;
		}
		
	}
	@RequestMapping(value = "/account/activeEmail/{codeInput}", method = RequestMethod.POST)
	public String activeAccount(@RequestParam int codeInput,@RequestParam String idAccount) {
		Accounts account = accountService.findById(idAccount).get();
		if (codeInput == confirmCode) {
			account.setActive(true);
			accountService.save(account);
		}else {
			return"Mã xác nhận sai";
		}
		return null;
	}
	
	
}
