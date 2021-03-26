package ces.riccico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import ces.riccico.entity.Booking;

public interface BookingService {
	
	ResponseEntity<?> acceptBooking(int bookingId);

	ResponseEntity<?> cancelBooking(int bookingId);

	ResponseEntity<?> completeBooking̣̣̣(int bookingId);

	ResponseEntity<?> findByAccountId(int accountId);
	
	ResponseEntity<?> findByHouseId(int houseId);

	List<Booking> getAlḷ();
	
	ResponseEntity<?> getBookingDetail(int bookingId);
	
	ResponseEntity<?> payment(int bookingId);

	ResponseEntity<?> receiveBooking(int houseId, String dateStart, String dateStop);

	ResponseEntity<?> refund(int bookingId);

}
