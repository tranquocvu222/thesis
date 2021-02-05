package ces.riccico.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import ces.riccico.models.Accounts;
import ces.riccico.models.LoginModel;
import ces.riccico.security.AccountDetail;


public interface AccountService  {

	Accounts findByEmail(String email);

	Accounts findByUserName(String username);

	void delete(Accounts entity);

	void deleteById(String id);

	List<Accounts> findAll();

	Optional<Accounts> findById(String id);

	Accounts save(Accounts entity);

	ResponseEntity<?> logout();

	ResponseEntity<?> login(LoginModel account);

	AccountDetail loadUserByUsername(String username);
	
	ResponseEntity<?> changePassword(String oldPassword, String newPassword);



}
