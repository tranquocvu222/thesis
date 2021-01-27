package ces.riccico.controller;

import java.util.ArrayList;
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
import ces.riccico.service.RoleService;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
public class AccountController {

	@Autowired
	AccountService as;
	
	@Autowired
	RoleService rs;
	
	@RequestMapping(value = "/account", method = RequestMethod.GET)
	public List<Accounts> getAll() {
		try {
			List<Accounts> listAccount = new ArrayList<Accounts>();
			listAccount = as.findAll();
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
			rs.findAll();
		    model.setRole(rs.findAll().get(1));
		    model.setBanded(false);
			model.setIdAccount(String.valueOf(uuid));
			as.save(model);
		} catch (Exception e) {
			System.out.println("addAccount: " + e);
		}
	}
}
