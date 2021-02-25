package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.Accounts;
import ces.riccico.models.Amenities;
import ces.riccico.models.House;
import ces.riccico.models.TypeAmenities;
import ces.riccico.models.TypeFeature;
import ces.riccico.models.TypeRoom;
import ces.riccico.notification.Notification;
import ces.riccico.notification.HouseNotification;
import ces.riccico.notification.UserNotification;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.AmenitiesRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.repository.TypeAmenitiesRepository;
import ces.riccico.repository.TypeFeatureReponsitory;
import ces.riccico.repository.TypeRoomRepository;
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
	
	@Autowired
	TypeRoomRepository typeRoomRepository;
	
	@Autowired
	TypeFeatureReponsitory typeFeatureRepository;
	
	@Autowired
	AmenitiesRepository amenitiesRepository;
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
				ResponseEntity.status(HttpStatus.NOT_FOUND).body(UserNotification.accountNotExist);
			}
			if (accountRepository.findById(idAccount).get().getHouses().size() == 0) {
				ResponseEntity.status(HttpStatus.NOT_FOUND).body(HouseNotification.houseNotExist);
			}
			Set<House> listHouses = new HashSet<House>();
			listHouses = accountRepository.findById(idAccount).get().getHouses();
			return ResponseEntity.ok(listHouses);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.fail);
		}
	}


	@Override
	public ResponseEntity<?> postNewHouse(House house) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Accounts account = accountRepository.findById(idCurrent).get();
		House houseNew = new House();
		try {
			if (idCurrent == null || idCurrent.isEmpty()) {
				ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.loginRequired);
			} else {
				if (house.getName().equals(null)) {
					ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HouseNotification.nameIsNull);
				} else if (house.getCountry().equals(null)) {
					ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HouseNotification.countryIsNull);
				} else if (house.getCity().equals(null)) {
					ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HouseNotification.cityIsNull);
				} else if (house.getAddress().equals(null)) {
					ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HouseNotification.addressIsNull);
				} else {
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
					houseRepository.saveAndFlush(houseNew);	
				}
			}
			return ResponseEntity.ok(houseNew);
		} catch (Exception e) {
			System.out.println(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("theem thaats baij");
		}


	}


	@Override
	public ResponseEntity<?> deleteHouse(Integer idHouse) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		House house = houseRepository.findById(idHouse).get();
		if (idCurrent == null || idCurrent.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.loginRequired);
		} else if (!houseRepository.findById(idHouse).isPresent()) {
			ResponseEntity.status(HttpStatus.NOT_FOUND).body(HouseNotification.houseNotExist);
		} else {
			if (house.isDeleted()) {
				return ResponseEntity.ok(HouseNotification.houseDeleted);
			} else if (accountRepository.findById(idCurrent).get().getRole().getRoleName().equals(ROLE_ADMIN)) {
//				houseRepository.deleteById(idHouse);
				house.setDeleted(IS_DELETED);
				houseRepository.saveAndFlush(house);
				return ResponseEntity.ok(HouseNotification.byAdmin);
			} else {
				if (!idCurrent.equals(houseRepository.findById(idHouse).get().getAccount().getIdAccount())) {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UserNotification.accountNotPermission);
				}
//				houseRepository.deleteById(idHouse);
				house.setDeleted(IS_DELETED);
				houseRepository.saveAndFlush(house);
				return ResponseEntity.ok(HouseNotification.byUser);
			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.fail);
	}


	@Override
	public ResponseEntity<?> updateHouse(Integer idHouse, House house) {
		try {
			String idCurrent = securityAuditorAware.getCurrentAuditor().get();
			if (idCurrent == null || idCurrent.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.loginRequired);
			} else if (!houseRepository.findById(idHouse).isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HouseNotification.houseNotExist);
			} else if (!houseRepository.findById(idHouse).get().getAccount().getIdAccount().equals(idCurrent)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UserNotification.accountNotPermission);
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
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.fail);
		}
	}


	@Override
	public ResponseEntity<?> approveHouse(Integer idHouse) {
		try {
			House house = houseRepository.findById(idHouse).get();
			if (house.isApproved()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HouseNotification.isApproved);
			} else {
				house.setApproved(IS_APPROVED);
				houseRepository.saveAndFlush(house);
				return ResponseEntity.ok(Notification.success);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.fail);
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
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.fail);
		}

	}
	

	
	
	@Override
	public ResponseEntity<?> createTypeRoom( Integer idHouse, Set<TypeRoom> setTypeRoom) {
		House house = houseRepository.findById(idHouse).get();
		try {
			if (house == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(Notification.message, HouseNotification.houseNotExist));
			}else {
			Set<TypeRoom> typeRoom = new HashSet<>();
			setTypeRoom.forEach(t ->{
				TypeRoom typeR  = typeRoomRepository.findById(t.getIdTyperoom()).get();
				typeRoom.add(typeR);});
				house.setTypeRoom(typeRoom);
				houseRepository.saveAndFlush(house);
			}
			return ResponseEntity.ok(Map.of(Notification.message,Notification.success));
		} catch (Exception e) {
			System.out.println(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
		}
	}
	
	@Override
	public ResponseEntity<?> createTypeFeature( Integer idHouse, Set<TypeFeature> setTypeFeature) {
		House house = houseRepository.findById(idHouse).get();
		try {
			if (house == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(Notification.message, HouseNotification.houseNotExist));
			}else {
			Set<TypeFeature> typeFeature = new HashSet<>();
			setTypeFeature.forEach(t ->{
				TypeFeature typeF  = typeFeatureRepository.findById(t.getIdFeature()).get();
				typeFeature.add(typeF);});
				house.setTypeFeature(typeFeature);
				houseRepository.saveAndFlush(house);
			}
			return ResponseEntity.ok(Map.of(Notification.message,Notification.success));
		} catch (Exception e) {
			System.out.println(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
		}
	}
	
	
	@Override
	public ResponseEntity<?> createAmenities( Integer idHouse, Set<Amenities> setAmenities) {
		House house = houseRepository.findById(idHouse).get();
		try {
			if (house == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(Notification.message, HouseNotification.houseNotExist));
			}else {
			Set<Amenities> amenities = new HashSet<>();
			setAmenities.forEach(t ->{
				Amenities amen  = amenitiesRepository.findById(t.getIdAmenities()).get();
				amenities.add(amen);});
				house.setAmenities(amenities);
				houseRepository.saveAndFlush(house);
			}
			return ResponseEntity.ok(Map.of(Notification.message,Notification.success));
		} catch (Exception e) {
			System.out.println(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
		}
	}

}
