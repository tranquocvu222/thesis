package ces.riccico.controller;

import java.util.List;

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

import ces.riccico.models.House;
import ces.riccico.service.HouseService;

@RestController
@RequestMapping("/houses")
@CrossOrigin
public class HouseController {
	@Autowired
	private HouseService houseService;
	
	@GetMapping
//	@PreAuthorize("hasAnyAuthority('admin')")
	public List<House> getAll(){
		return houseService.getAll();
	}
	
	@GetMapping("/isApproved")
	public List<House> getAllApproved(){
		return houseService.getAllApproved();
	}
	
	@GetMapping("/notApproved")
//	@PreAuthorize("hasAnyAuthority('admin')")
	public List<House> getAllNotApproved(){
		return houseService.getAllNotApproved();
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<?> getHouseByUsername(@PathVariable String username){
		return houseService.findHouseByUsername(username);
	}
	
	@PostMapping("/create")
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> postNewHouse(@RequestBody House house){
		return houseService.postNewHouse(house);
	}
	
	@PutMapping("/{idHouse}")
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> updateHouse(@PathVariable String idHouse, @RequestBody House house){
		return houseService.updateHouse(idHouse, house);
	}
	
	@DeleteMapping("/{idHouse}")
//	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> deleteHouse(@PathVariable String idHouse){
		return houseService.deleteHouse(idHouse);
	}
	
	@PutMapping("/approve/{idHouse}")
//	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> approveHouse(@PathVariable String idHouse){
		return houseService.approveHouse(idHouse);
	}
	
}
