package ces.riccico.service;


import java.util.List;



import org.springframework.http.ResponseEntity;

import ces.riccico.models.House;

public interface HouseService {
	
	List<House> getAll();
	
	List<House> getAllApproved();
	
	List<House> getAllNotApproved();
	
	List<House> getAllDeleted();
	ResponseEntity<?> findHouseByUsername(String username);
	
	ResponseEntity<?> postNewHouse(House house);
	
	ResponseEntity<?> deleteHouse(int idHouse);
	
	ResponseEntity<?> updateHouse (int idHouse, House house);
	
	ResponseEntity<?> approveHouse(int idHouse);
	
	ResponseEntity<?> findByHouseName (String houseName, int page, int size);
}
