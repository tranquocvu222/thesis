package ces.riccico.service;

<<<<<<< HEAD
import java.util.List;
import java.util.Optional;

import ces.riccico.models.Users;

public interface UserService {

	void deleteAll();

	void deleteAll(List<Users> entities);

	void delete(Users entity);

	void deleteById(Integer id);

	Iterable<Users> findAllById(Iterable<Integer> ids);

	List<Users> findAll();

	Optional<Users> findById(Integer id);

	Users save(Users entity);

	List<Users> findAllUsers();


=======
public interface UserService {

>>>>>>> e4ef1c1... set up user service
}
