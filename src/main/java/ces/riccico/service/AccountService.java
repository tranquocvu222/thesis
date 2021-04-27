

package ces.riccico.service;

import org.springframework.http.ResponseEntity;
import ces.riccico.entity.Account;
import ces.riccico.entity.User;
import ces.riccico.model.LoginModel;
import ces.riccico.security.AccountDetail;

public interface AccountService {
	
	ResponseEntity<?> activeAccount(int codeInput, String email);

	ResponseEntity<?> banAccount(int accountId);
	
	ResponseEntity<?> changePassword(String oldPassword, String newPassword);

//	ResponseEntity<?> findAllIsBanned();

	ResponseEntity<?> forgetPassword(String email);

	ResponseEntity<?> login(LoginModel account);

	ResponseEntity<?> logout();

	AccountDetail loadUserByUsername(String username);

	ResponseEntity<?> register(Account account, User user);

	ResponseEntity<?> resetPassword(String email, String password);

	ResponseEntity<?> getStatisticOwner(int accountId);

	ResponseEntity<?> findAllAccountPageAndSize(String status, int page, int size);

}
