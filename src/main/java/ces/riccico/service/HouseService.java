package ces.riccico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import ces.riccico.models.House;

public interface HouseService {
	
	List<House> getAll();
	
	List<House> getAllApproved();
	
	List<House> getAllNotApproved();
	
	ResponseEntity<?> findHouseByUsername(String username);
	
	ResponseEntity<?> postNewHouse(House house);
	
	ResponseEntity<?> deleteHouse(String idHouse);
	
	ResponseEntity<?> updateHouse (String idHouse, House house);
	
	ResponseEntity<?> approveHouse(String idHouse);
}
