package ces.riccico.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.common.constants.BookingConstants;
import ces.riccico.common.constants.CommonConstants;
import ces.riccico.common.constants.HouseConstants;
import ces.riccico.common.constants.UserConstants;
import ces.riccico.common.enums.Status;
import ces.riccico.entity.Account;
import ces.riccico.entity.Booking;
import ces.riccico.entity.House;
import ces.riccico.entity.Rating;
import ces.riccico.model.BookingModel;
import ces.riccico.model.MessageModel;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.BookingRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.BookingService;

@Service
public class BookingServiceImpl implements BookingService {

	private static Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private HouseRepository houseRepository;

	@Autowired
	private SecurityAuditorAware securityAuditorAware;

	@Override
	public ResponseEntity<?> acceptBooking(int bookingId) {
		MessageModel message = new MessageModel();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();

		if (!bookingRepository.findById(bookingId).isPresent()) {
			message.setMessage(BookingConstants.BOOKING_NOT_EXITST);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (!idCurrent.equals(bookingRepository.findById(bookingId).get().getHouse().getAccount().getAccountId())) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		Booking booking = bookingRepository.findById(bookingId).get();
		booking.setStatus(Status.APPROVAL.getStatusName());
		bookingRepository.saveAndFlush(booking);
		message.setMessage(CommonConstants.SUCCESS);
		return ResponseEntity.ok(message);
	}

	@Override
	public ResponseEntity<?> cancelBooking(int bookingId) {
		MessageModel message = new MessageModel();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Booking booking = bookingRepository.findById(bookingId).get();

		if (!bookingRepository.findById(bookingId).isPresent()) {
			message.setMessage(BookingConstants.BOOKING_NOT_EXITST);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (!idCurrent.equals(booking.getHouse().getAccount().getAccountId())
				|| !idCurrent.equals(booking.getAccount().getAccountId())) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		booking.setStatus(Status.CANCELED.getStatusName());
		bookingRepository.saveAndFlush(booking);

		if (idCurrent.equals(booking.getAccount().getAccountId())) {
			message.setMessage(BookingConstants.BY_CUSTOMER);
			return ResponseEntity.ok(message);
		} else {

			message.setMessage(BookingConstants.BY_HOST);
			return ResponseEntity.ok(message);
		}
	}

	@Override
	public ResponseEntity<?> completeBooking̣̣̣(int bookingId) {
		MessageModel message = new MessageModel();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		Date currentDate = new Date();
		String dateNow = sdf.format(currentDate);

		try {
			currentDate = sdf.parse(dateNow);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			message.setMessage(e.getLocalizedMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		try {
			Booking booking = bookingRepository.findById(bookingId).get();
			if (!bookingRepository.findById(bookingId).isPresent()) {
				message.setMessage(BookingConstants.BOOKING_NOT_EXITST);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			}

			if (!Status.APPROVAL.getStatusName().equals(booking.getStatus())) {
				message.setMessage(BookingConstants.INVALID_STATUS);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			if (currentDate.compareTo(booking.getCreateEnd()) < 0) {
				message.setMessage(BookingConstants.INVALID_DATE_COMPLETE);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			booking.setStatus(Status.COMPLETED.getStatusName());
			bookingRepository.saveAndFlush(booking);
			message.setMessage(CommonConstants.SUCCESS);
			return ResponseEntity.ok(message);

		} catch (NullPointerException e) {
			logger.error(e.getMessage());
			message.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public ResponseEntity<?> findByAccountId(int accountId) {
		MessageModel message = new MessageModel();
		List<Booking> listBookings = new ArrayList<Booking>();
		List<BookingModel> listBookingModels = new ArrayList<BookingModel>();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();

		if (idCurrent != accountId) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		try {
			listBookings = bookingRepository.findByAccountId(accountId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			message.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (listBookings.size() == 0) {
			message.setMessage(BookingConstants.NULL_BOOKING);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		BookingModel bookingModel = new BookingModel();

		for (Booking booking : listBookings) {
			bookingModel.setBooking(booking);
			bookingModel.setHouseName(booking.getHouse().getTitle());
			bookingModel.setHouseId(booking.getHouse().getId());
			listBookingModels.add(bookingModel);
		}
		return ResponseEntity.ok(listBookingModels);
	}

	@Override
	public ResponseEntity<?> findByHouseId(int houseId) {
		MessageModel message = new MessageModel();
		List<Booking> listBookings = new ArrayList<Booking>();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		List<BookingModel> listBookingModels = new ArrayList<BookingModel>();

		if (!idCurrent.equals(houseRepository.findById(houseId).get().getAccount().getAccountId())) {

			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		try {
			listBookings = bookingRepository.findByHouseId(houseId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			message.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (listBookings.size() == 0) {
			message.setMessage(BookingConstants.NULL_BOOKING);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		BookingModel bookingModel = new BookingModel();

		for (Booking booking : listBookings) {
			bookingModel.setBooking(booking);
			bookingModel.setAccountId(booking.getAccount().getAccountId());
			bookingModel.setAccountName(booking.getAccount().getUsername());
			listBookingModels.add(bookingModel);
		}
		return ResponseEntity.ok(listBookingModels);
	}

	@Override
	public List<Booking> getAlḷ() {
		return null;
	}

	@Override
	public ResponseEntity<?> getBookingDetail(int bookingId) {
		MessageModel message = new MessageModel();
		Booking booking = new Booking();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();

		try {
			booking = bookingRepository.findById(bookingId).get();
		} catch (Exception e) {
			logger.error(e.getMessage());
			message.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (idCurrent != booking.getAccount().getAccountId()
				&& idCurrent != booking.getHouse().getAccount().getAccountId()) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		return ResponseEntity.ok(booking);
	}

	@Override

	public ResponseEntity<?> payment(int bookingId) {
		MessageModel message = new MessageModel();

		try {
			Booking booking = bookingRepository.findById(bookingId).get();
			Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();

			if (!bookingRepository.findById(bookingId).isPresent()) {
				message.setMessage(BookingConstants.BOOKING_NOT_EXITST);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			}

			if (!idCurrent.equals(booking.getAccount().getAccountId())) {
				message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
			}

			if (!Status.PENDING_PAYMENT.getStatusName().equals(booking.getStatus())) {
				message.setMessage(BookingConstants.INVALID_STATUS);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			booking.setStatus(Status.PENDING_APPROVAL.getStatusName());
			bookingRepository.saveAndFlush(booking);
			message.setMessage(CommonConstants.SUCCESS);
			return ResponseEntity.ok(message);

		} catch (Exception e) {
			logger.error(e.getMessage());
			message.setMessage(e.getLocalizedMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	@Override
	public ResponseEntity<?> receiveBooking(int houseId, String dateStart, String dateStop) {
		MessageModel message = new MessageModel();

		if (houseRepository.findById(houseId) == null) {
			message.setMessage(HouseConstants.HOUSE_NOT_EXIST);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Account account = accountRepository.findById(idCurrent).get();
		House house = houseRepository.findById(houseId).get();

		if (idCurrent.equals(house.getAccount().getAccountId())) {
			message.setMessage(BookingConstants.ACCOUNT_WITHOUT_PERMISSION);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		if (house.isApproved() == false || house.isDeleted() == true) {
			message.setMessage(HouseConstants.HOUSE_DELETED);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		List<Booking> listBookings = bookingRepository.findByHouseId(houseId);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		Date dateIn = null;
		Date dateOut = null;
		Date currentDate = new Date();
		String dateNow = dateFormat.format(currentDate);

		try {
			dateIn = dateFormat.parse(dateStart);
			dateOut = dateFormat.parse(dateStop);
			currentDate = dateFormat.parse(dateNow);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			message.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (TimeUnit.MILLISECONDS.toDays(dateIn.getTime() - currentDate.getTime()) < 0) {
			message.setMessage(BookingConstants.INVALID_CHECKIN);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (dateIn.compareTo(dateOut) > 0) {
			message.setMessage(BookingConstants.INVALID_CHECKOUT);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		for (Booking booking : listBookings) {
			if ((dateIn.compareTo(booking.getCreateCheckIn()) >= 0 && dateIn.compareTo(booking.getCreateEnd()) < 0
					|| dateOut.compareTo(booking.getCreateCheckIn()) > 0
							&& dateOut.compareTo(booking.getCreateEnd()) <= 0)
					&& !Status.CANCELED.getStatusName().equals(booking.getStatus())) {
				message.setMessage(BookingConstants.HOUSE_BOOKED);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
		}

		long days = TimeUnit.MILLISECONDS.toDays(dateOut.getTime() - dateIn.getTime());
		double price = house.getPrice();
		double bill = price * days;
		Booking booking = new Booking();
		booking.setAccount(account);
		booking.setHouse(house);
		booking.setStatus(Status.PENDING_PAYMENT.getStatusName());
		booking.setCreateCheckIn(dateIn);
		booking.setCreateEnd(dateOut);
		booking.setBill(bill);
		houseRepository.saveAndFlush(house);
		bookingRepository.saveAndFlush(booking);
		return ResponseEntity.ok(booking);

	}

	@Override
	public ResponseEntity<?> refund(int bookingId) {
		return null;
	}

}
