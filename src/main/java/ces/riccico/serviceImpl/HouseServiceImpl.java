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

import ces.riccico.common.HouseConstants;
import ces.riccico.common.CommonConstants;
import ces.riccico.common.UserConstants;
import ces.riccico.entities.Accounts;
import ces.riccico.entities.House;
import ces.riccico.models.Amenities;
import ces.riccico.models.HouseDetailModel;
import ces.riccico.models.HouseModel;
import ces.riccico.models.Message;
import ces.riccico.models.PaginationModel;
import ces.riccico.models.Role;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.service.HouseService;
import ces.riccico.security.SecurityAuditorAware;

@Service
public class HouseServiceImpl implements HouseService {

	private static final boolean IS_APPROVED = true;
	
	private static final boolean IS_DELETED = true;
	
	private static final boolean NOT_DELETED = false;
	


	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private HouseRepository houseRepository;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private SecurityAuditorAware securityAuditorAware;

	@Override
	public ResponseEntity<?> approveHouse(int idHouse) {
		Message message = new Message();

		try {
			House house = houseRepository.findById(idHouse).get();
			if (house.isApproved()) {
				message.setMessage(HouseConstants.HOUSE_APPROVED);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else {
				house.setApproved(IS_APPROVED);
				message.setMessage(CommonConstants.SUCCESS);
				houseRepository.saveAndFlush(house);
				return ResponseEntity.ok(message);
			}
		} catch (Exception e) {
			message.setMessage(e.getLocalizedMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public ResponseEntity<?> deleteHouse(int idHouse) {
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		House house = houseRepository.findById(idHouse).get();
		Message message = new Message();

		if (idCurrent != houseRepository.findById(idHouse).get().getAccount().getIdAccount()) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
		} else if (!houseRepository.findById(idHouse).isPresent()) {
			message.setMessage(HouseConstants.HOUSE_NOT_EXIST);
			ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		} else {
			if (house.isDeleted()) {
				message.setMessage(HouseConstants.HOUSE_DELETED);
				return ResponseEntity.ok(message);
			} else if (accountRepository.findById(idCurrent).get().getRole().equals(Role.ADMIN.getRole())) {
				house.setDeleted(IS_DELETED);
				houseRepository.saveAndFlush(house);
				message.setMessage(HouseConstants.BY_ADMIN);
				return ResponseEntity.ok(message);
			} else {
				if (!idCurrent.equals(houseRepository.findById(idHouse).get().getAccount().getIdAccount())) {
					message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
				}

				house.setDeleted(IS_DELETED);
				houseRepository.saveAndFlush(house);
				message.setMessage(HouseConstants.BY_USER);
				return ResponseEntity.ok(message);
			}
		}

		message.setMessage(CommonConstants.FAIL);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}

	@Override
	public ResponseEntity<?> findByPageAndSize(int page, int size) {
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
			message.setMessage(e.getLocalizedMessage());
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
			message.setMessage(e.getLocalizedMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

	}

	@Override
	public ResponseEntity<?> findHouseByUsername(String username) {
		Message message = new Message();

		try {
			Integer idAccount = accountRepository.findByUsername(username).getIdAccount();

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

		} catch (Exception e) {
			message.setMessage(e.getLocalizedMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

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
	public List<House> getAllUnApproved() {
		List<House> listNotApproved = new ArrayList<House>();

		for (House house : houseRepository.findAll()) {
			if (!house.isApproved() && !house.isDeleted()) {
				listNotApproved.add(house);
			}
		}

		return listNotApproved;
	}

	@Override
	public ResponseEntity<?> getHouseDetail(Integer idHouse) {
		Message message = new Message();
		House house = houseRepository.findById(idHouse).get();
		HouseDetailModel houseDetail;

		if (house == null) {
			message.setMessage(HouseConstants.HOUSE_NOT_EXIST);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		} else {
			houseDetail = mapper.map(house, HouseDetailModel.class);
			int amenities = Integer.parseInt(house.getAmenities(), 2);
			boolean wifi = ((amenities & Amenities.WIFI.getValue()) != 0) ? true : false;
			boolean tivi = ((amenities & Amenities.TIVI.getValue()) != 0) ? true : false;
			boolean air_conditioner = ((amenities & Amenities.AC.getValue()) != 0) ? true : false;
			boolean fridge = ((amenities & Amenities.FRIDGE.getValue()) != 0) ? true : false;
			boolean swimPool = ((amenities & Amenities.SWIM_POOL.getValue()) != 0) ? true : false;
			houseDetail.setWifi(wifi);
			houseDetail.setTivi(tivi);
			houseDetail.setAir_conditioner(air_conditioner);
			houseDetail.setFridge(fridge);
			houseDetail.setSwimPool(swimPool);
		}

		return ResponseEntity.ok(houseDetail);
	}

	@Override
	public ResponseEntity<?> postNewHouse(HouseDetailModel houseDetail) {
		Integer idCurrent = null;
		Accounts account;
		Message message = new Message();

		try {
			idCurrent = securityAuditorAware.getCurrentAuditor().get();
			account = accountRepository.findById(idCurrent).get();

			if (account == null) {
				message.setMessage(UserConstants.ACCOUNT_NOT_EXISTS);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			} else {
				if (houseDetail.getTitle().equals(null)) {
					message.setMessage(HouseConstants.NAME_IS_NULL);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (houseDetail.getCountry() == null || houseDetail.getCountry().isEmpty()) {
					message.setMessage(HouseConstants.COUNTRY_IS_NULL);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (houseDetail.getProvince().equals(null)) {
					message.setMessage(HouseConstants.CITY_IS_NULL);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else if (houseDetail.getAddress().equals(null)) {
					message.setMessage(HouseConstants.ADDRESS_IS_NULL);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else {
					House house = mapper.map(houseDetail, House.class);
					byte wifi = ((houseDetail.isWifi() == true)) ? Amenities.WIFI.getValue() : 0;
					byte tivi = ((houseDetail.isTivi() == true)) ? Amenities.TIVI.getValue() : 0;
					byte ac = ((houseDetail.isAir_conditioner() == true)) ? Amenities.AC.getValue() : 0;
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
			}
		} catch (Exception e) {
			message.setMessage(e.getLocalizedMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public ResponseEntity<?> searchFilter(String country, String province, Double lowestSize, Double highestSize,
			Double lowestPrice, Double highestPrice, boolean tivi, boolean wifi, boolean air_conditioner,
			boolean fridge, boolean swim_pool, byte lowestGuest, byte highestGuest, int page, int size) {

		Message message = new Message();
		List<HouseModel> listHouseModel = new ArrayList<HouseModel>();
		PaginationModel paginationModel = new PaginationModel();
		List<House> listHouse = new ArrayList<House>();
		String amenities = null;

		try {
			byte wifi_binary = (wifi == true) ? Amenities.WIFI.getValue() : 0;
			byte tivi_binary = (tivi == true) ? Amenities.TIVI.getValue() : 0;
			byte ac_binary = (air_conditioner == true) ? Amenities.AC.getValue() : 0;
			byte fridge_binary = (fridge == true) ? Amenities.FRIDGE.getValue() : 0;
			byte swim_pool_binary = (swim_pool == true) ? Amenities.SWIM_POOL.getValue() : 0;
			amenities = Integer
					.toBinaryString(wifi_binary | tivi_binary | ac_binary | fridge_binary | swim_pool_binary);
			Pageable paging = PageRequest.of(page, size);
			listHouse = houseRepository.searchFilter(country, province, lowestSize, highestSize, lowestPrice,
					highestPrice, amenities, lowestGuest, highestGuest, paging)
					.getContent();

			if (listHouse.size() == 0) {
				message.setMessage(HouseConstants.HOUSE_NOT_FOUND);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			} else {
				int pageMax = houseRepository.searchFilter(country, province, lowestSize, highestSize, lowestPrice,
						highestPrice, amenities, lowestGuest, highestGuest, paging)
						.getTotalPages();

				for (House house : listHouse) {
					HouseModel houseModel = mapper.map(house, HouseModel.class);
					listHouseModel.add(houseModel);

				}

				paginationModel.setListHouse(listHouseModel);
				paginationModel.setPageMax(pageMax);
				return ResponseEntity.ok(paginationModel);

			}
		} catch (Exception e) {
			message.setMessage(e.toString());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public ResponseEntity<?> updateHouse(int idHouse, HouseDetailModel houseDetail) {
		Message message = new Message();
		try {
			Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
			Accounts account = accountRepository.findById(idCurrent).get();

			if (idCurrent != houseRepository.findById(idHouse).get().getAccount().getIdAccount()) {
				message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			} else if (!houseRepository.findById(idHouse).isPresent()) {
				message.setMessage(HouseConstants.HOUSE_NOT_EXIST);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			} else if (!houseRepository.findById(idHouse).get().getAccount().getIdAccount().equals(idCurrent)) {
				message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
			}

			House house = houseRepository.findById(idHouse).get();
			house.setAddress(houseDetail.getAddress());
			house.setTitle(houseDetail.getTitle());
			house.setCountry(houseDetail.getCountry());
			house.setProvince(houseDetail.getProvince());
			house.setContent(houseDetail.getContent());
			house.setPhoneContact(houseDetail.getPhoneContact());
			house.setImage(houseDetail.getImage());
			house.setSize(houseDetail.getSize());
			house.setPrice(houseDetail.getPrice());
			byte wifi = ((houseDetail.isWifi() == true)) ? Amenities.WIFI.getValue() : 0;
			byte tivi = ((houseDetail.isTivi() == true)) ? Amenities.TIVI.getValue() : 0;
			byte ac = ((houseDetail.isAir_conditioner() == true)) ? Amenities.AC.getValue() : 0;
			byte fridge = ((houseDetail.isFridge() == true)) ? Amenities.FRIDGE.getValue() : 0;
			byte swim_pool = ((houseDetail.isSwimPool() == true)) ? Amenities.SWIM_POOL.getValue() : 0;
			String amenities = Integer.toBinaryString(wifi | tivi | ac | fridge | swim_pool);
			house.setAmenities(amenities);
			house.setBedroom(houseDetail.getBedroom());
			house.setMaxGuest(houseDetail.getMaxGuest());
			houseRepository.saveAndFlush(house);
			return ResponseEntity.ok(house);

		} catch (Exception e) {
			message.setMessage(e.toString());
			System.out.print(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}
<<<<<<< HEAD
=======

>>>>>>> 4094398... fix search filter
}