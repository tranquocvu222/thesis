package ces.riccico.service;

import org.springframework.http.ResponseEntity;

import ces.riccico.entity.Rating;

public interface RatingService {
	
	ResponseEntity<?>  findByRatingAccountId();
	
	ResponseEntity<?> findRatingByHouseId(int houseId);
	
	ResponseEntity<?> writeRating(int idBooking, Rating rating);

}
