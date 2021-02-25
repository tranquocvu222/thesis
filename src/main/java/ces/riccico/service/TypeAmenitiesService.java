package ces.riccico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import ces.riccico.models.TypeAmenities;

public interface TypeAmenitiesService {

	ResponseEntity<?> deleteTypeAmenities(Integer idAmenities);

	ResponseEntity<?> createTypeAmenities(TypeAmenities typeAmenities);

	List<TypeAmenities> getAll();

	ResponseEntity<?> updateTypeAmenities(TypeAmenities typeA, Integer idTypeamenities);



}
