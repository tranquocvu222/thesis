package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.House;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.service.HouseService;
import ces.riccico.security.SecurityAuditorAware;

@Service
public class HouseServiceImpl implements HouseService {

	@Autowired
	private HouseRepository houseRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private SecurityAuditorAware securityAuditorAware;
	
	@Override
	public List<House> getAll() {
		return null;
	}
	
	@Override
	public List<House> getAllApproved() {
		List<House> listApproved = new ArrayList<House>();
		for(House house : houseRepository.findAll()) {
			if(house.isApproved()) {
				listApproved.add(house);
			}
		}
		return listApproved;
	}

	@Override
	public List<House> getAllNotApproved() {
		List<House> listNotApproved = new ArrayList<House>();
		for(House house : houseRepository.findAll()) {
			if(!house.isApproved()) {
				listNotApproved.add(house);
			}
		}
		return listNotApproved;
	}

	@Override
	public ResponseEntity<?> findByAccountId(String idAccount) {
		if(idAccount.toString() == null || idAccount.isEmpty()) {
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
		}
		if(!accountRepository.findById(idAccount).isPresent()) {
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found user");
		}
		if(accountRepository.findById(idAccount).get().getHouses().size() == 0) {
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found house");
		}
		Set<House> listHouses = new HashSet<House>();
		listHouses = accountRepository.findById(idAccount).get().getHouses();
		return ResponseEntity.ok(listHouses);
	}

	@Override
	public ResponseEntity<?> save(House house) {
		return null;
	}

	@Override
	public ResponseEntity<?> delete(String houseId) {
		return null;
	}

	@Override
	public ResponseEntity<?> update(String idHouse, House house) {
		return null;
	}

	@Override
	public ResponseEntity<?> updateStatus(String idHouse) {
		return null;
	}

}
