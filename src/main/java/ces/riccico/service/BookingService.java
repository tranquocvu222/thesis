package ces.riccico.service;

import org.springframework.http.ResponseEntity;

import ces.riccico.model.DateModel;

public interface BookingService {

//	ResponseEntity<?> acceptBooking(int bookingId);

	ResponseEntity<?> cancelBooking(int bookingId, boolean click);

	ResponseEntity<?> completeBooking̣̣̣();

	ResponseEntity<?> incompleteBooking();

	ResponseEntity<?> findByHouseId(int houseId);

	ResponseEntity<?> getBookingDetail(int bookingId);

	ResponseEntity<?> getBookingForCustomer(int accountId, String status, int page, int size);

	ResponseEntity<?> getBookingForHost(int accountId, String status, int page, int size);

	ResponseEntity<?> payment(int bookingId);

	ResponseEntity<?> receiveBooking(int houseId, DateModel dateModel);

}
