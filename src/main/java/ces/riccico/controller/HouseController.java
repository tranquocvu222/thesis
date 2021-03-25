package ces.riccico.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import ces.riccico.entities.House;
import ces.riccico.models.HouseDetailModel;
import ces.riccico.service.HouseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/houses")
@CrossOrigin
public class HouseController {
	
	@Autowired
	private HouseService houseService;
	
	// confirm post to home page from user
	@PutMapping("/approve/{idHouse}")
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> approveHouse(@PathVariable int idHouse) {
		return houseService.approveHouse(idHouse);
	}
	
	// delete house
	@DeleteMapping("/{idHouse}")
	@PreAuthorize("hasAnyAuthority('user','admin')")
	public ResponseEntity<?> deleteHouse(@PathVariable int idHouse) {
		return houseService.deleteHouse(idHouse);
	}

	// find house with pagination
	@GetMapping
	public ResponseEntity<?> findByPageAndSize(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return houseService.findByPageAndSize(page, size);
	}
	
	// search by tile of post
	@GetMapping("/searchTitle")
	public ResponseEntity<?> findByTitle(@RequestParam(required = false) String title,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		return houseService.findByTitle(title, page, size);
	}

	// this is the all-post feature
	@GetMapping("/getAll")
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<House> getAll() {
		return houseService.getAll();
	}
	
	// shows approved houses list
	@GetMapping("/isApproved")
	public List<House> getAllApproved() {
		return houseService.getAllApproved();
	}
	
	//shows unapproved home lists
	@GetMapping("/notApproved")
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<House> getAllUnApproved() {
		return houseService.getAllUnApproved();
	}
	
	//find house by username of host 
	@GetMapping("/username/{username}")
	public ResponseEntity<?> getHouseByUsername(@PathVariable String username) {
		return houseService.findHouseByUsername(username);
	}

	// show list house has been deleted
	@GetMapping("/isDeleted")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<House> getHouseDelete() {
		return houseService.getAllDeleted();
	}
	
	// see house's detail,  service of room and view room
	@GetMapping("/detail")
	public ResponseEntity<?> getHouseDetail(@RequestParam Integer idHouse) {
		return houseService.getHouseDetail(idHouse);
	}
	
	// post your house on the website
	@PostMapping("/create")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "jwtToken") })
	@PreAuthorize("hasAnyAuthority('user','admin')")
	public ResponseEntity<?> postNewHouse(@RequestBody HouseDetailModel houseDetail) {
		return houseService.postNewHouse(houseDetail);
	}
	

	// this is the filter search feature
	@GetMapping("/filter")
	public ResponseEntity<?> searchFilter(@RequestParam(defaultValue = "") String country,
			@RequestParam(defaultValue = "") String city, @RequestParam(defaultValue = "0") Double lowestSize,
			@RequestParam(defaultValue = "50000") Double highestSize, @RequestParam(defaultValue = "0") Double lowestPrice,
			@RequestParam(defaultValue = "50000000") Double highestPrice,
			@RequestParam(defaultValue = "false") boolean tivi, @RequestParam(defaultValue = "false") boolean wifi,
			@RequestParam(defaultValue = "false") boolean air_conditioner,
			@RequestParam(defaultValue = "false") boolean fridge,
			@RequestParam(defaultValue = "false") boolean swim_pool, @RequestParam(defaultValue = "0") byte lowestGuest,
			@RequestParam(defaultValue = "100") byte highestGuest, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return houseService.searchFilter(country, city, lowestSize, highestSize, lowestPrice, highestPrice, tivi, wifi,
				air_conditioner, fridge, swim_pool,  lowestGuest, highestGuest, page, size);
	}

	// this is the house update feature
	@PutMapping("/{idHouse}")
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> updateHouse(@PathVariable int idHouse, @RequestBody(required = false) HouseDetailModel houseDetail) {
		return houseService.updateHouse(idHouse, houseDetail);
	}

}
