package ces.riccico.repository;


import org.springframework.data.jpa.repository.JpaRepository;


import ces.riccico.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

//	@Query(value = "SELECT * FROM users where users.account_id = ?1", nativeQuery = true)
//	Optional<User> findByAccountId(int account);
//	
//	@Query(value = "SELECT * FROM users where users.user_id = ?1", nativeQuery = true)
//	User findByUserId(int userId);
	
	User findByAccountId (int accountId);

}
