package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.Booking;
import ces.riccico.models.Rating;
import ces.riccico.notification.AuthNotification;
import ces.riccico.notification.BookingNotification;
import ces.riccico.notification.HouseNotification;
import ces.riccico.notification.UserNotification;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.BookingRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.repository.RatingRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.RatingService;

@Service
public class RatingServiceImpl implements RatingService {

	private static final String COMPLETED = "completed";

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private HouseRepository houseRepository;

	@Autowired
	private RatingRepository ratingRepository;

	@Autowired
	private SecurityAuditorAware securityAuditorAware;

	@Override
	public ResponseEntity<?> writeRating(int idBooking, Rating rating) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Booking booking = bookingRepository.findById(idBooking).get();
		if (!bookingRepository.findById(idBooking).isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BookingNotification.bookingNotExist);
		} else {
			if (!COMPLETED.equals(booking.getStatus().getStatusName())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BookingNotification.invalidStatus);
			} else {
				if (!idCurrent.equals(booking.getAccount().getIdAccount())) {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UserNotification.accountNotPermission);
				}else {
					Rating ratingNew = new Rating();
					ratingNew.setBooking(booking);
					ratingNew.setStar(rating.getStar());
					ratingNew.setContent(rating.getContent());
					ratingRepository.saveAndFlush(ratingNew);
				}
			}
		}
		 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthNotification.fail);
	}

	@Override
	public ResponseEntity<?> findByBookingHouseId(int houseId) {
		List<Rating> listRating = new ArrayList<Rating>();
		if(!houseRepository.findById(houseId).isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HouseNotification.houseNotExist);
		}
		listRating  = ratingRepository.findByBookingHouseId(houseId);
		return ResponseEntity.ok(listRating);
	}

	@Override
	public ResponseEntity<?> findByBookingAccountId(String accountId) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		List<Rating> listRating = new ArrayList<Rating>();
		if(!accountRepository.findById(accountId).isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(UserNotification.accountNotExist);
		}else {
			
		}
		return null;
	}

}
