package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.Accounts;
import ces.riccico.models.House;
import ces.riccico.notification.Notification;
import ces.riccico.notification.HouseNotification;
import ces.riccico.notification.UserNotification;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.service.HouseService;
import ces.riccico.security.SecurityAuditorAware;

@Service
public class HouseServiceImpl implements HouseService {

	private static final boolean IS_APPROVED = true;
	private static final boolean NOT_DELETED = false;
	private static final boolean IS_DELETED = true;
	private static final String ROLE_ADMIN = "admin";
	
	@Autowired
	private HouseRepository houseRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private SecurityAuditorAware securityAuditorAware;

	@Override
	public List<House> getAll() {
		List<House> listHouse = new ArrayList<House>();
		for (House house : houseRepository.findAll()) {
			if (!house.isDeleted()) {
				listHouse.add(house);
			}
		}
		return houseRepository.findAll();
	}

	@Override
	public List<House> getAllApproved() {
		List<House> listApproved = new ArrayList<House>();
		for (House house : houseRepository.findAll()) {
			if (house.isApproved() && !house.isDeleted()) {
				listApproved.add(house);
			}
		}
		return listApproved;
	}

	@Override
	public List<House> getAllNotApproved() {
		List<House> listNotApproved = new ArrayList<House>();
		for (House house : houseRepository.findAll()) {
			if (!house.isApproved() && !house.isDeleted()) {
				listNotApproved.add(house);
			}
		}
		return listNotApproved;
	}

	@Override
	public List<House> getAllDeleted() {
		List<House> listDeleted = new ArrayList<House>();
		for (House house : houseRepository.findAll()) {
			if (house.isDeleted()) {
				listDeleted.add(house);
			}
		}
		return listDeleted;
	}

	@Override
	public ResponseEntity<?> findHouseByUsername(String username) {
		try {
			String idAccount = accountRepository.findByUsername(username).getIdAccount();
			if (!accountRepository.findById(idAccount).isPresent()) {
				ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(Notification.message,UserNotification.accountNotExist));
			}
			if (accountRepository.findById(idAccount).get().getHouses().size() == 0) {
				ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(Notification.message,HouseNotification.houseNotExist));
			}
			Set<House> listHouses = new HashSet<House>();
			listHouses = accountRepository.findById(idAccount).get().getHouses();
			return ResponseEntity.ok(listHouses);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
		}
	}

	@Override
	public ResponseEntity<?> postNewHouse(House house) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Accounts account = accountRepository.findById(idCurrent).get();
		if (idCurrent == null || idCurrent.isEmpty()) {
			ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(Notification.message,Notification.loginRequired));
		} else {
			if (house.getName().equals(null)) {
				ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,HouseNotification.nameIsNull));
			} else if (house.getCountry().equals(null)) {
				ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,HouseNotification.countryIsNull));
			} else if (house.getCity().equals(null)) {
				ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,HouseNotification.cityIsNull));
			} else if (house.getAddress().equals(null)) {
				ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,HouseNotification.addressIsNull));
			} else {
				House houseNew = new House();
				houseNew.setName(house.getName());
				houseNew.setAccount(account);
				houseNew.setAddress(house.getAddress());
				houseNew.setDeleted(NOT_DELETED);
				houseNew.setPrice(house.getPrice());
				houseNew.setCity(house.getCity());
				houseNew.setCountry(house.getCountry());
				houseNew.setImage(house.getImage());
				houseNew.setLocation(house.getLocation());
				houseNew.setIntroduce(house.getIntroduce());
				houseNew.setAcreage(house.getAcreage());
				houseRepository.saveAndFlush(houseNew);
				return ResponseEntity.status(HttpStatus.CREATED).body(houseNew);
			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
	}

	@Override
	public ResponseEntity<?> deleteHouse(int idHouse) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		House house = houseRepository.findById(idHouse).get();
		if (idCurrent == null || idCurrent.isEmpty()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(Notification.message,Notification.loginRequired));
		} else if (!houseRepository.findById(idHouse).isPresent()) {
			ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(Notification.message,HouseNotification.houseNotExist));
		} else {
			if (house.isDeleted()) {
				return ResponseEntity.ok(HouseNotification.houseDeleted);
			} else if (accountRepository.findById(idCurrent).get().getRole().getRoleName().equals(ROLE_ADMIN)) {
//				houseRepository.deleteById(idHouse);
				house.setDeleted(IS_DELETED);
				houseRepository.saveAndFlush(house);
				return ResponseEntity.ok(Map.of(Notification.message,HouseNotification.byAdmin));
			} else {
				if (!idCurrent.equals(houseRepository.findById(idHouse).get().getAccount().getIdAccount())) {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(Notification.message,UserNotification.accountNotPermission));
				}
//				houseRepository.deleteById(idHouse);
				house.setDeleted(IS_DELETED);
				houseRepository.saveAndFlush(house);
				return ResponseEntity.ok(HouseNotification.byUser);
			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
	}

	@Override
	public ResponseEntity<?> updateHouse(int idHouse, House house) {
		try {
			String idCurrent = securityAuditorAware.getCurrentAuditor().get();
			if (idCurrent == null || idCurrent.isEmpty()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(Notification.message,Notification.loginRequired));
			} else if (!houseRepository.findById(idHouse).isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(Notification.message,HouseNotification.houseNotExist));
			} else if (!houseRepository.findById(idHouse).get().getAccount().getIdAccount().equals(idCurrent)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(Notification.message,UserNotification.accountNotPermission));
			}
			House houseUpdate = houseRepository.findById(idHouse).get();
			houseUpdate.setName(house.getName());
			houseUpdate.setAddress(house.getAddress());
			houseUpdate.setCity(house.getCity());
			houseUpdate.setImage(house.getImage());
			houseUpdate.setPrice(house.getPrice());
			houseRepository.saveAndFlush(houseUpdate);
			return ResponseEntity.ok(houseUpdate);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
		}
	}

	@Override
	public ResponseEntity<?> approveHouse(int idHouse) {
		try {
			House house = houseRepository.findById(idHouse).get();
			if (house.isApproved()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,HouseNotification.isApproved));
			} else {
				house.setApproved(IS_APPROVED);
				houseRepository.saveAndFlush(house);
				return ResponseEntity.ok(Map.of(Notification.message,Notification.success));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
		}
	}

	@Override
	public ResponseEntity<?> findByHouseName(String houseName, int page, int size) {
		try {
			Pageable paging = PageRequest.of(page, size);
			if (houseName == null || houseName.isEmpty()) {
				return ResponseEntity.ok(houseRepository.findAll(paging).getContent());
			} else {
				return ResponseEntity.ok(houseRepository.findByName(houseName, paging).getContent());
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
		}

	}

}
