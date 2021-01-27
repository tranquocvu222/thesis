package ces.riccico.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ces.riccico.models.Accounts;

@Service
public interface AccountService  {

	void deleteAll();

	void deleteAll(List<Accounts> entities);

	void delete(Accounts entity);

	void deleteById(String id);

	List<Accounts> findAllById(Iterable<String> ids);

	List<Accounts> findAll();

	Optional<Accounts> findById(String id);

	Accounts save(Accounts entity);

}
