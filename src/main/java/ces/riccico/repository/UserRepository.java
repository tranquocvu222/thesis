package ces.riccico.repository;


import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ces.riccico.models.Users;

public interface UserRepository extends CrudRepository<Users, Integer> {

	@Query(value = "SELECT * FROM users", nativeQuery = true)
	List<Users> findAllUsers();
}
