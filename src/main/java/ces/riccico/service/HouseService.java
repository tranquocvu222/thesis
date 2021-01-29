package ces.riccico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import ces.riccico.models.House;

public interface HouseService {
	
	List<House> getAll();
	
	List<House> getAllApproved();
	
	List<House> getAllNotApproved();
	
	ResponseEntity<?> findByAccountId(String idAccount);
	
	ResponseEntity<?> save(House house);
	
	ResponseEntity<?> delete(String houseId);
	
	ResponseEntity<?> update (String idHouse, House house);
	
	ResponseEntity<?> updateStatus(String idHouse);
}
