package ces.riccico.service;

import org.springframework.http.ResponseEntity;

public interface BookingService {

	ResponseEntity<?> acceptBooking(int bookingId);

	ResponseEntity<?> cancelBooking(int bookingId);

	ResponseEntity<?> completeBooking̣̣̣(int bookingId);

	ResponseEntity<?> findByAccountId(int accountId);

	ResponseEntity<?> findByHouseId(int houseId) ;

	ResponseEntity<?> getAlḷ();

	ResponseEntity<?> getBookingDetail(int bookingId);

	ResponseEntity<?> payment(int bookingId);

	ResponseEntity<?> receiveBooking(int houseId, String dateStart, String dateStop);

	ResponseEntity<?> refund(int bookingId);

}
