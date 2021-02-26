package ces.riccico.service;
import java.util.List;

import org.springframework.http.ResponseEntity;

import ces.riccico.models.Booking;
public interface BookingService {
	List<Booking> getAlḷ();
	List<Booking> getByUsername(String username);

	ResponseEntity<?> receiveBooking(int idHouse, String dateStart, String dateStop);

	ResponseEntity<?> acceptBooking(int idBooking);
	ResponseEntity<?> cancelBooking(int idBooking);
	ResponseEntity<?> completeBooking̣̣̣(int idBooking);
	ResponseEntity<?> payment(int idBooking);
	ResponseEntity<?> refund(int idBooking);
	List<Booking> findByHouseId(int idHouse);
}
