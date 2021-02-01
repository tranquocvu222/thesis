package ces.riccico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import ces.riccico.models.House;

public interface HouseService {
	
	List<House> getAll();
	
	List<House> getAllApproved();
	
	List<House> getAllNotApproved();
	
	ResponseEntity<?> findHouseByUsername(String userName);
	
	ResponseEntity<?> postNewHouse(House house);
	
	ResponseEntity<?> deleteHouse(String houseId);
	
	ResponseEntity<?> updateHouse (String idHouse, House house);
	
	ResponseEntity<?> updateStatus(String idHouse);
}
