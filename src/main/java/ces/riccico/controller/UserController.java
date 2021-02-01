package ces.riccico.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.models.Accounts;
import ces.riccico.models.Users;
import ces.riccico.service.AccountService;
import ces.riccico.service.UserService;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
public class UserController {

	@Autowired
	UserService us;
	
	@Autowired
	AccountService as;
	
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public List<Users> getAll() {
		try {
			List<Users> listUsers = new ArrayList<Users>();
			listUsers = us.findAllUsers();
			return listUsers;
		} catch (Exception e) {
			System.out.println("getAll: " + e);
			return null;
		}
	}
	
	@RequestMapping(value = "/user/new", method = RequestMethod.POST)
	public void addUsers(@RequestBody Users model) {
		try {
//			List<Accounts> listAccount = as.findAll();
//			model.setAccount(listAccount.);
//			System.out.println("addUsers: " + model);
			us.save(model);
		} catch (Exception e) {
			System.out.println("addUsers: " + e);
		}
	}
}
