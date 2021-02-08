package ces.riccico.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ces.riccico.models.Users;

public interface UserRepository extends CrudRepository<Users, Integer> {

	@Query(value = "SELECT * FROM users where users.id_account = ?1", nativeQuery = true)
	Optional<Users> findByIdAccount(String account);
}
