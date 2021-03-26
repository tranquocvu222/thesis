package ces.riccico.service;

import org.springframework.http.ResponseEntity;

import ces.riccico.entity.Rating;

public interface RatingService {
	
	ResponseEntity<?>  findByRatingAccountId(int accountId);
	
	ResponseEntity<?> findRatingByHouseId(int houseId);
	
	ResponseEntity<?> getRatingDetail(int ratingId);
	
	ResponseEntity<?> writeRating(int idBooking, Rating rating);
	
	ResponseEntity<?> updateRating(int ratingId, Rating rating);
}
