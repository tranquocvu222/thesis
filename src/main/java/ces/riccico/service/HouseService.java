package ces.riccico.service;

import java.util.List;
import org.springframework.http.ResponseEntity;

import ces.riccico.entities.House;

public interface HouseService {

	List<House> getAllDeleted();

	List<House> getAllNotApproved();

	List<House> getAllApproved();

	List<House> getAll();

	ResponseEntity<?> findByTitle(String title, int page, int size);

	ResponseEntity<?> findByPageAndSize(int page, int size);

	ResponseEntity<?> getHouseDetail(Integer idHouse);

	ResponseEntity<?> approveHouse(int idHouse);

	ResponseEntity<?> updateHouse(int idHouse, House house);

	ResponseEntity<?> deleteHouse(int idHouse);

	ResponseEntity<?> postNewHouse(House house);

	ResponseEntity<?> findHouseByUsername(String username);

}
