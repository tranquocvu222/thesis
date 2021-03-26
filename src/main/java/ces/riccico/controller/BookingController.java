package ces.riccico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.service.BookingService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/bookings")
@CrossOrigin
public class BookingController {

	@Autowired
	private BookingService bookingService;

	// this is the feature of accepting guests' booking
	@PutMapping("/acceptBooking/{bookingId}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> acceptBooking(@PathVariable int bookingId){
		return bookingService.acceptBooking(bookingId);
	}

	// this is the cancellation feature
	@PutMapping("/cancelBooking/{bookingId}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> cancelBooking(@PathVariable int bookingId){
		return bookingService.cancelBooking(bookingId);
	}

	// this is the feature to confirm the booking has been completed
	@PutMapping("/completeBooking/{bookingId}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> completeBooking(@PathVariable int bookingId){
		return bookingService.completeBooking̣̣̣(bookingId);
	}

	// get list booking of account
	@GetMapping("/account/{accountId}")
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> getBookingByAccountId(@PathVariable int accountId) {
		return bookingService.findByAccountId(accountId);
	}

	// get list booking of house
	@GetMapping("/house/{houseId}")
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> getBookingByHouseId(@PathVariable int houseId) {
		return bookingService.findByHouseId(houseId);
	}

	// get booking detail
	@GetMapping("/{bookingId}")
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> getBookingDetail(@PathVariable int bookingId){
		return bookingService.getBookingDetail(bookingId);
	}

 	
	// this is the booking payment feature
	@PutMapping("/payment/{bookingId}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> payment(@PathVariable int bookingId){
		return bookingService.payment(bookingId);
	}

	// this is home booking feature
	@PostMapping
	public ResponseEntity<?> receiveBooking (@RequestParam int houseId, @RequestParam String dateStart, @RequestParam String dateStop){
		return bookingService.receiveBooking(houseId, dateStart, dateStop);
	}

}
