package ces.riccico.service;

import java.util.List;
import java.util.Optional;

import ces.riccico.models.Roles;

public interface RoleService  {

	void deleteAll();

	void deleteAll(List<Roles> entities);

	void delete(Roles entity);

	void deleteById(Integer id);

	List<Roles> findAll();

	Optional<Roles> findById(Integer id);

	Roles save(Roles entity);

	List<Roles> findByIdRole(String idrole);

	
	
}
