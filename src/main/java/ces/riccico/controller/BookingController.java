package ces.riccico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<?> receiveBooking (@RequestParam String idHouse, @RequestParam String dateStart, @RequestParam String dateStop){
		return bookingService.receiveBooking(idHouse, dateStart, dateStop);
	}
	
	@PutMapping("/payment/{idBooking}")
	public ResponseEntity<?> payment(@PathVariable int idBooking){
		return bookingService.payment(idBooking);
	}
}
