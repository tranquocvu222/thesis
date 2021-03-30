package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.common.constants.CommonConstants;
import ces.riccico.common.constants.HouseConstants;
import ces.riccico.common.constants.UserConstants;
import ces.riccico.common.enums.Amenities;
import ces.riccico.common.enums.Role;
import ces.riccico.entity.Account;
import ces.riccico.entity.House;
import ces.riccico.model.HouseDetailModel;
import ces.riccico.model.HouseModel;
import ces.riccico.model.MessageModel;
import ces.riccico.model.PaginationModel;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.service.HouseService;
import ces.riccico.security.SecurityAuditorAware;

@Service
public class HouseServiceImpl implements HouseService {

	private static final boolean IS_APPROVED = true;
	
	private static final boolean IS_DELETED = true;
	
	private static final boolean NOT_DELETED = false;
	
	private static Logger logger = LoggerFactory.getLogger(HouseServiceImpl.class);


	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private HouseRepository houseRepository;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private SecurityAuditorAware securityAuditorAware;

	@Override

	public ResponseEntity<?> approveHouse(int houseId) {
		
		MessageModel message = new MessageModel();
		House house = houseRepository.findById(houseId).get();

		if (house.isApproved()) {
			message.setMessage(HouseConstants.HOUSE_APPROVED);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		house.setApproved(IS_APPROVED);
		message.setMessage(CommonConstants.SUCCESS);
		houseRepository.saveAndFlush(house);
		return ResponseEntity.ok(message);

	}

	@Override
	public ResponseEntity<?> deleteHouse(int houseId) {
		MessageModel message = new MessageModel();
		Integer idCurrent;
		House house = houseRepository.findById(houseId).get();

		idCurrent = securityAuditorAware.getCurrentAuditor().get();

		if (!accountRepository.findById(idCurrent).get().getRole().equals(Role.ADMIN.getRole())
				&& !idCurrent.equals(houseRepository.findById(houseId).get().getAccount().getAccountId())) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		if (!houseRepository.findById(houseId).isPresent()) {
			message.setMessage(HouseConstants.HOUSE_NOT_EXIST);
			ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (house.isDeleted()) {
			message.setMessage(HouseConstants.HOUSE_DELETED);
			return ResponseEntity.ok(message);
		}

		house.setDeleted(IS_DELETED);
		houseRepository.saveAndFlush(house);

		if (accountRepository.findById(idCurrent).get().getRole().equals(Role.ADMIN.getRole())) {
			message.setMessage(HouseConstants.BY_ADMIN);
			return ResponseEntity.ok(message);
		} else {

			message.setMessage(HouseConstants.BY_USER);
			return ResponseEntity.ok(message);
		}

	}

	@Override
	public ResponseEntity<?> findByPageAndSize(int page, int size) {
		List<Object> listHouseModel = new ArrayList<Object>();
		List<House> listHouse = new ArrayList<House>();
		PaginationModel paginationModel = new PaginationModel();
		MessageModel message = new MessageModel();

		Pageable paging = PageRequest.of(page, size);
		listHouse = houseRepository.findList(paging).getContent();
		int pageMax = houseRepository.findList(paging).getTotalPages();

		for (House house : listHouse) {
			HouseModel houseModel = mapper.map(house, HouseModel.class);
			listHouseModel.add(houseModel);
		}

		if (page >= pageMax) {
			message.setMessage(CommonConstants.INVALID_PAGE);
		}

		paginationModel.setListObject(listHouseModel);
		paginationModel.setPageMax(pageMax);
		return ResponseEntity.ok(paginationModel);

	}


	@Override
	public ResponseEntity<?> findByTitle(String title, int page, int size) {
		Pageable paging = PageRequest.of(page, size);

		if (title == null || title.isEmpty()) {
			return ResponseEntity.ok(houseRepository.findAll(paging).getContent());
		} else {
			return ResponseEntity.ok(houseRepository.findByTitle(title, paging).getContent());
		}

	}

	@Override
	public ResponseEntity<?> findHouseByUsername(String username) {
		
		MessageModel message = new MessageModel();
		Integer idAccount = accountRepository.findByUsername(username).getAccountId();


		if (!accountRepository.findById(idAccount).isPresent()) {
			message.setMessage(UserConstants.ACCOUNT_NOT_EXISTS);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (accountRepository.findById(idAccount).get().getHouses().size() == 0) {
			message.setMessage(HouseConstants.HOUSE_NOT_EXIST);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		Set<House> listHouses = new HashSet<House>();
		listHouses = accountRepository.findById(idAccount).get().getHouses();
		Set<House> listHousesApprove = new HashSet<House>();

		for (House house : listHouses) {
			if (house.isDeleted() == false && house.isApproved() == true) {
				listHousesApprove.add(house);
			}
		}

		return ResponseEntity.ok(listHousesApprove);

	}

	@Override
	public ResponseEntity<?> getAll() {
		List<House> listHouse = new ArrayList<House>();
		MessageModel message = new MessageModel();

		for (House house : houseRepository.findAll()) {
			if (!house.isDeleted()) {
				listHouse.add(house);
			}
		}

		if (listHouse.size() == 0) {
			message.setMessage(CommonConstants.LIST_EMPTY);
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		return ResponseEntity.ok(listHouse);
	}

	@Override
	public ResponseEntity<?> getAllApproved() {
		List<House> listApproved = new ArrayList<House>();
		MessageModel message = new MessageModel();

		for (House house : houseRepository.findAll()) {
			if (house.isApproved() && !house.isDeleted()) {
				listApproved.add(house);
			}
		}

		if (listApproved.size() == 0) {
			message.setMessage(CommonConstants.LIST_EMPTY);
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		return ResponseEntity.ok(listApproved);
	}

	@Override
	public ResponseEntity<?> getAllDeleted() {
		List<House> listDeleted = new ArrayList<House>();
		MessageModel message = new MessageModel();

		for (House house : houseRepository.findAll()) {
			if (house.isDeleted()) {
				listDeleted.add(house);
			}
		}

		if (listDeleted.size() == 0) {
			message.setMessage(CommonConstants.LIST_EMPTY);
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		return ResponseEntity.ok(listDeleted);
	}

	@Override
	public ResponseEntity<?> getAllUnApproved() {
		List<House> listNotApproved = new ArrayList<House>();
		MessageModel message = new MessageModel();

		for (House house : houseRepository.findAll()) {
			if (!house.isApproved() && !house.isDeleted()) {
				listNotApproved.add(house);
			}
		}
		if (listNotApproved.size() == 0) {
			message.setMessage(CommonConstants.LIST_EMPTY);
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		return ResponseEntity.ok(listNotApproved);
	}

	@Override

	public ResponseEntity<?> getHouseDetail(Integer houseId) {
		House house = houseRepository.findById(houseId).get();
		MessageModel message = new MessageModel();
		HouseDetailModel houseDetail;

		if (house == null) {
			message.setMessage(HouseConstants.HOUSE_NOT_EXIST);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		houseDetail = mapper.map(house, HouseDetailModel.class);
		int amenities = Integer.parseInt(house.getAmenities(), 2);
		boolean wifi = ((amenities & Amenities.WIFI.getValue()) != 0) ? true : false;
		boolean tivi = ((amenities & Amenities.TIVI.getValue()) != 0) ? true : false;
		boolean airConditioner = ((amenities & Amenities.AC.getValue()) != 0) ? true : false;
		boolean fridge = ((amenities & Amenities.FRIDGE.getValue()) != 0) ? true : false;
		boolean swimPool = ((amenities & Amenities.SWIM_POOL.getValue()) != 0) ? true : false;
		houseDetail.setWifi(wifi);
		houseDetail.setTivi(tivi);
		houseDetail.setAirConditioner(airConditioner);
		houseDetail.setFridge(fridge);
		houseDetail.setSwimPool(swimPool);

		return ResponseEntity.ok(houseDetail);
	}

	@Override
	public ResponseEntity<?> postNewHouse(HouseDetailModel houseDetail) {
		
		Integer idCurrent = null;
		Account account;
		MessageModel message = new MessageModel();
		idCurrent = securityAuditorAware.getCurrentAuditor().get();
		account = accountRepository.findById(idCurrent).get();

		if (account == null) {
			message.setMessage(UserConstants.ACCOUNT_NOT_EXISTS);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		House house = mapper.map(houseDetail, House.class);
		byte wifi = ((houseDetail.isWifi() == true)) ? Amenities.WIFI.getValue() : 0;
		byte tivi = ((houseDetail.isTivi() == true)) ? Amenities.TIVI.getValue() : 0;
		byte ac = ((houseDetail.isAirConditioner() == true)) ? Amenities.AC.getValue() : 0;
		byte fridge = ((houseDetail.isFridge() == true)) ? Amenities.FRIDGE.getValue() : 0;
		byte swim_pool = ((houseDetail.isSwimPool() == true)) ? Amenities.SWIM_POOL.getValue() : 0;
		String amenities = Integer.toBinaryString(wifi | tivi | ac | fridge | swim_pool);
		house.setAmenities(amenities);
		house.setAccount(account);
		house.setDeleted(NOT_DELETED);
		house.setApproved(IS_APPROVED);
		houseRepository.saveAndFlush(house);
		return ResponseEntity.status(HttpStatus.CREATED).body(house);

	}

	@Override
	public ResponseEntity<?> searchFilter(String country, String city, Double lowestSize, Double highestSize,
			Double lowestPrice, Double highestPrice, boolean tivi, boolean wifi, boolean airConditioner, boolean fridge,
			boolean swimPool, byte lowestGuest, byte highestGuest, int page, int size) {


		MessageModel message = new MessageModel();

		List<Object> listHouseModel = new ArrayList<Object>();
		PaginationModel paginationModel = new PaginationModel();
		List<House> listHouse = new ArrayList<House>();
		byte amenities;
		List<House> listHouseAmenities = new ArrayList<House>();

		byte wifi_binary = (wifi == true) ? Amenities.WIFI.getValue() : 0;
		byte tivi_binary = (tivi == true) ? Amenities.TIVI.getValue() : 0;
		byte ac_binary = (airConditioner == true) ? Amenities.AC.getValue() : 0;
		byte fridge_binary = (fridge == true) ? Amenities.FRIDGE.getValue() : 0;
		byte swim_pool_binary = (swimPool == true) ? Amenities.SWIM_POOL.getValue() : 0;
		amenities = (byte) (wifi_binary | tivi_binary | ac_binary | fridge_binary | swim_pool_binary);

		listHouse = houseRepository.searchFilter(country, city, lowestSize, highestSize, lowestPrice, highestPrice,
				lowestGuest, highestGuest);

		for (House house : listHouse) {
			if ((byte) (amenities & Byte.parseByte(house.getAmenities(), 2)) == amenities) {
				listHouseAmenities.add(house);
			}
		}

		if (listHouseAmenities.size() == 0) {
			message.setMessage(HouseConstants.HOUSE_NOT_FOUND);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		for (House house : listHouseAmenities) {
			HouseModel houseModel = mapper.map(house, HouseModel.class);
			listHouseModel.add(houseModel);
		}

		int fromIndex = (page) * size;
		final int numPages = (int) Math.ceil((double) listHouseModel.size() / (double) size);

		paginationModel
				.setListObject(listHouseModel.subList(fromIndex, Math.min(fromIndex + size, listHouseModel.size())));
		paginationModel.setPageMax(numPages);
		return ResponseEntity.ok(paginationModel);

	}

	@Override
	public ResponseEntity<?> updateHouse(int houseId, HouseDetailModel houseDetail) {
		
		MessageModel message = new MessageModel();

		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();

		if (!houseRepository.findById(houseId).get().getAccount().getAccountId().equals(idCurrent)) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		if (!houseRepository.findById(houseId).isPresent()) {
			message.setMessage(HouseConstants.HOUSE_NOT_EXIST);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		House house = houseRepository.findById(houseId).get();
		house.setAddress(houseDetail.getAddress());
		house.setTitle(houseDetail.getTitle());
		house.setCountry(houseDetail.getCountry());
		house.setCity(houseDetail.getCity());
		house.setContent(houseDetail.getContent());
		house.setPhoneContact(houseDetail.getPhoneContact());
		house.setImage(houseDetail.getImage());
		house.setSize(houseDetail.getSize());
		house.setPrice(houseDetail.getPrice());
		byte wifi = ((houseDetail.isWifi() == true)) ? Amenities.WIFI.getValue() : 0;
		byte tivi = ((houseDetail.isTivi() == true)) ? Amenities.TIVI.getValue() : 0;
		byte ac = ((houseDetail.isAirConditioner() == true)) ? Amenities.AC.getValue() : 0;
		byte fridge = ((houseDetail.isFridge() == true)) ? Amenities.FRIDGE.getValue() : 0;
		byte swim_pool = ((houseDetail.isSwimPool() == true)) ? Amenities.SWIM_POOL.getValue() : 0;
		String amenities = Integer.toBinaryString(wifi | tivi | ac | fridge | swim_pool);
		house.setAmenities(amenities);
		house.setBedroom(houseDetail.getBedroom());
		house.setMaxGuest(houseDetail.getMaxGuest());
		houseRepository.saveAndFlush(house);
		return ResponseEntity.ok(house);

	}

}
