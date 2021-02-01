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

	private static final boolean NOT_APPROVED = false;
	private static final boolean IS_APPROVED = true;
	private static final String ROLE_ADMIN = "admin";

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
		for (House house : houseRepository.findAll()) {
			if (house.isApproved()) {
				listApproved.add(house);
			}
		}
		return listApproved;
	}

	@Override
	public List<House> getAllNotApproved() {
		List<House> listNotApproved = new ArrayList<House>();
		for (House house : houseRepository.findAll()) {
			if (!house.isApproved()) {
				listNotApproved.add(house);
			}
		}
		return listNotApproved;
	}

	@Override
	public ResponseEntity<?> findHouseByUsername(String username) {
		String idAccount = accountRepository.findByUserName(username).getIdAccount();
		if (idAccount.toString() == null || idAccount.isEmpty()) {
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
		}
		if (!accountRepository.findById(idAccount).isPresent()) {
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found user");
		}
		if (accountRepository.findById(idAccount).get().getHouses().size() == 0) {
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found house");
		}
		Set<House> listHouses = new HashSet<House>();
		listHouses = accountRepository.findById(idAccount).get().getHouses();
		return ResponseEntity.ok(listHouses);
	}

	@Override
	public ResponseEntity<?> postNewHouse(House house) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Accounts account = accountRepository.findById(idCurrent).get();
		if (idCurrent == null || idCurrent.isEmpty()) {
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must login");
		}
		// Random id
		UUID uuid = UUID.randomUUID();
		House houseNew = new House();
		houseNew.setId(uuid.toString());
		houseNew.setName(house.getName());
		houseNew.setAccount(account);
		houseNew.setAddress(house.getAddress());
		houseNew.setApproved(NOT_APPROVED);
		houseNew.setPrice(house.getPrice());
		houseNew.setCity(house.getCity());
		houseNew.setCountry(house.getCountry());
		houseNew.setImage(house.getImage());
		houseNew.setLocation(house.getLocation());
		houseRepository.saveAndFlush(houseNew);
		return ResponseEntity.ok(houseNew);
	}

	@Override
	public ResponseEntity<?> deleteHouse(String idHouse) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		if (idCurrent == null || idCurrent.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must login");
		} else if (!houseRepository.findById(idHouse).isPresent()) {
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("House doesn't exist");
		} else {
			if (accountRepository.findById(idCurrent).get().getRole().getRoleName().equals(ROLE_ADMIN)) {
				houseRepository.deleteById(idHouse);
				return ResponseEntity.ok("delete success by admin");
			} else {
				if (!idCurrent.equals(houseRepository.findById(idHouse).get().getAccount().getIdAccount())) {
					return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("You don't have permission");
				}
				houseRepository.deleteById(idHouse);
				return ResponseEntity.ok("delete success by user");
			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
	}

	@Override
	public ResponseEntity<?> updateHouse(String idHouse, House house) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		if (idCurrent == null || idCurrent.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must login");
		} else if (!houseRepository.findById(idHouse).isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("House doesn't exist");
		} else if (!houseRepository.findById(idHouse).get().getAccount().getIdAccount().equals(idCurrent)) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("You don't have permission");
		}
		House houseUpdate = houseRepository.findById(idHouse).get();
		houseUpdate.setName(house.getName());
		houseUpdate.setAddress(house.getAddress());
		houseUpdate.setCity(house.getCity());
		houseUpdate.setImage(house.getImage());
		houseUpdate.setPrice(house.getPrice());
		houseRepository.saveAndFlush(houseUpdate);
		return ResponseEntity.ok(houseUpdate);
	}

	@Override
	public ResponseEntity<?> approveHouse(String idHouse) {
		House house = houseRepository.findById(idHouse).get();
		if(house.isApproved()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("House was approved");
		}
		house.setApproved(IS_APPROVED);
		houseRepository.saveAndFlush(house);
		return ResponseEntity.ok("approve success");
	}
}
