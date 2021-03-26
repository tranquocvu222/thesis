package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.common.constants.BookingConstants;
import ces.riccico.common.constants.HouseConstants;
import ces.riccico.common.constants.RatingConstants;
import ces.riccico.common.constants.UserConstants;
import ces.riccico.common.enums.Status;
import ces.riccico.entity.Booking;
import ces.riccico.entity.Rating;
import ces.riccico.model.MessageModel;
import ces.riccico.model.RatingAccountModel;
import ces.riccico.model.RatingHouseModel;
import ces.riccico.repository.BookingRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.repository.RatingRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.RatingService;

@Service
public class RatingServiceImpl implements RatingService {

	private static Logger logger = LoggerFactory.getLogger(RatingServiceImpl.class);

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

		MessageModel message = new MessageModel();

		try {
			List<Rating> listRating = new ArrayList<Rating>();

			if (!houseRepository.findById(houseId).isPresent()) {
				message.setMessage(HouseConstants.HOUSE_NOT_EXIST);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			}

			listRating = ratingRepository.findByBookingHouseId(houseId);
			List<RatingHouseModel> listRatingModel = new ArrayList<RatingHouseModel>();

			if (listRating.size() == 0) {
				message.setMessage(RatingConstants.NULL_RATING);
				return ResponseEntity.ok(message);
			}
			
			RatingHouseModel ratingModel = new RatingHouseModel();
			for (Rating rating : listRating) {
				ratingModel.setRating(rating);
				ratingModel.setUsername(rating.getBooking().getAccount().getUsername());
				ratingModel.setCreatedAt(rating.getCreatedAt());
				listRatingModel.add(ratingModel);
			}

			return ResponseEntity.ok(listRatingModel);

		} catch (Exception e) {
			logger.error(e.getMessage());
			message.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

//	Find rating by id_account
	@Override
	public ResponseEntity<?> findByRatingAccountId() {

		MessageModel message = new MessageModel();

		try {
			Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
			List<Rating> listRating = new ArrayList<Rating>();
			listRating = ratingRepository.findByBookingAccountIdAccount(idCurrent);
			List<RatingAccountModel> listRatingModel = new ArrayList<RatingAccountModel>();

			if (listRating.size() == 0) {
				message.setMessage(RatingConstants.NULL_RATING);
				return ResponseEntity.ok(message);
			}
			
			RatingAccountModel ratingModel = new RatingAccountModel();
			for (Rating rating : listRating) {
				ratingModel.setRating(rating);
				ratingModel.setHouseName(rating.getBooking().getHouse().getTitle());
				ratingModel.setCreatedAt(rating.getCreatedAt());
				listRatingModel.add(ratingModel);
			}
			return ResponseEntity.ok(listRatingModel);

		} catch (NullPointerException e) {
			logger.error(e.getMessage());
			message.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

//	Write rating house 	
	@Override
	public ResponseEntity<?> writeRating(int idBooking, Rating rating) {
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Booking booking = bookingRepository.findById(idBooking).get();
		MessageModel message = new MessageModel();

		try {
			if (!idCurrent.equals(booking.getAccount().getIdAccount())) {
				message.setMessage(UserConstants.ACCOUNT_NOT_PERMISSION);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
			}
			
			if (!bookingRepository.findById(idBooking).isPresent()) {
				message.setMessage(BookingConstants.BOOKING_NOT_EXITST);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			}

			if (!Status.COMPLETED.getStatusName().equals(booking.getStatus())) {
				message.setMessage(BookingConstants.INVALID_STATUS);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			if (ratingRepository.findByBookingId(idBooking) != null) {
				message.setMessage(RatingConstants.IS_RATED);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			Rating ratingNew = mapper.map(rating, Rating.class);
			ratingNew.setBooking(booking);
			ratingRepository.saveAndFlush(ratingNew);
			return ResponseEntity.ok(ratingNew);

		} catch (Exception e) {
			logger.error(e.getMessage());
			message.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

	}

}