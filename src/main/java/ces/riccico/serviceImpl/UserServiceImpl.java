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
	UserRepository userRepository;

	@Override
	public Users save(Users entity) {
		return userRepository.save(entity);
	}

	@Override
	public List<Users> findAll() {
		return (List<Users>) userRepository.findAll();
	}

	@Override
	public Optional<Users> findByIdAccount(String account) {
		return userRepository.findByIdAccount(account);
	}


}
