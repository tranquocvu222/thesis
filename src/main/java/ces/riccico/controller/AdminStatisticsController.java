
package ces.riccico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.service.AdminService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@CrossOrigin
public class AdminStatisticsController {
	
	@Autowired
	private AdminService adminService;
	
	@GetMapping("/statisticsAdmin")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	public ResponseEntity<?> findRatingByHouseId() {
		return adminService.statisticsAdmin();
	}
	
	// this is list home booking paid
	@GetMapping("/listBookingsPaid")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	public ResponseEntity<?> findByBookingPaid(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return adminService.findByBookingPaid(page, size);
	}
	
	// this is revenue monthly
	@GetMapping("/revenue/{year}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	public ResponseEntity<?> monthlyRevenue(@PathVariable int year) {
		return adminService.monthlyRevenue(year);
	}

}
