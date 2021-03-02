package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import ces.riccico.models.Message;
import ces.riccico.models.TypeFeature;
import ces.riccico.models.TypeRoom;
import ces.riccico.notification.Notification;
import ces.riccico.notification.HouseNotification;
import ces.riccico.notification.UserNotification;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.AmenitiesRepository;
import ces.riccico.repository.HouseRepository;
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
		Message message = new Message();
		try {
			Integer idAccount = accountRepository.findByUsername(username).getIdAccount();
			if (!accountRepository.findById(idAccount).isPresent()) {
				message.setMessage(UserNotification.accountNotExist);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			}
			if (accountRepository.findById(idAccount).get().getHouses().size() == 0) {
				message.setMessage(HouseNotification.houseNotExist);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			}
			Set<House> listHouses = new HashSet<House>();
			listHouses = accountRepository.findById(idAccount).get().getHouses();
			return ResponseEntity.ok(listHouses);
		} catch (Exception e) {
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

	}

	@Override
	public ResponseEntity<?> postNewHouse(House house) {
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Accounts account = accountRepository.findById(idCurrent).get();
		Message message = new Message();
		try {
			if (account== null) {
				message.setMessage(UserNotification.accountNotExist);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			} else {
				if (house.getTitle().equals(null)) {
					message.setMessage(HouseNotification.nameIsNull);
					ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (house.getCountry().equals(null)) {
					message.setMessage(HouseNotification.countryIsNull);
					ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (house.getProvince().equals(null)) {
					message.setMessage(HouseNotification.cityIsNull);
					ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (house.getAddress().equals(null)) {
					message.setMessage(HouseNotification.addressIsNull);
					ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else {
					House houseNew = new House();
					houseNew.setTitle(house.getTitle());
					houseNew.setAccount(account);
					houseNew.setAddress(house.getAddress());
					houseNew.setDeleted(NOT_DELETED);
					houseNew.setApproved(false);
					houseNew.setPrice(house.getPrice());
					houseNew.setProvince(house.getProvince());
					houseNew.setCountry(house.getCountry());
					houseNew.setImage(house.getImage());
					houseNew.setContent(house.getContent());
					houseNew.setSize(house.getSize());
					houseNew.setPhoneContact(house.getPhoneContact());
					houseRepository.saveAndFlush(houseNew);
					return ResponseEntity.status(HttpStatus.CREATED).body(houseNew);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		message.setMessage(Notification.fail);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}

	@Override
	public ResponseEntity<?> deleteHouse(int idHouse) {
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		House house = houseRepository.findById(idHouse).get();
		Message message = new Message();
		Accounts account = accountRepository.findById(idCurrent).get();
		if (account == null) {
			message.setMessage(UserNotification.accountNotExist);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
		} else if (!houseRepository.findById(idHouse).isPresent()) {
			message.setMessage(HouseNotification.houseNotExist);
			ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		} else {
			if (house.isDeleted()) {
				message.setMessage(HouseNotification.houseDeleted);
				return ResponseEntity.ok(message);
			} else if (accountRepository.findById(idCurrent).get().getRole().getRoleName().equals(ROLE_ADMIN)) {
//				houseRepository.deleteById(idHouse);
				house.setDeleted(IS_DELETED);
				houseRepository.saveAndFlush(house);
				message.setMessage(HouseNotification.byAdmin);
				return ResponseEntity.ok(message);
			} else {
				if (!idCurrent.equals(houseRepository.findById(idHouse).get().getAccount().getIdAccount())) {
					message.setMessage(UserNotification.accountNotPermission);
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
				}
//				houseRepository.deleteById(idHouse);
				house.setDeleted(IS_DELETED);
				houseRepository.saveAndFlush(house);
				message.setMessage(HouseNotification.byUser);
				return ResponseEntity.ok(message);
			}
		}
		message.setMessage(Notification.fail);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}

	@Override
	public ResponseEntity<?> updateHouse(int idHouse, House house) {
		Message message = new Message();
		try {
			Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
			Accounts account = accountRepository.findById(idCurrent).get();
			if (account == null) {
				message.setMessage(UserNotification.accountNotExist);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			} else if (!houseRepository.findById(idHouse).isPresent()) {
				message.setMessage(HouseNotification.houseNotExist);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			} else if (!houseRepository.findById(idHouse).get().getAccount().getIdAccount().equals(idCurrent)) {
				message.setMessage(UserNotification.accountNotPermission);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
			}
			House houseUpdate = houseRepository.findById(idHouse).get();
			houseUpdate.setTitle(house.getTitle());
			houseUpdate.setContent(house.getContent());
			houseUpdate.setImage(house.getImage());
			houseUpdate.setPrice(house.getPrice());
			houseUpdate.setSize(house.getSize());
			houseUpdate.setPhoneContact(house.getPhoneContact());
			houseRepository.saveAndFlush(houseUpdate);
			return ResponseEntity.ok(houseUpdate);
		} catch (Exception e) {
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public ResponseEntity<?> approveHouse(int idHouse) {
		Message message = new Message();
		try {
			House house = houseRepository.findById(idHouse).get();
			if (house.isApproved()) {
				message.setMessage(HouseNotification.isApproved);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else {
				house.setApproved(IS_APPROVED);
				message.setMessage(Notification.success);
				return ResponseEntity.ok(message);
			}
		} catch (Exception e) {
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public ResponseEntity<?> findByTitle(String title, int page, int size) {
		Message message = new Message();
		try {
			Pageable paging = PageRequest.of(page, size);
			if (title == null || title.isEmpty()) {
				return ResponseEntity.ok(houseRepository.findAll(paging).getContent());
			} else {
				return ResponseEntity.ok(houseRepository.findByTitle(title, paging).getContent());
			}
		} catch (Exception e) {
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

	}
	@Override
	public ResponseEntity<?> findByPageAndSize(int page, int size) {
		// TODO Auto-generated method stub
		Message message = new Message();
		try {
			Pageable paging = PageRequest.of(page, size);
			return ResponseEntity.ok(houseRepository.findList(paging).getContent());
		} catch (Exception e) {
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public ResponseEntity<?> createTypeRoom(Integer idHouse, Set<TypeRoom> setTypeRoom) {
		House house = houseRepository.findById(idHouse).get();
		Message message = new Message();
		try {
			if (house == null) {
				message.setMessage(HouseNotification.houseNotExist);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			} else {
				Set<TypeRoom> typeRoom = new HashSet<>();
				setTypeRoom.forEach(t -> {
					TypeRoom typeR = typeRoomRepository.findById(t.getIdTyperoom()).get();
					typeRoom.add(typeR);
				});
				house.setTypeRoom(typeRoom);
				houseRepository.saveAndFlush(house);
			}
			message.setMessage(Notification.success);
			return ResponseEntity.ok(message);
		} catch (Exception e) {
			System.out.println(e);
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public ResponseEntity<?> createTypeFeature(Integer idHouse, Set<TypeFeature> setTypeFeature) {
		House house = houseRepository.findById(idHouse).get();
		Message message = new Message();
		try {
			if (house == null) {
				message.setMessage(HouseNotification.houseNotExist);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			} else {
				Set<TypeFeature> typeFeature = new HashSet<>();
				setTypeFeature.forEach(t -> {
					TypeFeature typeF = typeFeatureRepository.findById(t.getIdFeature()).get();
					typeFeature.add(typeF);
				});
				house.setTypeFeature(typeFeature);
				houseRepository.saveAndFlush(house);
			}
			message.setMessage(Notification.success);
			return ResponseEntity.ok(message);
		} catch (Exception e) {
			System.out.println(e);
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public ResponseEntity<?> createAmenities(Integer idHouse, Set<Amenities> setAmenities) {
		House house = houseRepository.findById(idHouse).get();
		Message message = new Message();
		try {
			if (house == null) {
				message.setMessage(HouseNotification.houseNotExist);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			} else {
				Set<Amenities> amenities = new HashSet<>();
				setAmenities.forEach(t -> {
					Amenities amen = amenitiesRepository.findById(t.getIdAmenities()).get();
					amenities.add(amen);
				});
				house.setAmenities(amenities);
				houseRepository.saveAndFlush(house);
			}
			message.setMessage(Notification.success);
			return ResponseEntity.ok(message);
		} catch (Exception e) {
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public ResponseEntity<?> getHouseDetail(Integer idHouse) {
		House house = houseRepository.findById(idHouse).get();
		Message message = new Message();
		if (house == null) {
			message.setMessage(HouseNotification.houseNotExist);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}
		return ResponseEntity.ok(house);
	}


}