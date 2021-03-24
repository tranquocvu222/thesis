

package ces.riccico.repository;

import org.springframework.stereotype.Repository;

import ces.riccico.entities.Account;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>{
	
	Account findByEmail(String email);

	Account findByUsername(String username);
	
//	@Query(value="SELECT * FROM accounts a WHERE a.username = ?1", nativeQuery = true)
//	List<Accounts> findByListUserName (String username);


}
