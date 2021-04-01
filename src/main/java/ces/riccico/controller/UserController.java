package ces.riccico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.entity.User;
import ces.riccico.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@CrossOrigin
@RestController
public class UserController {

	@Autowired
	UserService userService;
	
	@PutMapping("/editUser/{userId}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	public ResponseEntity<?> editUser(@RequestBody User model, @PathVariable Integer userId) {
		return userService.editUser(model, userId);
	}
	
	@GetMapping("/users")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	public ResponseEntity<?> findAll() {
		return userService.findAll();
	}

	
	@GetMapping("/userDetail")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	public ResponseEntity<?> userDetail() {
		return userService.findById();
	}
}
