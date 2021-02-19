package ces.riccico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.service.RatingService;

@RestController
@RequestMapping("/ratings")
@CrossOrigin
public class RatingController {
	
	@Autowired
	private RatingService ratingService;
//	
//	@PostMapping("/write/{idBooking}")
//	public ResponseEntity<?> 
}
