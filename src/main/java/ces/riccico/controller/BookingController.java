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

@RestController
@RequestMapping("/bookings")
@CrossOrigin
public class BookingController {
	
	@Autowired
	private BookingService bookingService;
	
	@PostMapping
	public ResponseEntity<?> receiveBooking (@RequestParam int idHouse, @RequestParam String dateStart, @RequestParam String dateStop){
		return bookingService.receiveBooking(idHouse, dateStart, dateStop);
	}
	
	@PutMapping("/payment/{idBooking}")
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> payment(@PathVariable int idBooking){
		return bookingService.payment(idBooking);
	}
	
	@PutMapping("/acceptBooking/{idBooking}")
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> acceptBooking(@PathVariable int idBooking){
		return bookingService.acceptBooking(idBooking);
	}
	
	@PutMapping("/cancelBooking/{idBooking}")
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> cancelBooking(@PathVariable int idBooking){
		return bookingService.cancelBooking(idBooking);
	}
	
	@PutMapping("/completeBooking/{idBooking}")
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> completeBooking(@PathVariable int idBooking){
		return bookingService.completeBooking̣̣̣(idBooking);
	}
}