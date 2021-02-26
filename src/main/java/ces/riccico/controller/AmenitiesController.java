package ces.riccico.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.models.Amenities;
import ces.riccico.models.TypeFeature;
import ces.riccico.service.AmenitiesService;

@CrossOrigin
@RestController
public class AmenitiesController {

	@Autowired
	AmenitiesService amenitiesService;
	
//	Show list Amenities
	@RequestMapping(value = "/amenities", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<Amenities> getAll() {
		return amenitiesService.getAll();
	}

//	Add Amenities
	@RequestMapping(value = "/createAmenities/{idTypeAmenities}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> createAmenities(@RequestBody Amenities amenities, @PathVariable Integer idTypeAmenities) {
		return amenitiesService.createAmenities(amenities, idTypeAmenities);
	}

//	Delete Amenities
	@RequestMapping(value = "/deleteAmenities/{idAmenities}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> deleteAmenities(@PathVariable Integer idAmenities) {
		return amenitiesService.deleteAmenities(idAmenities);

	}
}
