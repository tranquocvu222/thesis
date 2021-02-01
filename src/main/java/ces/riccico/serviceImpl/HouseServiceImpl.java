package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.Accounts;
import ces.riccico.models.House;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.service.HouseService;
import ces.riccico.security.SecurityAuditorAware;

@Service
public class HouseServiceImpl implements HouseService {
	private static final boolean IS_APPROVED = false;
	@Autowired
	private HouseRepository houseRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private SecurityAuditorAware securityAuditorAware;
	
	@Override
	public List<House> getAll() {
		return houseRepository.findAll();
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
	public ResponseEntity<?> findHouseByUsername(String username) {
		String idAccount = accountRepository.findByUserName(username).getIdAccount();
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
	public ResponseEntity<?> postNewHouse(House house) {
		String idAccount = securityAuditorAware.getCurrentAuditor().get();
		Accounts account = accountRepository.findById(idAccount).get();
		if(idAccount == null || idAccount.isEmpty()) {
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must login");
		}
		//Random id
		UUID uuid = UUID.randomUUID();
		House houseNew = new House(); 
		houseNew.setId(uuid.toString());
		houseNew.setName(house.getName());
		houseNew.setAccount(account);
		houseNew.setAddress(house.getAddress());
		houseNew.setApproved(IS_APPROVED);
		houseNew.setPrice(house.getPrice());
		houseNew.setCity(house.getCity());
		houseNew.setCountry(house.getCountry());
		houseNew.setImage(house.getImage());
		houseNew.setLocation(house.getLocation());
		houseRepository.saveAndFlush(houseNew);
		return ResponseEntity.ok(houseNew);
	}

	@Override
	public ResponseEntity<?> deleteHouse(String houseId) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();

		return null;
	}

	@Override
	public ResponseEntity<?> updateHouse(String idHouse, House house) {
		return null;
	}

	@Override
	public ResponseEntity<?> updateStatus(String idHouse) {
		return null;
	}

}
