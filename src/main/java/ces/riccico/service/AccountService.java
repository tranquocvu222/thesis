package ces.riccico.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import ces.riccico.models.Accounts;
import ces.riccico.models.LoginModel;
import ces.riccico.models.Users;
import ces.riccico.security.AccountDetail;

public interface AccountService {
	

	ResponseEntity<?> register(Accounts account, Users user);

	ResponseEntity<?> login(LoginModel account);

	ResponseEntity<?> logout();

	AccountDetail loadUserByUsername(String username);

	ResponseEntity<?> changePassword(String oldPassword, String newPassword);

	List<Accounts> findAll();

	List<Accounts> findAllIsBanned();

	ResponseEntity<?> activeAccount(int codeInput, String email);

	ResponseEntity<?> banAccount(String idAccount);

	ResponseEntity<?> forgetPassword(String email);

	ResponseEntity<?> resetPassword(String email, String password);

	Optional<Accounts> findById(String id);

}
