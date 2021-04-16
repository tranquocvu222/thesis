package ces.riccico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.model.DateModel;
import ces.riccico.service.BookingService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;


@RestController
@RequestMapping("/bookings")
@CrossOrigin
@EnableAsync
public class BookingController {

	@Autowired
	private BookingService bookingService;

	// this is the cancellation feature
	@GetMapping("/cancelBooking/{bookingId}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	public ResponseEntity<?> cancelBooking(@PathVariable int bookingId) {
		return bookingService.cancelBooking(bookingId);
	}

	// this is the feature to confirm the booking has been completed
	@GetMapping("/completeBooking/{bookingId}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	public ResponseEntity<?> completeBooking(@PathVariable int bookingId) {
		return bookingService.completeBooking̣̣̣(bookingId);
	}
	//auto incompleted booking 
	@Async
	@Scheduled(fixedDelay = 20000)
	@PutMapping("/incompleted")
	public ResponseEntity<?> incompleteBooking() {
		return bookingService.incompleteBooking();
	}

	// get list booking of house
	@GetMapping("/house/{houseId}")
	public ResponseEntity<?> getBookingByHouseId(@PathVariable int houseId) {
		return bookingService.findByHouseId(houseId);
	}

	// get booking detail
	@GetMapping("/detail/{bookingId}")
	public ResponseEntity<?> getBookingDetail(@PathVariable int bookingId) {
		return bookingService.getBookingDetail(bookingId);
	}
	
	//this is get booking for customer by status
	@GetMapping("/customer")
	public ResponseEntity<?> getBookingForCustomer(@RequestParam Integer accountId, @RequestParam(defaultValue = "") String status,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		return bookingService.getBookingForCustomer(accountId, status, page, size);
	}
	
	//this is get booking for host by status
	@GetMapping("/host")
	public ResponseEntity<?> getBookingForHost(@RequestParam Integer accountId, @RequestParam(defaultValue = "") String status,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		return bookingService.getBookingForHost(accountId, status, page, size);
	}

	// this is the booking payment feature
	@PutMapping("/payment/{bookingId}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	public ResponseEntity<?> payment(@PathVariable int bookingId) {
		return bookingService.payment(bookingId);
	}

	// this is home booking feature
	@PostMapping("/{houseId}")
	public ResponseEntity<?> receiveBooking(@PathVariable int houseId, @RequestBody DateModel dateModel) {
		return bookingService.receiveBooking(houseId, dateModel);
	}

}
