package ces.riccico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.entity.User;

@Service
public interface UserService {

	ResponseEntity<?> editUser(User model, Integer userId);

	List<User> findAll();

	ResponseEntity<?> findById();

}
