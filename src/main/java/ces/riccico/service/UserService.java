package ces.riccico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import ces.riccico.entities.Users;

@Service
public interface UserService {
	
	ResponseEntity<?> editUser( Users model) ;
	
	List<Users> findAll();
	
	ResponseEntity<?> findById();
	
}
