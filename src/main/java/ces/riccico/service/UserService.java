package ces.riccico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import ces.riccico.entities.User;

@Service
public interface UserService {
	
	
	List<User> findAll();
	
	ResponseEntity<?> findById();

	ResponseEntity<?> editUser(User model, Integer userId);
	
}
