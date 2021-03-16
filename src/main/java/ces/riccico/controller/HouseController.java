
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
import ces.riccico.service.HouseService;

@RestController
@RequestMapping("/houses")
@CrossOrigin
public class HouseController {
	@Autowired
	private HouseService houseService;

	@GetMapping("/getAll")
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<House> getAll() {
		return houseService.getAll();
	}

	@GetMapping("/detail")
	public ResponseEntity<?> getHouseDetail(@RequestParam Integer idHouse) {
		return houseService.getHouseDetail(idHouse);
	}

	@GetMapping("/isApproved")
	public List<House> getAllApproved() {
		return houseService.getAllApproved();
	}

	@GetMapping("/notApproved")
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<House> getAllNotApproved() {
		return houseService.getAllNotApproved();
	}

	@GetMapping("/isDeleted")
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<House> getHouseDelete() {
		return houseService.getAllDeleted();
	}

	@GetMapping("/username/{username}")
	public ResponseEntity<?> getHouseByUsername(@PathVariable String username) {
		return houseService.findHouseByUsername(username);
	}

	@PostMapping("/create")
	@PreAuthorize("hasAnyAuthority('user','admin')")
	public ResponseEntity<?> postNewHouse(@RequestBody House house) {
		return houseService.postNewHouse(house);
	}

	@PutMapping("/{idHouse}")
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> updateHouse(@PathVariable int idHouse, @RequestBody House house) {
		return houseService.updateHouse(idHouse, house);
	}

	@DeleteMapping("/{idHouse}")
	@PreAuthorize("hasAnyAuthority('user','admin')")
	public ResponseEntity<?> deleteHouse(@PathVariable int idHouse) {
		return houseService.deleteHouse(idHouse);
	}

	@PutMapping("/approve/{idHouse}")
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> approveHouse(@PathVariable int idHouse) {
		return houseService.approveHouse(idHouse);
	}

	@GetMapping("/searchTitle")
	public ResponseEntity<?> findByTitle(@RequestParam(required = false) String title,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		return houseService.findByTitle(title, page, size);
	}

	@GetMapping
	public ResponseEntity<?> findByPageAndSize(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return houseService.findByPageAndSize(page, size);
	}

//	@GetMapping("/search")
//	public ResponseEntity<?> searchByFilter(@RequestParam(required = false) String country,
//			@RequestParam(required = false) String province, @RequestParam(required = false) Double size,
//			@RequestParam(required = false) Double priceBelow, @RequestParam(required = false) Double priceAbove,
//			@RequestParam(required = false) byte bedroom, @RequestParam(required = false) byte maxGuest,
//			@RequestParam(required = false) boolean tivi, @RequestParam(required = false) boolean wifi,
//			@RequestParam(required = false) boolean air_conditioner, @RequestParam(required = false) boolean fridge,
//			@RequestParam(required = false) boolean swimPool, @RequestParam(defaultValue = "0") int page,
//			@RequestParam(defaultValue = "20") int sizePage) {
//		return houseService.searchByFilter(country, province, size, priceBelow, priceAbove, bedroom, maxGuest, tivi,
//				wifi, air_conditioner, fridge, swimPool, page, sizePage);
//	}

}
