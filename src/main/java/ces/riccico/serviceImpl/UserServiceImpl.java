package ces.riccico.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ces.riccico.models.Users;
import ces.riccico.repository.UserRepository;
import ces.riccico.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository ur;

	@Override
	public List<Users> findAllUsers() {
		return ur.findAllUsers();
	}

	@Override
	public Users save(Users entity) {
		return ur.save(entity);
	}

	@Override
	public Optional<Users> findById(Integer id) {
		return ur.findById(id);
	}

	@Override
	public List<Users> findAll() {
		return (List<Users>)ur.findAll();
	}

	@Override
	public Iterable<Users> findAllById(Iterable<Integer> ids) {
		return ur.findAllById(ids);
	}

	@Override
	public void deleteById(Integer id) {
		ur.deleteById(id);
	}

	@Override
	public void delete(Users entity) {
		ur.delete(entity);
	}

	@Override
	public void deleteAll(List<Users> entities) {
		ur.deleteAll(entities);
	}

	@Override
	public void deleteAll() {
		ur.deleteAll();
	}
	
	

	
	
}
