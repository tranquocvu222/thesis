
package ces.riccico.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ces.riccico.common.constants.BookingConstants;
import ces.riccico.common.constants.CommonConstants;
import ces.riccico.common.constants.HouseConstants;
import ces.riccico.common.constants.UserConstants;
import ces.riccico.common.enums.Role;
import ces.riccico.common.enums.StatusBooking;
import ces.riccico.common.enums.StatusHouse;
import ces.riccico.entity.Account;
import ces.riccico.entity.Booking;
import ces.riccico.entity.House;
import ces.riccico.entity.Rating;
import ces.riccico.model.BookingCustomerModel;
import ces.riccico.model.BookingDTO;
import ces.riccico.model.BookingDetailModel;
import ces.riccico.model.DateModel;
import ces.riccico.model.MessageModel;
import ces.riccico.model.PaginationModel;
import ces.riccico.model.RatingCustomerModel;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.BookingRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.repository.RatingRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.BookingService;

@Service
public class BookingServiceImpl implements BookingService {

	private static Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);
	public static final long HOUR = 3600 * 1000; // 1 hours

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private HouseRepository houseRepository;

	@Autowired
	private RatingRepository ratingRepository;

	@Autowired
	private SecurityAuditorAware securityAuditorAware;

	@Autowired
	private JavaMailSender sender;

	@Override
	public ResponseEntity<?> cancelBooking(int bookingId, boolean click) {
		MessageModel message = new MessageModel();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Booking booking = bookingRepository.findById(bookingId).get();
		Date dateCheckIn = booking.getDateCheckIn();
		Date currentDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		String dateNow = dateFormat.format(currentDate);

		try {
			dateCheckIn = dateFormat.parse(dateCheckIn.toString());
			dateCheckIn = new Date(dateCheckIn.getTime() + 14 * HOUR);
			currentDate = dateFormat.parse(dateNow);
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		long hours = TimeUnit.MILLISECONDS.toHours(dateCheckIn.getTime() - currentDate.getTime());

		if (!bookingRepository.findById(bookingId).isPresent()) {
			message.setMessage(BookingConstants.BOOKING_NOT_EXITST);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (!idCurrent.equals(booking.getAccount().getAccountId())) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			message.setStatus(HttpStatus.UNAUTHORIZED.value());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		if (!StatusBooking.PAID.getStatusName().equals(booking.getStatus())) {
			message.setMessage(BookingConstants.INVALID_STATUS);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (hours < 24) {
			message.setMessage(BookingConstants.CAN_NOT_CANCEL);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (click == false && idCurrent.equals(booking.getAccount().getAccountId())) {
			float bill = (float) (booking.getBill() * 15) / 100;
			message.setMessage(BookingConstants.CANCEL_BOOKING);
			message.setMessage("Cancel fee " + bill);
			return ResponseEntity.ok(message);
		}

		booking.setStatus(StatusBooking.CANCELED.getStatusName());
		bookingRepository.saveAndFlush(booking);

		SimpleMailMessage messageEmailCustomer = new SimpleMailMessage();
		messageEmailCustomer.setTo(booking.getAccount().getEmail());
		messageEmailCustomer.setSubject("BOOKING CENCELED");
		messageEmailCustomer.setText("Wellcome " + booking.getAccount().getUser().getLastName() + " "
				+ booking.getAccount().getUser().getFirstName()
				+ "\nYou booking canceled successfull \nRented on the date : " + booking.getDateCheckIn() + " to "
				+ booking.getDateCheckOut() + "\nAt Homestay " + booking.getHouse().getTitle()
				+ "\nThe total cost you get after deducting cancellation fee is : " + (booking.getBill() * 85) / 100
				+ "\nAny questions please contact " + booking.getHouse().getAccount().getUsername()
				+ "\nThrough phone number " + booking.getHouse().getPhoneContact());
		sender.send(messageEmailCustomer);
		message.setData(booking);
		message.setMessage(BookingConstants.BY_CUSTOMER);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);

	}

	@Override
	public ResponseEntity<?> completeBooking̣̣̣() {
		List<Booking> listBooking = bookingRepository.getBookingByStatus(StatusBooking.PAID.getStatusName());
		Date currentDate = new Date();
		MessageModel message = new MessageModel();

		for (Booking booking : listBooking) {
			Date dateCheckOut = booking.getDateCheckOut();
			dateCheckOut = new Date(dateCheckOut.getTime() + 12 * HOUR);
			if (dateCheckOut.compareTo(currentDate) < 0) {
				booking.setStatus(StatusBooking.COMPLETED.getStatusName());

				Double cost = booking.getBill() - ((booking.getBill() * 15) / 100);

				SimpleMailMessage messageEmailHost = new SimpleMailMessage();
				messageEmailHost.setTo(booking.getHouse().getAccount().getEmail());
				messageEmailHost.setSubject("PAYMENT THE BILL ");
				messageEmailHost.setText("Wellcome " + booking.getHouse().getAccount().getUsername()
						+ "\nYou have received the cost for the bill " + booking.getId() + " from "
						+ booking.getAccount().getUsername() + "\nHave booked your home "
						+ booking.getHouse().getTitle() + "\nRented on the date : " + booking.getDateCheckIn() + " to "
						+ booking.getDateCheckOut() + "\nWith total cost (Commissions have been deducted) " + cost);
				sender.send(messageEmailHost);
			}

			bookingRepository.saveAndFlush(booking);
		}
		message.setData(listBooking);
		message.setMessage(UserConstants.GET_INFORMATION);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);

	}

	@Override
	public ResponseEntity<?> incompleteBooking() {
		List<Booking> listBookingPending = bookingRepository.getBookingByStatus(StatusBooking.PENDING.getStatusName());
		Date currentDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		String dateNow = dateFormat.format(currentDate);
		MessageModel message = new MessageModel();

		for (Booking booking : listBookingPending) {
			Date dateCheckIn = booking.getDateCheckIn();
			try {
				dateCheckIn = dateFormat.parse(dateCheckIn.toString());
				dateCheckIn = new Date(dateCheckIn.getTime() + 14 * HOUR);
				currentDate = dateFormat.parse(dateNow);
			} catch (ParseException e) {
				logger.error(e.getMessage());
			}
			long hours = TimeUnit.MILLISECONDS.toHours(dateCheckIn.getTime() - currentDate.getTime());
			if (hours < 24) {
				booking.setStatus(StatusBooking.INCOMPLETED.getStatusName());
				bookingRepository.saveAndFlush(booking);
			}

		}
		message.setData(listBookingPending);
		message.setMessage(UserConstants.GET_INFORMATION);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);
	}

	@Override
	public ResponseEntity<?> findByHouseId(int houseId) {
		MessageModel message = new MessageModel();
		List<Booking> listBookings = bookingRepository.findByHouseId(houseId);
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();

		if (!idCurrent.equals(houseRepository.findById(houseId).get().getAccount().getAccountId())) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			message.setStatus(HttpStatus.UNAUTHORIZED.value());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		if (listBookings.size() == 0) {
			message.setMessage(BookingConstants.NULL_BOOKING);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		List<BookingDetailModel> listBookingModel = new ArrayList<BookingDetailModel>();

		for (Booking booking : listBookings) {
			BookingDetailModel bookingModel = mapper.map(booking, BookingDetailModel.class);
			bookingModel.setCustomerId(booking.getAccount().getAccountId());
			bookingModel.setCustomerName(booking.getAccount().getUsername());
			bookingModel.setHouseName(booking.getHouse().getTitle());
			bookingModel.setHouseId(booking.getHouse().getId());
			Rating rating = ratingRepository.findByBookingId(booking.getId());
			if (rating != null) {
				bookingModel.setRating(rating);
			}
			listBookingModel.add(bookingModel);
		}
		message.setData(listBookingModel);
		message.setMessage(UserConstants.GET_INFORMATION);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);
	}

	@Override
	public ResponseEntity<?> getBookingDetail(int bookingId) {
		MessageModel message = new MessageModel();
		Booking booking = new Booking();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		booking = bookingRepository.findById(bookingId).get();

		if (idCurrent != booking.getAccount().getAccountId()
				&& idCurrent != booking.getHouse().getAccount().getAccountId()) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			message.setStatus(HttpStatus.UNAUTHORIZED.value());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}
		message.setData(booking);
		message.setMessage(UserConstants.GET_INFORMATION);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);
	}

	@Override
	public ResponseEntity<?> getBookingForCustomer(int accountId, String status, int page, int size) {
		MessageModel message = new MessageModel();
		Integer currentId = securityAuditorAware.getCurrentAuditor().get();
		Optional<Account> account = accountRepository.findById(accountId);

		if (!account.isPresent()) {
			message.setMessage(UserConstants.ACCOUNT_NOT_EXISTS);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (!account.get().getRole().equals(Role.ADMIN.getRole()) && !currentId.equals(accountId)) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			message.setStatus(HttpStatus.FORBIDDEN.value());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
		}

		if (account.get().getBookings().size() == 0) {
			message.setMessage(BookingConstants.NULL_BOOKING);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		List<Object> listBookingModel = new ArrayList<Object>();
		List<BookingCustomerModel> listBooking = new ArrayList<BookingCustomerModel>();
		PaginationModel paginationModel = new PaginationModel();
		Pageable paging = PageRequest.of(page, size);
		int pageMax = 0;
		if (status.equals(null) || status.isEmpty()) {
			listBooking = bookingRepository.getAllBookingForCustomer(accountId, paging).getContent();
			pageMax = bookingRepository.getAllBookingForCustomer(accountId, paging).getTotalPages();
		} else {
			List<String> listStatus = Stream.of(StatusBooking.values()).map(StatusBooking::name)
					.collect(Collectors.toList());
			if (!listStatus.contains(status.toUpperCase())) {
				message.setMessage(BookingConstants.INVALID_STATUS);
				message.setStatus(HttpStatus.BAD_REQUEST.value());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
			listBooking = bookingRepository.getBookingForCustomer(accountId, status, paging).getContent();
			pageMax = bookingRepository.getBookingForCustomer(accountId, status, paging).getTotalPages();
		}
		if (listBooking.size() == 0) {
			message.setMessage(BookingConstants.NULL_BOOKING);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}
		for (BookingCustomerModel booking : listBooking) {
			long days = TimeUnit.MILLISECONDS
					.toDays(booking.getDateCheckOut().getTime() - booking.getDateCheckIn().getTime());
			booking.setNight(days);
			RatingCustomerModel rating = ratingRepository.findByBooking(booking.getId());
			if (rating != null) {
				booking.setRating(rating);
				;
			}
			listBookingModel.add(booking);
		}

		if (page >= pageMax) {
			message.setMessage(CommonConstants.INVALID_PAGE);
		}

		paginationModel.setListObject(listBookingModel);
		paginationModel.setPageMax(pageMax);

		message.setData(paginationModel);
		message.setMessage(UserConstants.GET_INFORMATION);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);
	}

	@Override
	public ResponseEntity<?> getBookingForHost(int accountId, String status, int page, int size) {
		MessageModel message = new MessageModel();
		Integer currentId = securityAuditorAware.getCurrentAuditor().get();

		if (!accountRepository.findById(accountId).isPresent()) {
			message.setMessage(UserConstants.ACCOUNT_NOT_EXISTS);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (!accountRepository.findById(currentId).get().getRole().equals(Role.ADMIN.getRole())
				&& !currentId.equals(accountId)) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			message.setStatus(HttpStatus.FORBIDDEN.value());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
		}

		if (accountRepository.findById(accountId).get().getBookings().size() == 0) {
			message.setMessage(BookingConstants.NULL_BOOKING);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		List<Object> listBookingModel = new ArrayList<Object>();
		List<Booking> listBooking = new ArrayList<Booking>();
		PaginationModel paginationModel = new PaginationModel();
		Pageable paging = PageRequest.of(page, size);
		int pageMax = 0;
		if (status.equals(null) || status.isEmpty()) {
			listBooking = bookingRepository.getAllBookingForHost(accountId, paging).getContent();
			pageMax = bookingRepository.getAllBookingForHost(accountId, paging).getTotalPages();
		} else {
			List<String> listStatus = Stream.of(StatusBooking.values()).map(StatusBooking::name)
					.collect(Collectors.toList());
			if (!listStatus.contains(status.toUpperCase())) {
				message.setMessage(BookingConstants.INVALID_STATUS);
				message.setStatus(HttpStatus.BAD_REQUEST.value());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
			listBooking = bookingRepository.getBookingForHost(accountId, status, paging).getContent();
			pageMax = bookingRepository.getBookingForHost(accountId, status, paging).getTotalPages();
		}
		if (listBooking.size() == 0) {
			message.setMessage(BookingConstants.NULL_BOOKING);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}
		for (Booking booking : listBooking) {
			BookingDetailModel bookingModel = mapper.map(booking, BookingDetailModel.class);
			long days = TimeUnit.MILLISECONDS
					.toDays(booking.getDateCheckOut().getTime() - booking.getDateCheckIn().getTime());
			bookingModel.setNight(days);
			bookingModel.setCustomerId(booking.getAccount().getAccountId());
			bookingModel.setCustomerName(booking.getAccount().getUsername());
			bookingModel.setHouseName(booking.getHouse().getTitle());
			bookingModel.setHouseId(booking.getHouse().getId());
			Rating rating = ratingRepository.findByBookingId(booking.getId());
			if (rating != null) {
				bookingModel.setRating(rating);
			}
			listBookingModel.add(bookingModel);
		}

		if (page >= pageMax) {
			message.setMessage(CommonConstants.INVALID_PAGE);
		}

		paginationModel.setListObject(listBookingModel);
		paginationModel.setPageMax(pageMax);

		message.setData(paginationModel);
		message.setMessage(UserConstants.GET_INFORMATION);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);
	}

	@Override
	public ResponseEntity<?> payment(int bookingId) {
		MessageModel message = new MessageModel();
		Booking bookingCurrent = bookingRepository.findById(bookingId).get();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		int houseId = bookingRepository.findById(bookingId).get().getHouse().getId();
		List<Booking> listBookings = bookingRepository.findByHouseId(houseId);

		if (!bookingRepository.findById(bookingId).isPresent()) {
			message.setMessage(BookingConstants.BOOKING_NOT_EXITST);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (!idCurrent.equals(bookingCurrent.getAccount().getAccountId())) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			message.setStatus(HttpStatus.UNAUTHORIZED.value());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		Date dateCheckIn = bookingCurrent.getDateCheckIn();
		Date dateCheckOut = bookingCurrent.getDateCheckOut();

		for (Booking booking : listBookings) {
			if ((dateCheckIn.compareTo(booking.getDateCheckOut()) >= 0
					&& dateCheckOut.compareTo(booking.getDateCheckIn()) < 0
					|| dateCheckOut.compareTo(booking.getDateCheckIn()) > 0
							&& dateCheckIn.compareTo(booking.getDateCheckOut()) <= 0)
					&& (StatusBooking.PAID.getStatusName().equals(booking.getStatus())
							|| StatusBooking.COMPLETED.getStatusName().equals(booking.getStatus()))) {
				message.setMessage(BookingConstants.HOUSE_BOOKED);
				message.setStatus(HttpStatus.BAD_REQUEST.value());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}
		}

		if (!StatusBooking.PENDING.getStatusName().equals(bookingCurrent.getStatus())) {
			message.setMessage(BookingConstants.INVALID_STATUS);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		SimpleMailMessage messageEmailHost = new SimpleMailMessage();
		messageEmailHost.setTo(bookingCurrent.getHouse().getAccount().getEmail());
		messageEmailHost.setSubject("BOOKING CONFIRMATION ");
		messageEmailHost.setText("Wellcome " + bookingCurrent.getHouse().getAccount().getUsername()
				+ "\nHave booked your home " + bookingCurrent.getHouse().getTitle() + "\nRented on the date : "
				+ bookingCurrent.getDateCheckIn() + " to " + bookingCurrent.getDateCheckOut() + "\nBy customer "
				+ bookingCurrent.getAccount().getUser().getLastName() + " "
				+ bookingCurrent.getAccount().getUser().getFirstName());
		sender.send(messageEmailHost);

		SimpleMailMessage messageEmailCustomer = new SimpleMailMessage();
		messageEmailCustomer.setTo(bookingCurrent.getAccount().getEmail());
		messageEmailCustomer.setSubject("YOUR RESERVATION IS PAID SUCCESS ON MASTER TRAVEL");
		messageEmailCustomer.setText("Wellcome " + bookingCurrent.getAccount().getUser().getLastName() + " "
				+ bookingCurrent.getAccount().getUser().getFirstName()
				+ "\nYour payment is successfull\nRented on the date : " + bookingCurrent.getDateCheckIn() + " to "
				+ bookingCurrent.getDateCheckOut() + "\nTotal cost " + bookingCurrent.getBill()
				+ "\nAny questions please contact " + bookingCurrent.getHouse().getAccount().getUsername()
				+ "\nThrough phone number " + bookingCurrent.getHouse().getPhoneContact());
		sender.send(messageEmailCustomer);

		bookingCurrent.setStatus(StatusBooking.PAID.getStatusName());
		bookingRepository.saveAndFlush(bookingCurrent);
		message.setData(bookingCurrent);
		message.setStatus(HttpStatus.OK.value());
		message.setMessage(CommonConstants.SUCCESS);
		return ResponseEntity.ok(message);

	}

	@Override
	public ResponseEntity<?> receiveBooking(int houseId, DateModel dateModel) {

		MessageModel message = new MessageModel();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Account account = accountRepository.findById(idCurrent).get();
		House house = houseRepository.findById(houseId).get();

		if (house == null) {
			message.setMessage(HouseConstants.HOUSE_NOT_EXIST);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (idCurrent.equals(house.getAccount().getAccountId())) {
			message.setMessage(BookingConstants.ACCOUNT_WITHOUT_PERMISSION);
			message.setStatus(HttpStatus.UNAUTHORIZED.value());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		if (!StatusHouse.LISTED.getStatusName().equals(house.getStatus()) || house.isBlock() == true) {
			message.setMessage(HouseConstants.UNLISTED_BLOCK);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		List<BookingDTO> listBookings = bookingRepository.getByHouseId(houseId);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		Date dateCheckIn = null;
		Date dateCheckOut = null;
		Date currentDate = new Date();
		String dateNow = dateFormat.format(currentDate);

		try {
			dateCheckIn = dateFormat.parse(dateModel.getDateCheckIn());
//			dateCheckIn = new Date(dateCheckIn.getTime() + 14 * HOUR);
			dateCheckOut = dateFormat.parse(dateModel.getDateCheckOut());
//			dateCheckOut = new Date(dateCheckOut.getTime() + 12 * HOUR);
			currentDate = dateFormat.parse(dateNow);
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}

		if (TimeUnit.MILLISECONDS.toDays(dateCheckIn.getTime() - currentDate.getTime()) < 0) {
			message.setMessage(BookingConstants.INVALID_CHECKIN);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (dateCheckIn.compareTo(dateCheckOut) >= 0) {
			message.setMessage(BookingConstants.INVALID_CHECKOUT);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		for (BookingDTO booking : listBookings) {
			if ((dateCheckIn.compareTo(booking.getDateCheckIn()) >= 0
					&& dateCheckIn.compareTo(booking.getDateCheckOut()) < 0
					|| dateCheckOut.compareTo(booking.getDateCheckIn()) > 0
							&& dateCheckOut.compareTo(booking.getDateCheckOut()) <= 0)) {
				message.setMessage(BookingConstants.HOUSE_BOOKED);
				message.setStatus(HttpStatus.NOT_FOUND.value());

				logger.info(String.valueOf(booking.getDateCheckIn()));
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			}
		}

		long days = TimeUnit.MILLISECONDS.toDays(dateCheckOut.getTime() - dateCheckIn.getTime());
		double price = house.getPrice();
		double bill = price * days;
		Booking booking = new Booking();
		booking.setAccount(account);
		booking.setHouse(house);
		booking.setStatus(StatusBooking.PENDING.getStatusName());
		booking.setDateCheckIn(dateCheckIn);
		booking.setDateCheckOut(dateCheckOut);
		booking.setBill(bill);
		bookingRepository.saveAndFlush(booking);
		message.setData(booking);
		message.setMessage(CommonConstants.SUCCESS);
		message.setStatus(HttpStatus.OK.value());
		return ResponseEntity.ok(message);
	}

}
