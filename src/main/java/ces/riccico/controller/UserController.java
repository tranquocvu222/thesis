package ces.riccico.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.entities.Users;
import ces.riccico.service.UserService;

@CrossOrigin
@RestController
public class UserController {

	@Autowired
	UserService userService;
	
	
	@RequestMapping(value = "/editUser", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('user','admin')")
	public ResponseEntity<?> editUser(@RequestBody Users model) {
		return userService.editUser(model);
	}

	
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<Users> findAll() {
		return userService.findAll();
	}

	
	@RequestMapping(value = "/userDetail", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('admin','user')")
	public ResponseEntity<?> userDetail() {
		return userService.findById();
	}
}
