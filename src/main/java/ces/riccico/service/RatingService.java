package ces.riccico.service;

import org.springframework.http.ResponseEntity;

import ces.riccico.models.Rating;

public interface RatingService {
	ResponseEntity<?> writeRating(int idBooking, Rating rating);
	ResponseEntity<?> findByBookingHouseId(int houseId);
	ResponseEntity<?>  findByBookingAccountId();
}
