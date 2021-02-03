package ces.riccico.repository;


import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ces.riccico.models.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface AccountRepository extends JpaRepository<Accounts, String>{
	Accounts findByUsername(String username);
	
	Accounts findByEmail(String email);
	
	@Query(value="SELECT * FROM accounts a WHERE a.username = ?1", nativeQuery = true)
	List<Accounts> findByListUserName (String username);

}
