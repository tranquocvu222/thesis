package ces.riccico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


import ces.riccico.models.Roles;



public interface RoleRepository extends CrudRepository<Roles, Integer> {

	@Query(value = "SELECT * FROM roles r WHERE r.idRole = ?1", nativeQuery = true)
	List<Roles> findByIdRole(String idrole);
}
