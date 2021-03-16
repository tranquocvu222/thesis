package ces.riccico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import ces.riccico.entities.Users;

@Service
public interface UserService {
	
	List<Users> findAll();
	ResponseEntity<?> editUser( Users model) ;
	ResponseEntity<?> findById();

}
