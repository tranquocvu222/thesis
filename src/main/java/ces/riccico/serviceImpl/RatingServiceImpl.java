package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.notification.Notification;
import ces.riccico.entities.Booking;
import ces.riccico.models.Message;
import ces.riccico.entities.Rating;
import ces.riccico.models.RatingAccountModel;
import ces.riccico.models.RatingHouseModel;
import ces.riccico.models.Status;
import ces.riccico.notification.BookingNotification;
import ces.riccico.notification.HouseNotification;
import ces.riccico.notification.RatingNotification;
import ces.riccico.notification.UserNotification;
import ces.riccico.repository.BookingRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.repository.RatingRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.RatingService;

@Service
public class RatingServiceImpl implements RatingService {


	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private HouseRepository houseRepository;

	@Autowired
	private RatingRepository ratingRepository;

	@Autowired
	private SecurityAuditorAware securityAuditorAware;

	@Autowired
	private ModelMapper mapper;
	
//	Find rating by id_house
	@Override
	public ResponseEntity<?> findRatingByHouseId(int houseId) {
		
		Message message = new Message();
		
		try {
			
			List<Rating> listRating = new ArrayList<Rating>();
			
			if (!houseRepository.findById(houseId).isPresent()) {
				message.setMessage(HouseNotification.HOUSE_NOT_EXIST);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			} else {
				listRating = ratingRepository.findByBookingHouseId(houseId);
				List<RatingHouseModel> listRatingModel = new ArrayList<RatingHouseModel>();
				if (listRating.size() == 0) {
					message.setMessage(RatingNotification.NULL_RATING);
					return ResponseEntity.ok(message);
				} else {
					for (Rating rating : listRating) {
						RatingHouseModel ratingModel = new RatingHouseModel();
						ratingModel.setRating(rating);
						ratingModel.setUsername(rating.getBooking().getAccount().getUsername());
						listRatingModel.add(ratingModel);
					}
					return ResponseEntity.ok(listRatingModel);
				}
			}
			
		} catch (Exception e) {
			
			message.setMessage(Notification.FAIL);
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	
//	Find rating by id_account
	@Override
	public ResponseEntity<?> findByRatingAccountId() {
		
		Message message = new Message();
		
		try {
			
			Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
			List<Rating> listRating = new ArrayList<Rating>();
			listRating = ratingRepository.findByBookingAccountIdAccount(idCurrent);
			List<RatingAccountModel> listRatingModel = new ArrayList<RatingAccountModel>();
			
			if (listRating.size() == 0) {
				message.setMessage(RatingNotification.NULL_RATING);
				return ResponseEntity.ok(message);
			} else {
				for (Rating rating : listRating) {
					RatingAccountModel ratingModel = new RatingAccountModel();
					ratingModel.setRating(rating);
					ratingModel.setHouseName(rating.getBooking().getHouse().getTitle());
					listRatingModel.add(ratingModel);
				}
				return ResponseEntity.ok(listRatingModel);
			}
			
		} catch (Exception e) {
			
			message.setMessage(Notification.FAIL);
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	
//	Write rating house 	
	@Override
	public ResponseEntity<?> writeRating(int idBooking, Rating rating) {
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Booking booking = bookingRepository.findById(idBooking).get();
		Message message = new Message();
		
		try {
			
			if (!bookingRepository.findById(idBooking).isPresent()) {
				message.setMessage(BookingNotification.BOOKING_NOT_EXITST);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			} else {
				if (!Status.COMPLETED.getStatusName().equals(booking.getStatus())) {
					message.setMessage(BookingNotification.INVALID_STATUS);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				} else {
					if (!idCurrent.equals(booking.getAccount().getIdAccount())) {
						message.setMessage(UserNotification.ACCOUNT_NOT_PERMISSION);
						return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
					} else {
						if (ratingRepository.findByBookingId(idBooking) != null) {
							message.setMessage(RatingNotification.IS_RATED);
							return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
						}
						Rating ratingNew = mapper.map(rating, Rating.class);
						ratingNew.setBooking(booking);
						ratingRepository.saveAndFlush(ratingNew);
						message.setMessage(Notification.FAIL);
						
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
					}
				}
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		message.setMessage(Notification.FAIL);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	
	}

	

}