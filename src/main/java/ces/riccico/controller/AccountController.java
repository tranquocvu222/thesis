package ces.riccico.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.models.Accounts;
import ces.riccico.service.AccountService;

@RestController
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@PostMapping("/login")
	public ResponseEntity<?> login (@RequestBody Accounts account){
		return accountService.login(account);
	}
	
	@GetMapping("/allAccounts")
	public List<Accounts> getAll(){
		return accountService.getAll();
	}
}
