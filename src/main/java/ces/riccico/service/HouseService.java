package ces.riccico.service;

import java.util.List;
import org.springframework.http.ResponseEntity;

import ces.riccico.entity.House;
import ces.riccico.model.HouseDetailModel;

public interface HouseService {

	ResponseEntity<?> approveHouse(int houseId);

	ResponseEntity<?> deleteHouse(int houseId);

	ResponseEntity<?> findByPageAndSize(int page, int size);

	ResponseEntity<?> findByTitle(String title, int page, int size);

	ResponseEntity<?> findHouseByUsername(String username);

	List<House> getAll();
	
	List<House> getAllApproved();

	List<House> getAllDeleted();

	List<House> getAllUnApproved();

	ResponseEntity<?> getHouseDetail(Integer houseId);

	ResponseEntity<?> postNewHouse(HouseDetailModel houseDetail);

	ResponseEntity<?> searchFilter(String country, String city, Double lowestSize, Double highestSize,
			Double lowestPrice, Double highestPrice, boolean tivi, boolean wifi, boolean airConditioner, boolean fridge,
			boolean swimPool, byte lowestGuest, byte highestGuest, int page, int size);

	ResponseEntity<?> updateHouse(int houseId, HouseDetailModel houseDetail);

}
