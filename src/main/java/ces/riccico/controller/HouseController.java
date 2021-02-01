package ces.riccico.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<House> getAll(){
		return houseService.getAll();
	}
	
	@GetMapping("isApproved")
	public List<House> getAllApproved(){
		return houseService.getAllApproved();
	}
	
	@GetMapping("notApproved")
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<House> getAllNotApproved(){
		return houseService.getAllNotApproved();
	}
	
	@GetMapping("/username")
	public ResponseEntity<?> getHouseByUsername(@RequestParam String username){
		return houseService.findHouseByUsername(username);
	}
	
	@PostMapping("/create")
	@PreAuthorize("hasAnyAuthority('user')")
	public ResponseEntity<?> postNewHouse(@RequestBody House house){
		return houseService.postNewHouse(house);
	}
	
}
