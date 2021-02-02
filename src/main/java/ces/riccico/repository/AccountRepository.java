package ces.riccico.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ces.riccico.models.Accounts;
import ces.riccico.models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import ces.riccico.models.Accounts;

public interface AccountRepository extends JpaRepository<Accounts, String>{
	Accounts findByUsername(String username);
	@Query(value="SELECT * FROM accounts a WHERE a.username = ?1", nativeQuery = true)
	List<Accounts> findByListUserName (String username);

}
