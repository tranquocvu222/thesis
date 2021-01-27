package ces.riccico.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import ces.riccico.models.Accounts;
import ces.riccico.repository.AccountRepository;
import ces.riccico.security.AccountDetail;

public interface AccountService {


	ResponseEntity<?> login(Accounts account);


	void delete(Accounts entity);

	void deleteById(String id);

	List<Accounts> findAllById(Iterable<String> ids);

	List<Accounts> findAll();



	Accounts save(Accounts entity);

	AccountDetail loadUserByUsername(String username);


	Accounts findByUserName(String username);


	Optional<Accounts> findById(String id);


}
