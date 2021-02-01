package ces.riccico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ces.riccico.models.Accounts;
import ces.riccico.models.Roles;

public interface AccountRepository extends CrudRepository<Accounts, String>  {

	@Query(value = "SELECT * FROM accounts a WHERE a.username = ?1", nativeQuery = true)
	List<Accounts> findByUsername(String username);
}
