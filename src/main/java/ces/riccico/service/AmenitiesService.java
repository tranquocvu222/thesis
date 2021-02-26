package ces.riccico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import ces.riccico.models.Amenities;

public interface AmenitiesService {

	ResponseEntity<?> deleteAmenities(Integer idAmenities);

	ResponseEntity<?> createAmenities(Amenities amenities, Integer idTypeAmenities);

	List<Amenities> getAll();

}
