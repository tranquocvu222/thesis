

package ces.riccico.repository;

import org.springframework.stereotype.Repository;

import ces.riccico.entities.Accounts;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface AccountRepository extends JpaRepository<Accounts, Integer>{
	
	Accounts findByEmail(String email);

	Accounts findByUsername(String username);
	
//	@Query(value="SELECT * FROM accounts a WHERE a.username = ?1", nativeQuery = true)
//	List<Accounts> findByListUserName (String username);


}
