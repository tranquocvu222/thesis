package ces.riccico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import ces.riccico.entities.Booking;

public interface BookingService {
	
	ResponseEntity<?> acceptBooking(int bookingId);

	ResponseEntity<?> cancelBooking(int bookingId);

	ResponseEntity<?> completeBooking̣̣̣(int bookingId);
	
	List<Booking> findByHouseId(int houseId);

	List<Booking> getAlḷ();

	List<Booking> getByUsername(String username);
	
	ResponseEntity<?> payment(int bookingId);

	ResponseEntity<?> receiveBooking(int houseId, String dateStart, String dateStop);

	ResponseEntity<?> refund(int bookingId);

}
