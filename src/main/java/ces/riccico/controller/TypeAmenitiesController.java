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

import ces.riccico.models.TypeAmenities;
import ces.riccico.service.TypeAmenitiesService;

@CrossOrigin
@RestController
public class TypeAmenitiesController {

	@Autowired
	TypeAmenitiesService typeAmenitiesService;

//	Show list TypeAmenities
	@RequestMapping(value = "/typeamenities", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<TypeAmenities> getAll() {
		return typeAmenitiesService.getAll();
	}

//	Add TypeAmenities
	@RequestMapping(value = "/createTypeAmenities", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> createTypeAmennities(@RequestBody TypeAmenities typeAmenities) {
		return typeAmenitiesService.createTypeAmenities(typeAmenities);
	}
	
//	Update TypeAmenities
	@RequestMapping(value = "/updateTypeAmenities/{idTypeamenities}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> updateTypeAmennities(@RequestBody TypeAmenities typeAmenities,@PathVariable Integer idTypeamenities) {
		return typeAmenitiesService.updateTypeAmenities(typeAmenities,idTypeamenities);

	}

//	Delete TypeAmenities
	@RequestMapping(value = "/deleteTypeAmenities/{idTypeamenities}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> deleteTypeAmennities(@PathVariable Integer idTypeamenities) {
		return typeAmenitiesService.deleteTypeAmenities(idTypeamenities);

	}

}
