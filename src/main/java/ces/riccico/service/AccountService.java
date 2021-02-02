package ces.riccico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import ces.riccico.models.Accounts;
import ces.riccico.security.AccountDetail;

public interface AccountService {
	ResponseEntity<?> login(Accounts account);

	AccountDetail loadUserByUsername(String username);

	List<Accounts> getAll();
	
	ResponseEntity<?> logout();
}
