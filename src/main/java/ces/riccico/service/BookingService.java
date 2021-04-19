package ces.riccico.service;

import org.springframework.http.ResponseEntity;

import ces.riccico.model.DateModel;

public interface BookingService {

//	ResponseEntity<?> acceptBooking(int bookingId);

	ResponseEntity<?> cancelBooking(int bookingId, boolean click);

	ResponseEntity<?> completeBooking̣̣̣();

	ResponseEntity<?> findByAccountId(int accountId);

	ResponseEntity<?> findByHouseId(int houseId) ;

	ResponseEntity<?> getAlḷ();

	ResponseEntity<?> getBookingDetail(int bookingId);
	
	ResponseEntity<?> getBookingDate(int houseId);

	ResponseEntity<?> payment(int bookingId);

	ResponseEntity<?> receiveBooking(int houseId, DateModel dateModel);



}
