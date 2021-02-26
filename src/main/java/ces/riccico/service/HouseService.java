package ces.riccico.service;


import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;

import ces.riccico.models.Amenities;
import ces.riccico.models.House;
import ces.riccico.models.TypeAmenities;
import ces.riccico.models.TypeFeature;
import ces.riccico.models.TypeRoom;



public interface HouseService {

	List<House> getAllDeleted();

	List<House> getAllNotApproved();

	List<House> getAllApproved();

	List<House> getAll();

	ResponseEntity<?> createAmenities(Integer idHouse, Set<Amenities> setAmenities);

	ResponseEntity<?> createTypeFeature(Integer idHouse, Set<TypeFeature> setTypeFeature);

//	ResponseEntity<?> createTypeRoom(Integer idHouse, Set<TypeRoom> setTypeRoom);

	ResponseEntity<?> findByTitle(String title, int page, int size);
	ResponseEntity<?> findByPageAndSize(int page, int size);
	
	ResponseEntity<?> getHouseDetail(Integer idHouse);
	
	ResponseEntity<?> approveHouse(int idHouse);

	ResponseEntity<?> updateHouse(int idHouse, House house);

	ResponseEntity<?> deleteHouse(int idHouse);

	ResponseEntity<?> postNewHouse(House house);

	ResponseEntity<?> findHouseByUsername(String username);


}
