package ces.riccico.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.Accounts;
import ces.riccico.models.Booking;
import ces.riccico.models.House;
import ces.riccico.notification.Notification;
import ces.riccico.notification.HouseNotification;
import ces.riccico.notification.UserNotification;
import ces.riccico.notification.BookingNotification;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.BookingRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.repository.StatusRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.BookingService;

@Service
public class BookingServiceImpl implements BookingService {
	private static final String PENDING_APPROVAL = "pending approval";
	private static final String APPROVAL = "approval";
	private static final String CANCELED = "canceled";
	private static final String PENDING_PAYMENT = "pending payment";
	private static final String REFUNDED = "refunded";
	private static final String COMPLETED = "completed";

	@Autowired
	private HouseRepository houseRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private StatusRepository statusRepository;

	@Autowired
	private SecurityAuditorAware securityAuditorAware;

	@Override
	public List<Booking> getAlḷ() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Booking> getByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Booking> findByHouseId(int idHouse) {
		return bookingRepository.findByHouseId(idHouse);
	}

	public ResponseEntity<?> receiveBooking(int idHouse, String dateStart, String dateStop) {
		try {
			String idCurrent = securityAuditorAware.getCurrentAuditor().get();
			Accounts account = accountRepository.findById(idCurrent).get();
			House house = houseRepository.findById(idHouse).get();
			if (idCurrent.equals(house.getAccount().getIdAccount())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of(Notification.message, BookingNotification.accountNotPermission));
			} else {
				if (!houseRepository.findById(idHouse).isPresent()) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND)
							.body(Map.of(Notification.message, HouseNotification.houseNotExist));
				} else {
					List<Booking> listBookings = new ArrayList<Booking>();
					try {
						listBookings = bookingRepository.findByHouseId(idHouse);
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
					Date dateIn = null;
					Date dateOut = null;
					Date currentDate = new Date();
					String dateNow = sdf.format(currentDate);
					try {
						dateIn = sdf.parse(dateStart);
						dateOut = sdf.parse(dateStop);
						currentDate = sdf.parse(dateNow);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if (TimeUnit.MILLISECONDS.toDays(dateIn.getTime() - currentDate.getTime()) < 0) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body(Map.of(Notification.message, BookingNotification.invalidDate));
					}
					if (dateIn.compareTo(dateOut) > 0) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body(Map.of(Notification.message, BookingNotification.invalidDateOut));
					}
					for (Booking booking : listBookings) {
						if ((dateIn.compareTo(booking.getCreateCheckIn()) >= 0
								&& dateIn.compareTo(booking.getCreateEnd()) < 0
								|| dateOut.compareTo(booking.getCreateCheckIn()) > 0
										&& dateOut.compareTo(booking.getCreateEnd()) <= 0)
								&& !CANCELED.equals(booking.getStatus())) {
							return ResponseEntity.status(HttpStatus.BAD_REQUEST)
									.body(Map.of(Notification.message, BookingNotification.isBooked));
						}
					}
					long days = TimeUnit.MILLISECONDS.toDays(dateOut.getTime() - dateIn.getTime());
					double price = house.getPrice();
					double bill = price * days;
					Booking booking = new Booking();
					booking.setAccount(account);
					booking.setHouse(house);
					booking.setStatus(statusRepository.findByStatusName(PENDING_PAYMENT));
					booking.setCreateCheckIn(dateIn);
					booking.setCreateEnd(dateOut);
					booking.setBill(bill);
					houseRepository.saveAndFlush(house);
					bookingRepository.saveAndFlush(booking);
					return ResponseEntity.ok(booking);
				}
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message, Notification.fail));
		}
	}

	@Override
	public ResponseEntity<?> acceptBooking(int idBooking) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		if (!bookingRepository.findById(idBooking).isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of(Notification.message, BookingNotification.bookingNotExist));
		}
		if (!idCurrent.equals(bookingRepository.findById(idBooking).get().getHouse().getAccount().getIdAccount())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of(Notification.message, UserNotification.accountNotPermission));
		}
		Booking booking = bookingRepository.findById(idBooking).get();
		booking.setStatus(statusRepository.findByStatusName(APPROVAL));
		bookingRepository.saveAndFlush(booking);
		return ResponseEntity.ok(Map.of(Notification.message, Notification.success));
	}

	@Override
	public ResponseEntity<?> cancelBooking(int idBooking) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Booking booking = bookingRepository.findById(idBooking).get();
		if (!bookingRepository.findById(idBooking).isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of(Notification.message, BookingNotification.bookingNotExist));
		} else {
			if (!idCurrent.equals(booking.getHouse().getAccount().getIdAccount())
					|| !idCurrent.equals(booking.getAccount().getIdAccount())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of(Notification.message, UserNotification.accountNotPermission));
			} else {
				booking.setStatus(statusRepository.findByStatusName(CANCELED));
				bookingRepository.saveAndFlush(booking);
				if (idCurrent.equals(booking.getAccount().getIdAccount())) {
					return ResponseEntity.ok(Map.of(Notification.message, BookingNotification.byCustomer));
				} else {
					return ResponseEntity.ok(Map.of(Notification.message, BookingNotification.byOwner));
				}
			}
		}
	}

	@Override
	public ResponseEntity<?> completeBooking̣̣̣(int idBooking) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		Date currentDate = new Date();
		String dateNow = sdf.format(currentDate);
		try {
			currentDate = sdf.parse(dateNow);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			Booking booking = bookingRepository.findById(idBooking).get();
			if (!bookingRepository.findById(idBooking).isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of(Notification.message, BookingNotification.bookingNotExist));
			}
			if (!APPROVAL.equals(booking.getStatus().getStatusName())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, BookingNotification.invalidStatus));
			}
			if (currentDate.compareTo(booking.getCreateEnd()) < 0) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, BookingNotification.invalidDateComplete));
			}
			booking.setStatus(statusRepository.findByStatusName(COMPLETED));
			bookingRepository.saveAndFlush(booking);
			return ResponseEntity.ok(Map.of(Notification.message, Notification.success));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message, Notification.fail));
		}
	}

	@Override
	public ResponseEntity<?> payment(int idBooking) {
		try {
			Booking booking = bookingRepository.findById(idBooking).get();
			String idCurrent = securityAuditorAware.getCurrentAuditor().get();
			if (!bookingRepository.findById(idBooking).isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of(Notification.message, BookingNotification.bookingNotExist));
			}
			if (!idCurrent.equals(booking.getAccount().getIdAccount())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of(Notification.message, UserNotification.accountNotPermission));
			}
			if (!PENDING_PAYMENT.equals(booking.getStatus().getStatusName())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, BookingNotification.invalidStatus));
			}
			booking.setStatus(statusRepository.findByStatusName(PENDING_APPROVAL));
			bookingRepository.saveAndFlush(booking);
			return ResponseEntity.ok(Map.of(Notification.message, Notification.success));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message, Notification.fail));
		}
	}

	@Override
	public ResponseEntity<?> refund(int idBooking) {
		// TODO Auto-generated method stub
		return null;
	}
}
