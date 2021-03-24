package ces.riccico.service;

import java.util.List;
import org.springframework.http.ResponseEntity;

import ces.riccico.entities.House;
import ces.riccico.models.HouseDetailModel;

public interface HouseService {

	ResponseEntity<?> approveHouse(int idHouse);

	ResponseEntity<?> deleteHouse(int idHouse);

	ResponseEntity<?> findByPageAndSize(int page, int size);

	ResponseEntity<?> findByTitle(String title, int page, int size);

	ResponseEntity<?> findHouseByUsername(String username);

	List<House> getAll();
	
	List<House> getAllApproved();

	List<House> getAllDeleted();

	List<House> getAllUnApproved();

	ResponseEntity<?> getHouseDetail(Integer idHouse);

	ResponseEntity<?> postNewHouse(HouseDetailModel houseDetail);

	ResponseEntity<?> searchFilter(String country, String city, Double lowestSize, Double highestSize,
			Double lowestPrice, Double highestPrice, boolean tivi, boolean wifi, boolean air_conditioner, boolean fridge,
			boolean swim_pool, byte lowestGuest, byte highestGuest, int page, int size);

	ResponseEntity<?> updateHouse(int idHouse, HouseDetailModel houseDetail);

}
