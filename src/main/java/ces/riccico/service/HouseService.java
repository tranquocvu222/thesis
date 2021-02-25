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

	ResponseEntity<?> findByHouseName(String houseName, int page, int size);

	ResponseEntity<?> approveHouse(Integer idHouse);

	ResponseEntity<?> updateHouse(Integer idHouse, House house);

	ResponseEntity<?> deleteHouse(Integer idHouse);

	ResponseEntity<?> postNewHouse(House house);

	ResponseEntity<?> findHouseByUsername(String username);

	List<House> getAllDeleted();

	List<House> getAllNotApproved();

	List<House> getAllApproved();

	List<House> getAll();

	ResponseEntity<?> createTypeRoom(Integer idHouse, Set<TypeRoom> setTypeRoom);

	ResponseEntity<?> createTypeFeature(Integer idHouse, Set<TypeFeature> setTypeFeature);

	ResponseEntity<?> createAmenities(Integer idHouse, Set<Amenities> setAmenities);

	

	



	

}
