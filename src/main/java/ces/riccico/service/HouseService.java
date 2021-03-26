package ces.riccico.service;

import java.util.List;
import org.springframework.http.ResponseEntity;

import ces.riccico.entities.House;
import ces.riccico.models.HouseDetailModel;

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

	ResponseEntity<?> searchFilter(String country, String province, Double sizeBelow, Double sizeAbove,
			Double priceBelow, Double priceAbove, boolean tivi, boolean wifi, boolean air_conditioner, boolean fridge,
			boolean swim_pool, byte guestAbove, byte guestBelow, int page, int size);

	ResponseEntity<?> updateHouse(int houseId, HouseDetailModel houseDetail);

}
