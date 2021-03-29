
package ces.riccico.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.entity.Account;
import ces.riccico.entity.User;
import ces.riccico.model.LoginModel;

import ces.riccico.service.AccountService;
import ces.riccico.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@CrossOrigin
@RestController
public class AccountController {

	@Autowired
	AccountService accountService;

	@Autowired
	UserService userService;

	// this is the account authentication feature.
	@RequestMapping(value = "/register/activeEmail/{codeInput}/{email}", method = RequestMethod.PUT)
	public ResponseEntity<?> activeAccount(@PathVariable int codeInput, @PathVariable String email) {
		return accountService.activeAccount(codeInput, email);
	}

	// this is change password feature, can change the password
	@PutMapping("/changePassword")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('admin','user')")
	public ResponseEntity<?> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
		return accountService.changePassword(oldPassword, newPassword);
	}

	// shows banned accounts list
	@RequestMapping(value = "/accounts/isbanned", method = RequestMethod.GET)
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<Account> findAllIsBanned() {
		return accountService.findAllIsBanned();
	}

	// find house with pagination
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> findByPageAndSize(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return accountService.findByPageAndSize(page, size);
	}

	// this is a forgotten password feature, which helps you recover your password
	// when you forget it
	@RequestMapping(value = "/forgetPassword/{email}", method = RequestMethod.POST)
	public ResponseEntity<?> forgetPassword(@PathVariable String email) {
		return accountService.forgetPassword(email);
	}

	// prevent users from using the system
	@RequestMapping(value = "/banned/{accountId}", method = RequestMethod.PUT)
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> isBanneed(@PathVariable int accountId) {
		return accountService.banAccount(accountId);
	}

	// this is a login feature that helps you log into the system
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginModel account) {
		return accountService.login(account);
	}

	// this is logout feature
	@DeleteMapping("/log-out")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('admin','user')")
	public ResponseEntity<?> logout() {
		return accountService.logout();
	}

	// this is the registration feature,you can create an account to use system's
	// feature
	@ResponseBody
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> register(@RequestBody Account account, User user) {
		return accountService.register(account, user);
	}

	// this is reset password feature
	@RequestMapping(value = "/resetPassword/{email}/{password}", method = RequestMethod.PUT)
	public ResponseEntity<?> resetPassword(@PathVariable String email, @PathVariable String password) {
		return accountService.resetPassword(email, password);
	}

}
