package ces.riccico.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.Accounts;
import ces.riccico.models.Roles;
import ces.riccico.repository.AccountRepository;
import ces.riccico.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	AccountRepository ar;

	@Override
	public Accounts save(Accounts entity) {
		return ar.save(entity);
	}

	@Override
	public Optional<Accounts> findById(String id) {
		return ar.findById(id);
	}

	@Override
	public List<Accounts> findAll() {
		return (List<Accounts>) ar.findAll();
	}

	@Override
	public List<Accounts> findAllById(Iterable<String> ids) {
		return (List<Accounts>) ar.findAllById(ids);
	}

	@Override
	public void deleteById(String id) {
		ar.deleteById(id);
	}

	@Override
	public void delete(Accounts entity) {
		ar.delete(entity);
	}

	@Override
	public void deleteAll(List<Accounts> entities) {
		ar.deleteAll(entities);
	}

	@Override
	public void deleteAll() {
		ar.deleteAll();
	}

//	@Override
//	public List<Accounts> findByUserName(String username) {
//		return ar.findByUserName(username);
//	}
//
//	@Override
//	public List<Accounts> findByEmail(String email) {
//		return ar.findByEmail(email);
//	}

	@Override
	public Accounts findByUsername(String username) {
		return ar.findByUsername(username);
	}

	@Override
	public Accounts findByEmail(String email) {
		return ar.findByEmail(email);
	}

//	@Override
//	public List<Accounts> findByListEmail(String email) {
//		return ar.findByListEmail(email);
//	}

}
