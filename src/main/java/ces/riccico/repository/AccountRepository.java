

package ces.riccico.repository;

import org.springframework.stereotype.Repository;

import ces.riccico.entity.Account;
import ces.riccico.entity.House;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

	Account findByEmail(String email);

	Account findByUsername(String username);
	
	@Query(value="select a.account_id, a.email, a.is_active, a.is_banned, a.role, a.username, a.password, u.address, u.birth_day, u.city, u.country, u.first_name\r\n"
			+ ",u.last_name from accounts a join users u on a.account_id = u.account_id ", nativeQuery = true)
	Page<Account> getAllAccount(Pageable pageable);
	
	@Query(value="select count(h.house_id) from houses h join accounts a on h.account_id = a.account_id where a.account_id = ?1 ", nativeQuery = true)
	Integer countHouse(int accountId);
	
	@Query("Select a from Account a where a.isBanned =?1")
	Page<Account> getAccountsForAdmin(boolean status, Pageable pageable);

//	@Query(value="SELECT * FROM accounts a WHERE a.username = ?1", nativeQuery = true)
//	List<Accounts> findByListUserName (String username);

}
