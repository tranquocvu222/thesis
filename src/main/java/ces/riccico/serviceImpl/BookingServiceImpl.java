package ces.riccico.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ces.riccico.common.constants.BookingConstants;
import ces.riccico.common.constants.CommonConstants;
import ces.riccico.common.constants.HouseConstants;
import ces.riccico.common.constants.UserConstants;
import ces.riccico.common.enums.StatusBooking;
import ces.riccico.entity.Account;
import ces.riccico.entity.Booking;
import ces.riccico.entity.House;
import ces.riccico.entity.Rating;
import ces.riccico.model.BookingDetailModel;
import ces.riccico.model.DateModel;
import ces.riccico.model.MessageModel;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.BookingRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.repository.RatingRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.BookingService;
import ces.riccico.service.HouseService;

@Service
public class BookingServiceImpl implements BookingService {

	private static Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

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

	@Autowired
	private HouseService houseService;

//	@Override
//	public ResponseEntity<?> acceptBooking(int bookingId) {
//		MessageModel message = new MessageModel();
//		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
//		Booking booking = bookingRepository.findById(bookingId).get();
//		
//		if (!bookingRepository.findById(bookingId).isPresent()) {
//			message.setMessage(BookingConstants.BOOKING_NOT_EXITST);
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
//		}
//
//		if (!idCurrent.equals(bookingRepository.findById(bookingId).get().getHouse().getAccount().getAccountId())) {
//			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
//		}
//		
//		if (!Status.PAID.getStatusName().equals(booking.getStatus())) {
//			message.setMessage(BookingConstants.INVALID_STATUS);
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
//		}
//
//		booking.setStatus(Status.APPROVAL.getStatusName());
//		bookingRepository.saveAndFlush(booking);
//		message.setMessage(CommonConstants.SUCCESS);
//		return ResponseEntity.ok(message);
//	}

	public ResponseEntity<?> cancelBooking(int bookingId, boolean click) {
		MessageModel message = new MessageModel();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Booking booking = bookingRepository.findById(bookingId).get();
		Date dateCheckIn = booking.getDateCheckIn();
		Date dateCreateAt = booking.getCreatedAt();

		long hours = TimeUnit.MILLISECONDS.toHours(dateCheckIn.getTime() - dateCreateAt.getTime());
		System.out.println("====== " + hours);

		if (!bookingRepository.findById(bookingId).isPresent()) {
			message.setMessage(BookingConstants.BOOKING_NOT_EXITST);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (!idCurrent.equals(booking.getHouse().getAccount().getAccountId())
				&& !idCurrent.equals(booking.getAccount().getAccountId())) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		if (!StatusBooking.PAID.getStatusName().equals(booking.getStatus())) {
			message.setMessage(BookingConstants.INVALID_STATUS);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (hours < 24) {
			if (click == false) {
				float bill = (float) (booking.getBill() * 25) / 100;
				message.setMessage(BookingConstants.CANCEL_BOOKING);
				message.setMessage("Cancel fee " + bill);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else {

				System.out.println("========== " + click);
				booking.setStatus(StatusBooking.CANCELED.getStatusName());
				bookingRepository.saveAndFlush(booking);

				if (idCurrent.equals(booking.getAccount().getAccountId())) {
					message.setMessage(BookingConstants.BY_CUSTOMER);
					return ResponseEntity.ok(message);
				}
				message.setMessage(BookingConstants.BY_HOST);
				return ResponseEntity.ok(message);

			}
		} else {

			booking.setStatus(StatusBooking.CANCELED.getStatusName());
			bookingRepository.saveAndFlush(booking);
		}

		if (idCurrent.equals(booking.getAccount().getAccountId())) {
			SimpleMailMessage messageEmailCustomer = new SimpleMailMessage();
			messageEmailCustomer.setTo(booking.getAccount().getEmail());
			messageEmailCustomer.setSubject("BOOKING CENCELED");
			messageEmailCustomer.setText("Wellcome " + booking.getAccount().getUser().getLastName() + " "
					+ booking.getAccount().getUser().getFirstName()
					+ "\nYou booking canceled successfull \nRented on the date : " + booking.getDateCheckIn() + " to "
					+ booking.getDateCheckOut() + "\nAt Homestay " + booking.getHouse().getTitle()
					+ "\nThe total cost you get after deducting cancellation fee is : " + (booking.getBill() * 25) / 100
					+ "\nAny questions please contact " + booking.getHouse().getAccount().getUsername()
					+ "\nThrough phone number " + booking.getHouse().getPhoneContact());
			sender.send(messageEmailCustomer);

			message.setMessage(BookingConstants.BY_CUSTOMER);
			return ResponseEntity.ok(message);
		}

		message.setMessage(BookingConstants.BY_HOST);
		return ResponseEntity.ok(message);
	}


	@Override
	public ResponseEntity<?> completeBooking̣̣̣() {
		List<Booking> listBooking = bookingRepository.findBookingPaid();
		Date currentDate = new Date();

		for (Booking booking : listBooking) {
			Date dateCheckOut = booking.getDateCheckOut();
			if (dateCheckOut.compareTo(currentDate) <0 ) {
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

		return ResponseEntity.ok(listBooking);

	}

	@Override
	public ResponseEntity<?> findByAccountId(int accountId) {
		MessageModel message = new MessageModel();
		List<Booking> listBookings = bookingRepository.findByAccountId(accountId);
		List<BookingDetailModel> listBookingModels = new ArrayList<BookingDetailModel>();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();

		if (idCurrent != accountId) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		if (listBookings.size() == 0) {
			message.setMessage(BookingConstants.NULL_BOOKING);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

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
			listBookingModels.add(bookingModel);
		}
		return ResponseEntity.ok(listBookingModels);
	}

	@Override
	public ResponseEntity<?> findByHouseId(int houseId) {
		MessageModel message = new MessageModel();
		List<Booking> listBookings = bookingRepository.findByHouseId(houseId);
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		List<BookingDetailModel> listBookingModels = new ArrayList<BookingDetailModel>();

		if (!idCurrent.equals(houseRepository.findById(houseId).get().getAccount().getAccountId())) {

			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		if (listBookings.size() == 0) {
			message.setMessage(BookingConstants.NULL_BOOKING);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		for (Booking booking : listBookings) {
			BookingDetailModel bookingModel = mapper.map(booking, BookingDetailModel.class);
			bookingModel.setCustomerId(booking.getAccount().getAccountId());
			bookingModel.setCustomerName(booking.getAccount().getUsername());
			bookingModel.setHouseName(booking.getHouse().getTitle());
			bookingModel.setHouseId(booking.getHouse().getId());
			listBookingModels.add(bookingModel);
		}
		return ResponseEntity.ok(listBookingModels);
	}

	@Override
	public ResponseEntity<?> getAlḷ() {
		return ResponseEntity.ok(bookingRepository.findAll());
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
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		return ResponseEntity.ok(booking);
	}

	@Override
	public ResponseEntity<?> getBookingDate(int houseId) {
		// TODO Auto-generated method stub
		MessageModel message = new MessageModel();

		if (!houseRepository.findById(houseId).isPresent()) {
			message.setMessage(HouseConstants.HOUSE_NOT_EXIST);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		House house = houseRepository.findById(houseId).get();

		if (house.isApproved() == false || house.isDeleted() == true) {
			message.setMessage(HouseConstants.HOUSE_DELETED);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		List<Booking> listBookings = bookingRepository.findByHouseId(houseId);

		if (listBookings.size() == 0) {
			message.setMessage(BookingConstants.NULL_BOOKING);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

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
			if (TimeUnit.MILLISECONDS.toDays(booking.getDateCheckOut().getTime() - currentDate.getTime()) > 0) {
				DateModel dateModel = new DateModel();
				dateModel.setDateCheckIn(booking.getDateCheckIn().toString());
				dateModel.setDateCheckOut(booking.getDateCheckOut().toString());
				listDateModel.add(dateModel);
			}
		}

		return ResponseEntity.ok(listDateModel);
	}

	@Override
	public ResponseEntity<?> payment(int bookingId) {
		MessageModel message = new MessageModel();
		Booking bookingCurrent = bookingRepository.findById(bookingId).get();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		int houseId = bookingRepository.findById(bookingId).get().getHouse().getId();
		List<Booking> listBookings = bookingRepository.findByHouseId(houseId);
		System.out.println("=" + listBookings.size());

		if (!bookingRepository.findById(bookingId).isPresent()) {
			message.setMessage(BookingConstants.BOOKING_NOT_EXITST);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}

		if (!idCurrent.equals(bookingCurrent.getAccount().getAccountId())) {
			message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
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
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			}
		}

		if (!StatusBooking.PENDING_PAYMENT.getStatusName().equals(bookingCurrent.getStatus())) {
			message.setMessage(BookingConstants.INVALID_STATUS);
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
		message.setMessage(CommonConstants.SUCCESS);
		return ResponseEntity.ok(message);

	}

	@Override
	public ResponseEntity<?> receiveBooking(int houseId, DateModel dateModel) {

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
		Date dateCheckIn = null;
		Date dateCheckOut = null;
		Date currentDate = new Date();
		String dateNow = dateFormat.format(currentDate);

		try {
			dateCheckIn = dateFormat.parse(dateModel.getDateCheckIn());
			dateCheckOut = dateFormat.parse(dateModel.getDateCheckOut());
			currentDate = dateFormat.parse(dateNow);
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}

		if (TimeUnit.MILLISECONDS.toDays(dateCheckIn.getTime() - currentDate.getTime()) < 0) {
			message.setMessage(BookingConstants.INVALID_CHECKIN);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		if (dateCheckIn.compareTo(dateCheckOut) >= 0) {
			message.setMessage(BookingConstants.INVALID_CHECKOUT);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		for (Booking booking : listBookings) {
			if ((dateCheckIn.compareTo(booking.getDateCheckIn()) >= 0
					&& dateCheckIn.compareTo(booking.getDateCheckOut()) < 0
					|| dateCheckOut.compareTo(booking.getDateCheckIn()) > 0
							&& dateCheckOut.compareTo(booking.getDateCheckOut()) <= 0)
					&& (StatusBooking.PAID.getStatusName().equals(booking.getStatus())
							|| StatusBooking.COMPLETED.getStatusName().equals(booking.getStatus()))) {
				message.setMessage(BookingConstants.HOUSE_BOOKED);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			}
		}

		long days = TimeUnit.MILLISECONDS.toDays(dateCheckOut.getTime() - dateCheckIn.getTime());
		double price = house.getPrice();
		double bill = price * days;
		Booking booking = new Booking();
		booking.setAccount(account);
		booking.setHouse(house);
		booking.setStatus(StatusBooking.PENDING_PAYMENT.getStatusName());
		booking.setDateCheckIn(dateCheckIn);
		booking.setDateCheckOut(dateCheckOut);
		booking.setBill(bill);
		houseRepository.saveAndFlush(house);
		bookingRepository.saveAndFlush(booking);
		return ResponseEntity.ok(booking);

	}

}
