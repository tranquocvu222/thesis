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
import ces.riccico.models.Accounts;
import ces.riccico.models.LoginModel;
import ces.riccico.service.AccountService;
import ces.riccico.models.Users;
import ces.riccico.service.UserService;

@CrossOrigin
@RestController
public class AccountController {

	@Autowired
	AccountService accountService;

	@Autowired
	UserService userService;

//	Confirm code
	@RequestMapping(value = "/register/activeEmail/{codeInput}/{username}", method = RequestMethod.POST)
	public ResponseEntity<?> activeAccount(@PathVariable int codeInput, @PathVariable String username) {
		return accountService.activeAccount(codeInput, username);
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

//	Forget Password
	@RequestMapping(value = "/forgetPassword/{email}", method = RequestMethod.POST)
	public ResponseEntity<?> forgetPassword(@PathVariable String email) {
		return accountService.forgetPassword(email);
	}

//	Reset Password
	@RequestMapping(value = "/resetPassword/{email}/{password}", method = RequestMethod.POST)
	public ResponseEntity<?> resetPassword(@PathVariable String email, @PathVariable String password) {
		return accountService.resetPassword(email, password);
	}

//ChangePassword
	@PutMapping("/changePassword")
	@PreAuthorize("hasAnyAuthority('user','admin')")
	public ResponseEntity<?> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
		return accountService.changePassword(oldPassword, newPassword);
	}

//	Show list account is not bannded
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public List<Accounts> findAll() {
		return accountService.findAll();
	}

//	Banned account
	@RequestMapping(value = "/banned/{idAccount}", method = RequestMethod.POST)
	public ResponseEntity<?> isBanded(@PathVariable String idAccount) {
		return accountService.banAccount(idAccount);
	}

//	Show list account is bannded
	@RequestMapping(value = "/accounts/isbanned", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<Accounts> findAllIsBanned() {
		return accountService.findAllIsBanned();
	}

	// Register
	@ResponseBody
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> register(@RequestBody Accounts account, Users user) {
		return accountService.register(account, user);
	}
}
