
package ces.riccico.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import ces.riccico.common.enums.StatusBooking;
import ces.riccico.common.enums.StatusHouse;
import ces.riccico.entity.Account;
import ces.riccico.entity.Booking;
import ces.riccico.entity.House;
import ces.riccico.entity.Rating;
import ces.riccico.model.DateModel;
import ces.riccico.model.HouseDetailModel;
import ces.riccico.model.HouseModel;
import ces.riccico.model.MessageModel;
import ces.riccico.model.PaginationModel;
import ces.riccico.model.RatingHouseModel;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.BookingRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.repository.RatingRepository;
import ces.riccico.service.HouseService;
import ces.riccico.security.SecurityAuditorAware;

@Service
public class HouseServiceImpl implements HouseService {

	private static Logger logger = LoggerFactory.getLogger(HouseServiceImpl.class);

	private static final String BLOCKED = "blocked";

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private HouseRepository houseRepository;

	@Autowired
	private RatingRepository ratingRepository;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private SecurityAuditorAware securityAuditorAware;

	@Override
	public ResponseEntity<?> blockHouse(int houseId) {
		// TODO Auto-generated method stub
		Integer currentId = securityAuditorAware.getCurrentAuditor().get();
		MessageModel message = new MessageModel();
		House house = new House();

		try {
			house = houseRepository.findById(houseId).get();
		} catch (Exception e) {
			message.setMessage(HouseConstants.HOUSE_NOT_FOUND);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (!accountRepository.findById(currentId).get().getRole().equals(Role.ADMIN.getRole())) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			message.setStatus(HttpStatus.FORBIDDEN.value());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
		}

		if (house.isBlock() == true) {
			house.setBlock(false);
			houseRepository.saveAndFlush(house);
			message.setData(house);
			message.setMessage(HouseConstants.UNBLOCK_SUCCESS);
			message.setStatus(HttpStatus.OK.value());
			return ResponseEntity.ok(message);
		}

		house.setBlock(true);
		houseRepository.saveAndFlush(house);
		message.setData(house);
		message.setMessage(HouseConstants.BLOCK_SUCCESS);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);

	}

	@Override
	public ResponseEntity<?> deactiveHouse(int houseId) {
		MessageModel message = new MessageModel();
		Integer currentId = securityAuditorAware.getCurrentAuditor().get();
		House house = new House();
		try {
			house = houseRepository.findById(houseId).get();
		} catch (Exception e) {
			message.setMessage(HouseConstants.HOUSE_NOT_FOUND);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (!currentId.equals(house.getAccount().getAccountId())) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			message.setStatus(HttpStatus.FORBIDDEN.value());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
		}

		if (!houseRepository.findById(houseId).isPresent()) {
			message.setMessage(HouseConstants.HOUSE_NOT_EXIST);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (house.isBlock() == true) {
			message.setMessage(HouseConstants.HOUSE_BLOCK);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (StatusHouse.DEACTIVED.getStatusName().equals(house.getStatus())) {
			message.setMessage(HouseConstants.HOUSE_DELETED);
			message.setStatus(HttpStatus.OK.value());
			return ResponseEntity.ok(message);
		}

		house.setStatus(StatusHouse.DEACTIVED.getStatusName());
		houseRepository.saveAndFlush(house);
		message.setData(house);
		message.setMessage(CommonConstants.SUCCESS);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);
	}

	@Override
	public ResponseEntity<?> unlistedHouse(int houseId) {
		MessageModel message = new MessageModel();
		Integer currentId = securityAuditorAware.getCurrentAuditor().get();
		House house = new House();

		try {
			house = houseRepository.findById(houseId).get();
		} catch (Exception e) {
			message.setMessage(HouseConstants.HOUSE_NOT_FOUND);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (!currentId.equals(houseRepository.findById(houseId).get().getAccount().getAccountId())) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			message.setStatus(HttpStatus.FORBIDDEN.value());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
		}

		if (!houseRepository.findById(houseId).isPresent()) {
			message.setMessage(HouseConstants.HOUSE_NOT_EXIST);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (house.isBlock() == true) {
			message.setMessage(HouseConstants.HOUSE_BLOCK);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (StatusHouse.DEACTIVED.getStatusName().equals(house.getStatus())) {
			message.setMessage(HouseConstants.HOUSE_DELETED);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (StatusHouse.LISTED.getStatusName().equals(house.getStatus())) {
			house.setStatus(StatusHouse.UNLISTED.getStatusName());
			houseRepository.saveAndFlush(house);
			message.setData(house);
			message.setMessage(CommonConstants.SUCCESS);
			message.setStatus(HttpStatus.OK.value());
			return ResponseEntity.ok(message);
		}

		house.setStatus(StatusHouse.LISTED.getStatusName());
		houseRepository.saveAndFlush(house);
		message.setData(house);
		message.setMessage(CommonConstants.SUCCESS);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);
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
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			message.setMessage(CommonConstants.INVALID_PAGE);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		paginationModel.setListObject(listHouseModel);
		paginationModel.setPageMax(pageMax);
		message.setData(paginationModel);
		message.setMessage(UserConstants.GET_INFORMATION);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);

	}

	@Override
	public ResponseEntity<?> findHouseByUsername(String username) {

		MessageModel message = new MessageModel();
		Integer idAccount = accountRepository.findByUsername(username).getAccountId();

		if (!accountRepository.findById(idAccount).isPresent()) {
			message.setMessage(UserConstants.ACCOUNT_NOT_EXISTS);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (accountRepository.findById(idAccount).get().getHouses().size() == 0) {
			message.setMessage(HouseConstants.HOUSE_NOT_FOUND);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		Set<House> listHouses = new HashSet<House>();
		listHouses = accountRepository.findById(idAccount).get().getHouses();
		Set<House> listHousesListed = new HashSet<House>();

		for (House house : listHouses) {
			if (StatusHouse.LISTED.equals(house.getStatus())) {
				listHousesListed.add(house);
			}
		}
		message.setData(listHousesListed);
		message.setMessage(UserConstants.GET_INFORMATION);
		message.setStatus(HttpStatus.NOT_FOUND.value());
		return ResponseEntity.ok(message);

	}

	@Override
	public ResponseEntity<?> getHouseDetail(Integer houseId) {
		MessageModel message = new MessageModel();
		HouseDetailModel houseDetail;

		if (!houseRepository.findById(houseId).isPresent()) {
			message.setMessage(HouseConstants.HOUSE_NOT_EXIST);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		House house = houseRepository.findById(houseId).get();

		List<Booking> listBookings = bookingRepository.findByHouseId(houseId);
		List<DateModel> listDateModel = new ArrayList<DateModel>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		Date currentDate = new Date();
		String dateNow = dateFormat.format(currentDate);

		try {
			currentDate = dateFormat.parse(dateNow);
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}

		for (Booking booking : listBookings) {
			if (StatusBooking.PAID.getStatusName().equals(booking.getStatus())) {
				if (TimeUnit.MILLISECONDS.toDays(booking.getDateCheckOut().getTime() - currentDate.getTime()) > 0) {
					DateModel dateModel = new DateModel();
					dateModel.setDateCheckIn(booking.getDateCheckIn().toString());
					dateModel.setDateCheckOut(booking.getDateCheckOut().toString());
					listDateModel.add(dateModel);
				}
			}
		}

		List<Rating> listRating = ratingRepository.findByBookingHouseId(houseId);
		List<RatingHouseModel> listRatingModel = new ArrayList<RatingHouseModel>();

		for (Rating rating : listRating) {
			RatingHouseModel ratingModel = new RatingHouseModel();
			ratingModel.setRating(rating);
			ratingModel.setUsername(rating.getBooking().getAccount().getUsername());
			ratingModel.setCreatedAt(rating.getCreatedAt());
			listRatingModel.add(ratingModel);
		}

		houseDetail = mapper.map(house, HouseDetailModel.class);
		int amenities = Integer.parseInt(house.getAmenities(), 2);
		boolean wifi = ((amenities & Amenities.WIFI.getValue()) != 0) ? true : false;
		boolean tivi = ((amenities & Amenities.TIVI.getValue()) != 0) ? true : false;
		boolean airConditioner = ((amenities & Amenities.AC.getValue()) != 0) ? true : false;
		boolean fridge = ((amenities & Amenities.FRIDGE.getValue()) != 0) ? true : false;
		boolean swimPool = ((amenities & Amenities.SWIM_POOL.getValue()) != 0) ? true : false;

		List<String> listImages = new ArrayList<String>();
		String images = house.getImages();
		for (String image : images.split(",")) {
			listImages.add(image);
		}

		houseDetail.setImages(listImages);
		houseDetail.setWifi(wifi);
		houseDetail.setTivi(tivi);
		houseDetail.setAirConditioner(airConditioner);
		houseDetail.setFridge(fridge);
		houseDetail.setSwimPool(swimPool);
		houseDetail.setDateBooked(listDateModel);
		houseDetail.setListRating(listRatingModel);

		message.setData(houseDetail);
		message.setMessage(UserConstants.GET_INFORMATION);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);
	}

	@Override
	public ResponseEntity<?> getHouseForHost(int accountId, String block, String status, int page, int size) {
		MessageModel message = new MessageModel();
		Integer currentId = securityAuditorAware.getCurrentAuditor().get();
		boolean blockCurrent = Boolean.parseBoolean(block);

		if (!accountRepository.findById(currentId).get().getRole().equals(Role.ADMIN.getRole())
				&& !currentId.equals(accountId)) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			message.setStatus(HttpStatus.FORBIDDEN.value());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
		}

		List<Object> listHouseModel = new ArrayList<Object>();
		List<House> listHouse = new ArrayList<House>();
		PaginationModel paginationModel = new PaginationModel();
		Pageable paging = PageRequest.of(page, size);
		int pageMax = 0;

		if (accountId == 0) {
			// Integer.toString(accountId) == null || Integer.toString(accountId).isEmpty()
			if (!accountRepository.findById(currentId).get().getRole().equals(Role.ADMIN.getRole())) {
				message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
				message.setStatus(HttpStatus.FORBIDDEN.value());
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			}
			if ((status == null || status.isEmpty()) && block == null) {
				listHouse = houseRepository.getAllHouseForAdmin(paging).getContent();
				pageMax = houseRepository.getAllHouseForAdmin(paging).getTotalPages();
			} else {

				if (blockCurrent == true && status == null) {
					listHouse = houseRepository.getHouseBlockForAdmin(blockCurrent, paging).getContent();
					pageMax = houseRepository.getHouseBlockForAdmin(blockCurrent, paging).getTotalPages();

					for (House house : listHouse) {
						HouseModel houseModel = mapper.map(house, HouseModel.class);
						if (houseModel.getModifiedDate() == null || houseModel.getModifiedDate().toString().isEmpty()) {
							houseModel.setModifiedDate(house.getCreatedAt());
						}
						listHouseModel.add(houseModel);
					}
					paginationModel.setListObject(listHouseModel);
					paginationModel.setPageMax(pageMax);
					message.setData(paginationModel);
					message.setMessage(UserConstants.GET_INFORMATION);
					message.setStatus(HttpStatus.OK.value());
					return ResponseEntity.ok(message);
				}
				List<String> listStatus = Stream.of(StatusHouse.values()).map(StatusHouse::name)
						.collect(Collectors.toList());
				try {
					if (!listStatus.contains(status.toUpperCase())) {
						message.setMessage(HouseConstants.INVALID_STATUS);
						message.setStatus(HttpStatus.BAD_REQUEST.value());
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
					}
				} catch (Exception e) {
					message.setMessage(HouseConstants.STATUS_NULL);
					message.setStatus(HttpStatus.BAD_REQUEST.value());
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				}

				blockCurrent = false;
				listHouse = houseRepository.getHouseForAdmin(blockCurrent, status, paging).getContent();
				pageMax = houseRepository.getHouseForAdmin(blockCurrent, status, paging).getTotalPages();

			}

		} else {
			if (!accountRepository.findById(accountId).isPresent()) {
				message.setMessage(UserConstants.ACCOUNT_NOT_EXISTS);
				message.setStatus(HttpStatus.NOT_FOUND.value());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			}

			if ((status == null || status.isEmpty()) && block == null) {
				listHouse = houseRepository.getAllHouseForHost(accountId, paging).getContent();
				pageMax = houseRepository.getAllHouseForHost(accountId, paging).getTotalPages();
			} else {

				if (blockCurrent == true && status == null) {
					listHouse = houseRepository.getHouseBlockForHost(accountId, blockCurrent, paging).getContent();
					pageMax = houseRepository.getHouseBlockForHost(accountId, blockCurrent, paging).getTotalPages();

					for (House house : listHouse) {
						HouseModel houseModel = mapper.map(house, HouseModel.class);
						if (houseModel.getModifiedDate() == null || houseModel.getModifiedDate().toString().isEmpty()) {
							houseModel.setModifiedDate(house.getCreatedAt());
						}
						listHouseModel.add(houseModel);
					}
					paginationModel.setListObject(listHouseModel);
					paginationModel.setPageMax(pageMax);
					message.setData(paginationModel);
					message.setMessage(UserConstants.GET_INFORMATION);
					message.setStatus(HttpStatus.OK.value());
					return ResponseEntity.ok(message);
				}
				List<String> listStatus = Stream.of(StatusHouse.values()).map(StatusHouse::name)
						.collect(Collectors.toList());

				try {
					if (!listStatus.contains(status.toUpperCase())) {
						message.setMessage(HouseConstants.INVALID_STATUS);
						message.setStatus(HttpStatus.BAD_REQUEST.value());
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
					}
				} catch (Exception e) {
					message.setMessage(HouseConstants.STATUS_NULL);
					message.setStatus(HttpStatus.BAD_REQUEST.value());
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				}

				blockCurrent = false;
				listHouse = houseRepository.getHouseForHost(accountId, status, blockCurrent, paging).getContent();
				pageMax = houseRepository.getHouseForHost(accountId, status, blockCurrent, paging).getTotalPages();

			}
		}

		if (listHouse.size() == 0) {
			message.setMessage(HouseConstants.HOUSE_NOT_FOUND);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		for (House house : listHouse) {
			HouseModel houseModel = mapper.map(house, HouseModel.class);
			if (houseModel.getModifiedDate() == null || houseModel.getModifiedDate().toString().isEmpty()) {
				houseModel.setModifiedDate(house.getCreatedAt());
			}
			listHouseModel.add(houseModel);
		}

		if (page >= pageMax) {
			message.setMessage(CommonConstants.INVALID_PAGE);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		paginationModel.setListObject(listHouseModel);
		paginationModel.setPageMax(pageMax);
		message.setData(paginationModel);
		message.setMessage(UserConstants.GET_INFORMATION);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);

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
			message.setStatus(HttpStatus.NOT_FOUND.value());
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

		List<String> listImages = houseDetail.getImages();
		String images = "";
		for (String i : listImages) {
			images += i + ",";
		}
		images = images.substring(0, images.length() - 1);

		house.setImages(images);

		house.setAccount(account);
		house.setBlock(false);
		house.setStatus(StatusHouse.LISTED.getStatusName());
		houseRepository.saveAndFlush(house);
		message.setData(house);
		message.setMessage(CommonConstants.SUCCESS);
		message.setStatus(HttpStatus.CREATED.value());
		return ResponseEntity.status(HttpStatus.CREATED).body(message);

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
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		for (House house : listHouseAmenities) {
			HouseModel houseModel = mapper.map(house, HouseModel.class);
			listHouseModel.add(houseModel);
		}

		int fromIndex = (page) * size;
		final int numPages = (int) Math.ceil((double) listHouseModel.size() / (double) size);

		paginationModel.setListObject(listHouseModel.subList(fromIndex, Math.min(fromIndex + size, listHouseModel.size())));
		paginationModel.setPageMax(numPages);
		message.setData(paginationModel);
		message.setMessage(UserConstants.GET_INFORMATION);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);

	}

	@Override
	public ResponseEntity<?> updateHouse(int houseId, HouseDetailModel houseDetail) {

		MessageModel message = new MessageModel();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		House house = new House();

		try {
			house = houseRepository.findById(houseId).get();
		} catch (Exception e) {
			message.setMessage(HouseConstants.HOUSE_NOT_FOUND);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (!house.getAccount().getAccountId().equals(idCurrent)) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			message.setStatus(HttpStatus.UNAUTHORIZED.value());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		if (!houseRepository.findById(houseId).isPresent()) {
			message.setMessage(HouseConstants.HOUSE_NOT_EXIST);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (house.isBlock() == true) {
			message.setMessage(HouseConstants.HOUSE_BLOCK);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (StatusHouse.DEACTIVED.getStatusName().equals(houseDetail.getStatus())) {
			message.setMessage(HouseConstants.INVALID_STATUS);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		house.setAddress(houseDetail.getAddress());
		house.setTitle(houseDetail.getTitle());
		house.setCountry(houseDetail.getCountry());
		house.setCity(houseDetail.getCity());
		house.setContent(houseDetail.getContent());
		house.setPhoneContact(houseDetail.getPhoneContact());
		house.setImage(houseDetail.getImage());
		house.setSize(houseDetail.getSize());
		house.setPrice(houseDetail.getPrice());
		house.setStatus(houseDetail.getStatus());
		byte wifi = ((houseDetail.isWifi() == true)) ? Amenities.WIFI.getValue() : 0;
		byte tivi = ((houseDetail.isTivi() == true)) ? Amenities.TIVI.getValue() : 0;
		byte ac = ((houseDetail.isAirConditioner() == true)) ? Amenities.AC.getValue() : 0;
		byte fridge = ((houseDetail.isFridge() == true)) ? Amenities.FRIDGE.getValue() : 0;
		byte swim_pool = ((houseDetail.isSwimPool() == true)) ? Amenities.SWIM_POOL.getValue() : 0;
		String amenities = Integer.toBinaryString(wifi | tivi | ac | fridge | swim_pool);
		house.setAmenities(amenities);

		List<String> listImages = houseDetail.getImages();
		String images = "";
		for (String i : listImages) {
			images += i + ",";
		}
		images = images.substring(0, images.length() - 1);

		house.setImages(images);
		house.setBedroom(houseDetail.getBedroom());
		house.setMaxGuest(houseDetail.getMaxGuest());
		houseRepository.saveAndFlush(house);
		message.setData(house);
		message.setMessage(CommonConstants.SUCCESS);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);

	}

}
