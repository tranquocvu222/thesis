
package ces.riccico.controller;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.model.HouseDetailModel;
import ces.riccico.service.HouseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/houses")
@CrossOrigin
public class HouseController {

	@Autowired
	private HouseService houseService;

	// block
	@GetMapping("/block/{houseId}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	public ResponseEntity<?> blockHouse(@PathVariable int houseId) {
		return houseService.blockHouse(houseId);
	}

	// delete house
	@DeleteMapping("/deactiveHouse/{houseId}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	public ResponseEntity<?> deactiveHouse(@PathVariable int houseId) {
		return houseService.deactiveHouse(houseId);
	}

	// unlisted house
	@GetMapping("/unlistedHouse/{houseId}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	public ResponseEntity<?> unlistedHouse(@PathVariable int houseId) {
		return houseService.unlistedHouse(houseId);
	}

	// find house with pagination
	@GetMapping("/listHouse")
	public ResponseEntity<?> findByPageAndSize(@RequestParam(required = false) String page,
			@RequestParam(required = false) String size) {
		return houseService.findByPageAndSize(page, size);
	}

//	// search by tile of post
//	@GetMapping("/searchTitle")
//	public ResponseEntity<?> findByTitle(@RequestParam(required = false) String title,
//			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
//		return houseService.findByTitle(title, page, size);
//	}

	// find house by username of host
	@GetMapping("/username/{username}")
	public ResponseEntity<?> getHouseByUsername(@PathVariable String username) {
		return houseService.findHouseByUsername(username);
	}

	// see house's detail, service of room and view room
	@GetMapping("/detail")
	public ResponseEntity<?> getHouseDetail(@RequestParam Integer houseId) {
		return houseService.getHouseDetail(houseId);
	}

	// get House For Host
	@GetMapping("/host")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	public ResponseEntity<?> getHouseForHost(@RequestParam(defaultValue = "0") Integer accountId,
			@RequestParam(required = false) String status, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return houseService.getHouseForHost(accountId, status, page, size);
	}

	// get House Recommend For User
	@GetMapping("/userRecs")
	public ResponseEntity<?> getHouseRecommendForUser(@RequestParam Integer houseId) throws IOException {
		return houseService.getHouseRecommendForUser(houseId);
	}

	// post your house on the website
	@PostMapping("/create")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	public ResponseEntity<?> postNewHouse(@RequestBody HouseDetailModel houseDetail) {
		return houseService.postNewHouse(houseDetail);
	}

	// this is the filter search feature
	@GetMapping("/filter")
	public ResponseEntity<?> searchFilter(@RequestParam(defaultValue = "") String country,

			@RequestParam(defaultValue = "") String city, @RequestParam(defaultValue = "0") Double lowestSize,
			@RequestParam(defaultValue = "50000") Double highestSize,
			@RequestParam(defaultValue = "0") Double lowestPrice,
			@RequestParam(defaultValue = "50000000") Double highestPrice,
			@RequestParam(defaultValue = "false") boolean tivi, @RequestParam(defaultValue = "false") boolean wifi,
			@RequestParam(defaultValue = "false") boolean airConditioner,
			@RequestParam(defaultValue = "false") boolean fridge,
			@RequestParam(defaultValue = "false") boolean swimPool, @RequestParam(defaultValue = "0") byte lowestGuest,
			@RequestParam(defaultValue = "100") byte highestGuest, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(required = false, defaultValue = "ASC") String sort) {
		return houseService.searchFilter(country, city, lowestSize, highestSize, lowestPrice, highestPrice, tivi, wifi,
				airConditioner, fridge, swimPool, lowestGuest, highestGuest, page, size, sort);

	}

	// this is the house update feature
	@PutMapping("/{houseId}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	public ResponseEntity<?> updateHouse(@PathVariable int houseId, @RequestBody HouseDetailModel houseDetail) {
		return houseService.updateHouse(houseId, houseDetail);
	}

}
