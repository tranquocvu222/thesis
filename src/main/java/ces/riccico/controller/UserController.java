package ces.riccico.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ces.riccico.models.Users;
import ces.riccico.service.UserService;

@CrossOrigin
@RestController
public class UserController {

	@Autowired
	UserService userService;


	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public List<Users> findAll() {
		return userService.findAll();
	}

	@RequestMapping(value = "/editUser", method = RequestMethod.POST)
	public ResponseEntity<?> editUser(@RequestBody Users model) {
		return userService.editUser(model);

}
}
