package ces.riccico.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ces.riccico.models.Accounts;
import ces.riccico.models.LoginModel;
import ces.riccico.service.AccountService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ces.riccico.models.Users;
import ces.riccico.notification.UserNotification;
import ces.riccico.service.RoleService;
import ces.riccico.service.UserService;

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

	// Thêm mới tài khoản
	@RequestMapping(value = "/account/new", method = RequestMethod.POST)
	public String addAccount(@RequestBody Accounts account, Users user) {
		try {
			List<Accounts> checkAccount = accountService.findByListUserName(account.getUserName());
			String validationUsername = "^[a-z0-9._-]{6,12}$";
			String validationPassword = "((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!.#$@_+,?-]).{6,30})";
			if (account.getUserName().equals("")) {
				return UserNotification.usernameNull;
			} else if (account.getPassWord().equals("")) {
				return UserNotification.passwordNull;
			} else if (!account.getUserName().matches(validationUsername)) {
				return UserNotification.invalidUsernameFormat;
			} else if (!account.getPassWord().matches(validationPassword)) {
				return UserNotification.invalidPasswordFormat;
			} else if (accountService.findByUserName(account.getUserName()) == null) {
				UUID uuid = UUID.randomUUID();
				account.setRole(roleService.findAll().get(1));
				account.setBanded(false);
				account.setIdAccount(String.valueOf(uuid));
				account.setPassWord(new BCryptPasswordEncoder().encode(account.getPassWord()));
				System.out.println("getPassword======= " + account.getPassWord());
				user.setAccount(account);
				accountService.save(account);
				userService.save(user);

				return UserNotification.registerSuccess;
			} else {
				return UserNotification.usernameExists;
			}

		} catch (Exception e) {
			System.out.println("addAccount: " + e);
			return UserNotification.registerFail;
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginModel account) {
		return accountService.login(account);

	}

	@DeleteMapping("/log-out")
	public ResponseEntity<?> logout() {
		return accountService.logout();
	}

}
