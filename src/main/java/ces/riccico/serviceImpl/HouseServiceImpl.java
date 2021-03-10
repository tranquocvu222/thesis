package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.notification.Notification;
import ces.riccico.entities.Accounts;
import ces.riccico.entities.House;
import ces.riccico.entities.Images;
import ces.riccico.models.HouseModel;
import ces.riccico.models.Message;
import ces.riccico.models.PaginationModel;
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

	@Autowired
	private ModelMapper mapper;

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
		Integer idCurrent = null;
		Accounts account;
		Message message = new Message();
		try {
			idCurrent = securityAuditorAware.getCurrentAuditor().get();
			account = accountRepository.findById(idCurrent).get();
			if (account == null) {
				message.setMessage(UserNotification.accountNotExist);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			} else {
				if (house.getTitle().equals(null)) {
					message.setMessage(HouseNotification.nameIsNull);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (house.getCountry() == null || house.getCountry().isEmpty()) {
					message.setMessage(HouseNotification.countryIsNull);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (house.getProvince().equals(null)) {
					message.setMessage(HouseNotification.cityIsNull);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (house.getAddress().equals(null)) {
					message.setMessage(HouseNotification.addressIsNull);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else {
					House houseNew = mapper.map(house, House.class);
					houseNew.setAccount(account);
					houseNew.setDeleted(NOT_DELETED);
					houseNew.setApproved(false);
					houseRepository.saveAndFlush(houseNew);
					return ResponseEntity.status(HttpStatus.CREATED).body(houseNew);
				}
			}
		} catch (Exception e) {
			message.setMessage(e.toString());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
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
			} else if (accountRepository.findById(idCurrent).get().getRole().equals(ROLE_ADMIN)) {
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
			houseUpdate = mapper.map(house, House.class);
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
				houseRepository.saveAndFlush(house);
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
		List<HouseModel> listHouseModel = new ArrayList<HouseModel>();
		List<House> listHouse = new ArrayList<House>();
		PaginationModel paginationModel = new PaginationModel();
		Message message = new Message();
		try {
			Pageable paging = PageRequest.of(page, size);
			listHouse = houseRepository.findList(paging).getContent();
			int pageMax = houseRepository.findList(paging).getTotalPages();
			for (House house : listHouse) {
				HouseModel houseModel = mapper.map(house, HouseModel.class);
				listHouseModel.add(houseModel);
			}
			paginationModel.setListHouse(listHouseModel);
			paginationModel.setPageMax(pageMax);
			return ResponseEntity.ok(paginationModel);
		} catch (Exception e) {
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public ResponseEntity<?> getHouseDetail(Integer idHouse) {
		Message message = new Message();
		House house = houseRepository.findById(idHouse).get();
		if (house == null) {
			message.setMessage(HouseNotification.houseNotExist);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}
//		Set<String> image_url = new HashSet<>();
//		for(Images image : house.getImages()) {
//			String url  = image.getImage();
//			image_url.add(url);
//		}
		return ResponseEntity.ok(house);
	}

}