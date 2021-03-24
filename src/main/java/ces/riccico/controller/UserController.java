
package ces.riccico.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.entities.User;
import ces.riccico.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@CrossOrigin
@RestController
public class UserController {

	@Autowired
	UserService userService;

	@RequestMapping(value = "/editUser/{userId}", method = RequestMethod.PUT)
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('user','admin')")
	public ResponseEntity<?> editUser(@RequestBody User model, @PathVariable Integer userId) {
		return userService.editUser(model, userId);
	}

	
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<User> findAll() {
		return userService.findAll();
	}

	
	@RequestMapping(value = "/userDetail", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('user','admin')")
	public ResponseEntity<?> userDetail() {
		return userService.findById();
	}
}
