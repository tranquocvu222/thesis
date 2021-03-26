package ces.riccico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import ces.riccico.entities.Booking;

public interface BookingService {
	
	ResponseEntity<?> acceptBooking(int idBooking);

	ResponseEntity<?> cancelBooking(int idBooking);

	ResponseEntity<?> completeBooking̣̣̣(int idBooking);
	
	ResponseEntity<?> findByAccountId(int accountId);
	
	ResponseEntity<?> findByHouseId(int houseId);

	List<Booking> getAlḷ();

	List<Booking> getByUsername(String username);
	
	ResponseEntity<?> payment(int idBooking);

	ResponseEntity<?> receiveBooking(int idHouse, String dateStart, String dateStop);

	ResponseEntity<?> refund(int idBooking);

}
