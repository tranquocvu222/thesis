package ces.riccico.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	private static final boolean IsBanned = true;
	private static final boolean IsNotBanned = false;

//	Show list account is not bannded
	@RequestMapping(value = "/account", method = RequestMethod.GET)
	public List<Accounts> getAll() {
		try {
			List<Accounts> listAccount = new ArrayList<Accounts>();
			for (Accounts account : accountService.findAll()) {
				if (account.isBanned() == IsNotBanned) {
					listAccount.add(account);
				}
			}
			return listAccount;
		} catch (Exception e) {
			System.out.println("getAll: " + e);
			return null;
		}
	}

//	Show list account is bannded
	@RequestMapping(value = "/account/isbanned", method = RequestMethod.GET)
	public List<Accounts> getAllIsBanned() {
		try {
			List<Accounts> listAccount = new ArrayList<Accounts>();
			for (Accounts account : accountService.findAll()) {
				if (account.isBanned() == IsBanned) {
					listAccount.add(account);
				}
			}
			return listAccount;
		} catch (Exception e) {
			System.out.println("getAll: " + e);
			return null;
		}
	}

	// Add account
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> addAccount(@RequestBody Accounts account, Users user) {
		try {
			int code = (int) Math.floor(((Math.random() * 899999) + 100000));
			confirmCode = code;
			if (account.getUsername().equals("")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.usernameNull);
			} else if (account.getEmail().equals("")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailNull);
			} else if (account.getPassword().equals("")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.passwordNull);
			} else if (!account.getUsername().matches(Validation.USERNAME_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.invalidUsernameFormat);
			} else if (!account.getEmail().matches(Validation.EMAIL_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.invalidEmailFormat);
			} else if (!account.getPassword().matches(Validation.PASSWORD_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.invalidPasswordFormat);
			} else if (accountService.findByEmail(account.getEmail()) != null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailExists);
			} else if (accountService.findByUserName(account.getUsername()) == null) {
				UUID uuid = UUID.randomUUID();
				account.setRole(roleService.findAll().get(1));
				account.setBanned(false);
				account.setIdAccount(String.valueOf(uuid));
				account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));
				account.setActive(false);
				user.setAccount(account);
				accountService.save(account);
				userService.save(user);
				SimpleMailMessage message = new SimpleMailMessage();
				message.setTo(account.getEmail());
				message.setSubject("Verification Code");
				message.setText("Wellcome " + account.getEmail() + "\nYour Verification Code is: " + code
						+ "\nPlease enter code on website to complete register");
				try {
					sender.send(message);
				} catch (Exception e) {
					System.out.println("createNewServices: " + e);
				}
				return ResponseEntity.ok(UserNotification.registerSuccess);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.usernameExists);
			}
		} catch (Exception e) {
			System.out.println("addAccount: " + e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.registerFail);
		}

	}

//	Confirm code
	@RequestMapping(value = "/register/activeEmail/{codeInput}/{username}", method = RequestMethod.POST)
    public ResponseEntity<?> activeAccount(@PathVariable int codeInput, @PathVariable String username) {
        boolean verify = true;
        Accounts account = accountService.findByUserName(username);
        try {
            if (codeInput == confirmCode) {
                account.setActive(true);
                accountService.save(account);
                return ResponseEntity.ok(verify);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(!verify);
    }

//	Banned account
	@RequestMapping(value = "/banned/{idAccount}", method = RequestMethod.POST)
	public ResponseEntity<?> isBanded(@PathVariable String idAccount) {
		try {
			Accounts account = accountService.findById(idAccount).get();
			if (account.isBanned() == IsBanned) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.isBanded);
			} else {
				account.setBanned(IsBanned);
				accountService.save(account);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.isBandedFail);
		}
		return ResponseEntity.ok(UserNotification.isBandedSuccess);
	}

//	Forget Password
	@RequestMapping(value = "/forgetPassword/{email}", method = RequestMethod.POST)
	public ResponseEntity<?> forgetPassword(@PathVariable String email) {
		Accounts accounts = accountService.findByEmail(email);
		try {
			if (email == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailNull);
			} else if (!email.matches(Validation.EMAIL_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.invalidEmailFormat);
			} else if (!accounts.getEmail().isEmpty() && accounts.isActive()) {
				SimpleMailMessage message = new SimpleMailMessage();
				message.setTo(accounts.getEmail());
				message.setSubject("Reset Password");
				message.setText("Wellcome " + accounts.getEmail() + "\nYour Password is: " + accounts.getPassword());
				try {
					sender.send(message);
				} catch (Exception e) {
					System.out.println("createNewServices: " + e);
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailNotExists);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.resetPasswordFail);
		}
		return ResponseEntity.ok(UserNotification.resetPasswordSuccess);
	}

//	Reset Password
	@RequestMapping(value = "/resetPassword/{email}/{password}", method = RequestMethod.POST)
	public ResponseEntity<?> resetPassword(@PathVariable String email, @PathVariable String password) {
		Accounts account = accountService.findByEmail(email);
		try {
			if (email == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailNull);
			} else if (!email.matches(Validation.EMAIL_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.invalidEmailFormat);
			} else if (password == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.passwordNull);
			} else if (!password.matches(Validation.PASSWORD_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.invalidPasswordFormat);
			} else if (!account.getEmail().isEmpty() && account.isActive()) {
				account.setPassword(new BCryptPasswordEncoder().encode(password) );
				accountService.save(account);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailNotExists);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserNotification.emailNotExists);
		}
		return ResponseEntity.ok(UserNotification.resetPasswordSuccess);
	}

//	Login
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginModel account) {
		return accountService.login(account);
	}

//	Log-out
	@DeleteMapping("/log-out")
	@PreAuthorize("hasAnyAuthority('user','admin')")
	public ResponseEntity<?> logout() {
		return accountService.logout();
	}
	
//ChangePassword
	@PutMapping("/changePassword")
	@PreAuthorize("hasAnyAuthority('user','admin')")
	public ResponseEntity<?> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword){
		return accountService.changePassword(oldPassword, newPassword);
	}
}
