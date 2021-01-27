package ces.riccico.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ces.riccico.models.Roles;
import ces.riccico.repository.RoleRepository;
import ces.riccico.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	RoleRepository rr;

	@Override
	public List<Roles> findByIdRole(String idrole) {
		return rr.findByIdRole(idrole);
	}

	@Override
	public Roles save(Roles entity) {
		return rr.save(entity);
	}

	@Override
	public Optional<Roles> findById(Integer id) {
		return rr.findById(id);
	}

	@Override
	public List<Roles> findAll() {
		return (List<Roles>)rr.findAll();
	}

	@Override
	public void deleteById(Integer id) {
		rr.deleteById(id);
	}

	@Override
	public void delete(Roles entity) {
		rr.delete(entity);
	}

	@Override
	public void deleteAll(List<Roles> entities) {
		rr.deleteAll(entities);
	}

	@Override
	public void deleteAll() {
		rr.deleteAll();
	}
	
	
}
