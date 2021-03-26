package ces.riccico.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ces.riccico.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

<<<<<<< HEAD
	@Query(value = "SELECT * FROM users where users.id_account = ?1", nativeQuery = true)
	Optional<User> findByIdAccount(int account);
=======
	@Query(value = "SELECT * FROM users where users.account_id = ?1", nativeQuery = true)
	Optional<User> findByAccountId(int account);
>>>>>>> codingstandards
}
