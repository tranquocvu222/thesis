package ces.riccico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
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
	@PutMapping("/acceptBooking/{idBooking}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> acceptBooking(@PathVariable int idBooking){
		return bookingService.acceptBooking(idBooking);
	}
	
	// this is the cancellation feature
	@PutMapping("/cancelBooking/{idBooking}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> cancelBooking(@PathVariable int idBooking){
		return bookingService.cancelBooking(idBooking);
	}
	
	// this is the feature to confirm the booking has been completed
	@PutMapping("/completeBooking/{idBooking}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> completeBooking(@PathVariable int idBooking){
		return bookingService.completeBooking̣̣̣(idBooking);
	}
	
	// this is the booking payment feature
	@PutMapping("/payment/{idBooking}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> payment(@PathVariable int idBooking){
		return bookingService.payment(idBooking);
	}
	
	// this is home booking feature
	@PostMapping
	public ResponseEntity<?> receiveBooking (@RequestParam int idHouse, @RequestParam String dateStart, @RequestParam String dateStop){
		return bookingService.receiveBooking(idHouse, dateStart, dateStop);
	}
	
}
