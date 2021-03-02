package ces.riccico.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ces.riccico.models.Users;

public interface UserRepository extends JpaRepository<Users, Integer> {

	@Query(value = "SELECT * FROM users where users.id_account = ?1", nativeQuery = true)
	Optional<Users> findByIdAccount(int account);
}
