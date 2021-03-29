package ces.riccico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.entity.Rating;
import ces.riccico.service.RatingService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/ratings")
@CrossOrigin
public class RatingController {

	@Autowired
	private RatingService ratingService;

	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@GetMapping("/account/{accountId}")
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> findByRatingAccountId(@PathVariable int accountId) {
		return ratingService.findByRatingAccountId(accountId);
	}

	@GetMapping("/houses/{houseId}")
	public ResponseEntity<?> findRatingByHouseId(@PathVariable int houseId) {
		return ratingService.findRatingByHouseId(houseId);
	}

	@GetMapping("/detail/{ratingId}")
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> getRatingDetail(@PathVariable int ratingId) {
		return ratingService.getRatingDetail(ratingId);
	}

	@PostMapping("/write/{bookingId}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> writeRating(@PathVariable int bookingId, @RequestBody Rating rating) {
		return ratingService.writeRating(bookingId, rating);
	}

	@PutMapping("/{ratingId}")
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> updateRating(@PathVariable int ratingId, @RequestBody Rating rating) {
		return ratingService.updateRating(ratingId, rating);
	}
}
