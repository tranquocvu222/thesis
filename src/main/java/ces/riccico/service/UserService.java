package ces.riccico.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import ces.riccico.models.Users;

@Service
public interface UserService {
	
	List<Users> findAll();
	ResponseEntity<?> editUser( Users model) ;

}
