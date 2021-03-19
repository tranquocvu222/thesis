package ces.riccico.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.entities.Accounts;
import ces.riccico.models.Role;
import ces.riccico.entities.Users;
import ces.riccico.models.LoginModel;
import ces.riccico.service.AccountService;
import ces.riccico.service.UserService;

@CrossOrigin
@RestController
public class AccountController {

	@Autowired
	AccountService accountService;

	@Autowired
	UserService userService;

	// this is the account authentication feature
	@RequestMapping(value = "/register/activeEmail/{codeInput}/{email}", method = RequestMethod.POST)
	public ResponseEntity<?> activeAccount(@PathVariable int codeInput, @PathVariable String email) {
		return accountService.activeAccount(codeInput, email);
	}

	// this is change password feature,  can change the password
	@PutMapping("/changePassword")
	@PreAuthorize("hasAnyAuthority('admin','user')")
	public ResponseEntity<?> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
		return accountService.changePassword(oldPassword, newPassword);
	}

	// shows the list of accounts 
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<Accounts> findAll() {
		return accountService.findAll();
	}

	// shows banned accounts list
	@RequestMapping(value = "/accounts/isbanned", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<Accounts> findAllIsBanned() {
		return accountService.findAllIsBanned();
	}

	// this is a forgotten password feature, which helps you recover your password when you forget it
	@RequestMapping(value = "/forgetPassword/{email}", method = RequestMethod.POST)
	public ResponseEntity<?> forgetPassword(@PathVariable String email) {
		return accountService.forgetPassword(email);
	}

	// prevent users from using the system
	@RequestMapping(value = "/banned/{idAccount}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> isBanneed(@PathVariable int idAccount) {
		return accountService.banAccount(idAccount);
	}

	// this is a login feature that helps you log into the system
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginModel account) {
		return accountService.login(account);
	}

	// this is logout feature
	@DeleteMapping("/log-out")
	@PreAuthorize("hasAnyAuthority('admin','user')")
	public ResponseEntity<?> logout() {
		return accountService.logout();
	}

	// this is the registration feature,you can create an account to use system's feature
	@ResponseBody
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> register(@RequestBody Accounts account, Users user) {
		return accountService.register(account, user);
	}

	// this is reset password feature
	@RequestMapping(value = "/resetPassword/{email}/{password}", method = RequestMethod.POST)
	public ResponseEntity<?> resetPassword(@PathVariable String email, @PathVariable String password) {
		return accountService.resetPassword(email, password);
	}

}
