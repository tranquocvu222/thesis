package ces.riccico.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.models.Accounts;

import ces.riccico.models.Users;
import ces.riccico.notification.UserNotification;
import ces.riccico.service.AccountService;
import ces.riccico.service.RoleService;
import ces.riccico.service.UserService;

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
	public String addAccount(@RequestBody Accounts model, Users user) {
		try {
			List<Accounts> checkAccount = accountService.findByUsername(model.getUserName()) ;
			String validationUsername = "^[a-z0-9._-]{6,12}$"; 
			String validationPassword = "((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!.#$@_+,?-]).{6,30})";
			if (model.getUserName().equals("")) {
				return UserNotification.usernameNull;
			}else if (model.getPassWord().equals("")) {
				return UserNotification.passwordNull;
			}else if (! model.getUserName().matches(validationUsername)) {
				return UserNotification.invalidUsernameFormat;
			}else if (! model.getPassWord().matches(validationPassword)) {
				return UserNotification.invalidPasswordFormat;
			} else if (checkAccount.size()==0)  {
				UUID uuid = UUID.randomUUID();
			    model.setRole(roleService.findAll().get(1));
			    model.setBanded(false);
				model.setIdAccount(String.valueOf(uuid));
				user.setAccount(model);
	            System.out.println("getIdUsers======= " + user.getAccount());
				System.out.println("getIdUsers " + user.getIdUser());
				accountService.save(model);
				userService.save(user);
				
				return UserNotification.registerSuccess;
			}else {
				return UserNotification.usernameExists;
			}
			
			
		} catch (Exception e) {
			System.out.println("addAccount: " + e);
			return UserNotification.registerFail;
		}
		
	}
	
	
}
