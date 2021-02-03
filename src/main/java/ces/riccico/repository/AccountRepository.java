package ces.riccico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ces.riccico.models.Accounts;


public interface AccountRepository extends CrudRepository<Accounts, String>  {

	Accounts findByUsername(String username);
	
	Accounts findByEmail(String email);
	
//	@Query(value = "SELECT * FROM accounts a WHERE a.username = ?1", nativeQuery = true)
//	List<Accounts> findByUserName(String username);
//	
//	@Query(value = "SELECT * FROM accounts a WHERE a.email = ?1", nativeQuery = true)
//	List<Accounts> findByListEmail(String email);

}
