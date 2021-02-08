package ces.riccico.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ces.riccico.models.Users;

@Service
public interface UserService {

	Users save(Users entity);

	List<Users> findAll();

//
//	Users findByIdAccount(String account);

	Optional<Users> findByIdAccount(String account);

}
