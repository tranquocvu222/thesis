package ces.riccico.service;

import org.springframework.http.ResponseEntity;

import ces.riccico.entity.Rating;

public interface RatingService {


	ResponseEntity<?> findRatingByAccountId(int accountId);

	ResponseEntity<?> findRatingByHouseId(int houseId);
	
	ResponseEntity<?> writeRating(int bookingId, Rating rating);

	ResponseEntity<?> getRatingDetail(int ratingId);
	
	ResponseEntity<?> updateRating(int ratingId, Rating rating);
}
