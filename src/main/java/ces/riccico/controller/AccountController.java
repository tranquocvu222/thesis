package ces.riccico.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.models.Accounts;
import ces.riccico.service.AccountService;


import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.models.Accounts;
import ces.riccico.models.Roles;
import ces.riccico.service.AccountService;

@CrossOrigin
@RestController
public class AccountController {

	@Autowired
	AccountService accountService;
	
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
	
	@RequestMapping(value = "/account/new", method = RequestMethod.POST)
	public void addAdmin(@RequestBody Accounts model) {
		try {
			UUID uuid = UUID.randomUUID();
			model.setIdAccount(String.valueOf(uuid));
			accountService.save(model);
		} catch (Exception e) {
			System.out.println("addAccount: " + e);
		}

	}
	@CrossOrigin
	@PostMapping("/login")
	public ResponseEntity<?> login (@RequestBody Accounts account){
		return accountService.login(account);
	}
	
}
