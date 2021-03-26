package ces.riccico.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ces.riccico.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {


	@Query(value = "SELECT * FROM users where users.account_id = ?1", nativeQuery = true)
	Optional<User> findByAccountId(int account);

}
