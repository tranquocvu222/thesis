package ces.riccico.service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import ces.riccico.model.HouseDetailModel;

public interface HouseService {

//	ResponseEntity<?> approveHouse(int houseId);

	ResponseEntity<?> deactiveHouse(int houseId);

	ResponseEntity<?> blockHouse(int houseId);

	ResponseEntity<?> findByPageAndSize(int page, int size);

	ResponseEntity<?> findHouseByUsername(String username);

	ResponseEntity<?> getHouseDetail(Integer houseId);
	
	ResponseEntity<?> getHouseForHost(int accountId, String status, int page, int size);

	ResponseEntity<?> getHouseRecommendForUser(int houseId) throws IOException;

	ResponseEntity<?> postNewHouse(HouseDetailModel houseDetail);

//	ResponseEntity<?> searchFilter(String country, String city, Double lowestSize, Double highestSize,
//			Double lowestPrice, Double highestPrice, boolean tivi, boolean wifi, boolean airConditioner, boolean fridge,
//			boolean swimPool, byte lowestGuest, byte highestGuest, int page, int size);

	ResponseEntity<?> updateHouse(int houseId, HouseDetailModel houseDetail);

	ResponseEntity<?> unlistedHouse(int houseId);

	ResponseEntity<?> searchFilter(String country, String city, Double lowestSize, Double highestSize, Double lowestPrice, Double highestPrice, boolean tivi,
			boolean wifi, boolean airConditioner, boolean fridge, boolean swimPool, byte lowestGuest, byte highestGuest, int page, int size);

}
