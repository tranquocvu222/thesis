
package ces.riccico.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import ces.riccico.entities.Accounts;
import ces.riccico.models.LoginModel;
import ces.riccico.entities.User;
import ces.riccico.security.AccountDetail;

public interface AccountService {
	
	ResponseEntity<?> activeAccount(int codeInput, String email);

	ResponseEntity<?> banAccount(int idAccount);
	
	ResponseEntity<?> changePassword(String oldPassword, String newPassword);
	
	List<Accounts> findAll();

	List<Accounts> findAllIsBanned();
	
	Optional<Accounts> findById(int id);

	ResponseEntity<?> forgetPassword(String email);

	ResponseEntity<?> login(LoginModel account);

	ResponseEntity<?> logout();

	AccountDetail loadUserByUsername(String username);

	ResponseEntity<?> register(Accounts account, User user);

	ResponseEntity<?> resetPassword(String email, String password);

	


}
