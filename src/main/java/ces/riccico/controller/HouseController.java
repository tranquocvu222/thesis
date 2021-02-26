
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

import ces.riccico.models.Amenities;
import ces.riccico.models.House;
import ces.riccico.models.TypeAmenities;
import ces.riccico.models.TypeFeature;
import ces.riccico.models.TypeRoom;
import ces.riccico.service.HouseService;

@RestController
@RequestMapping("/houses")
@CrossOrigin
public class HouseController {
	@Autowired
	private HouseService houseService;

	@GetMapping
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<House> getAll() {
		return houseService.getAll();
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

	@GetMapping("/isdeleted")
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<House> getHouseDelete() {
		return houseService.getAllDeleted();
	}

	@GetMapping("/{username}")
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

	@GetMapping("/search")
	public ResponseEntity<?> findByHouseName(@RequestParam(required = false) String houseName, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size) {
		return houseService.findByHouseName(houseName, page, size);

	}
	
	@PostMapping("/createTyperoom/{idHouse}")
	@PreAuthorize("hasAnyAuthority('user','admin')")
	public ResponseEntity<?> createTypeRoom(@PathVariable Integer idHouse, @RequestBody Set<TypeRoom> typeRoom) {
		return houseService.createTypeRoom(idHouse, typeRoom);
	}
	
	@PostMapping("/createTypeFeature/{idHouse}")
	@PreAuthorize("hasAnyAuthority('user','admin')")
	public ResponseEntity<?> createTypeFeature(@PathVariable Integer idHouse, @RequestBody Set<TypeFeature> typeFeature) {
		return houseService.createTypeFeature(idHouse, typeFeature);
	}
	
	@PostMapping("/createTypeAmenities/{idHouse}")
	@PreAuthorize("hasAnyAuthority('user','admin')")
	public ResponseEntity<?> createTypeAmenities(@PathVariable Integer idHouse, @RequestBody Set<Amenities> setAmenities) {
		return houseService.createAmenities(idHouse, setAmenities);
	}
}
