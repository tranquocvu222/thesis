package ces.riccico.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.models.Accounts;
import ces.riccico.models.TypeFeature;
import ces.riccico.models.Users;
import ces.riccico.notification.AuthNotification;
import ces.riccico.notification.HouseNotification;
import ces.riccico.notification.UserNotification;
import ces.riccico.service.TypeFeatureService;

@CrossOrigin
@RestController
public class TypeFeatureController {

	@Autowired
	TypeFeatureService typeFeatureService;

//	Show list TypeFeature
	@RequestMapping(value = "/typefeature", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<TypeFeature> getAll() {
		try {
			List<TypeFeature> listTypeFeature = new ArrayList<TypeFeature>();
			listTypeFeature = typeFeatureService.findAll();
			return listTypeFeature;
		} catch (Exception e) {
			System.out.println("getAll: " + e);
			return null;
		}
	}
	

//	Add TypeFeature
	@RequestMapping(value = "/createTypeFeature", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> createTypeFeature(@RequestBody TypeFeature typeFeature) {
		try {
			if (typeFeature.getFeaturename() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HouseNotification.FeatureNull);
			}
			typeFeatureService.save(typeFeature);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthNotification.fail);
		}
		return ResponseEntity.ok(AuthNotification.success);
	}
	
//	Update TypeFeature
	@RequestMapping(value = "/updateTypeFeature", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> updateTypeFeature(@RequestBody TypeFeature typeFeature) {
		Optional<TypeFeature> feature = typeFeatureService.findById(typeFeature.getIdFeature());
		try {
			if (feature.isPresent()) {
				feature.get().setFeaturename(typeFeature.getFeaturename());
				typeFeatureService.save(feature.get());
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthNotification.fail);
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthNotification.success);
	}
	
//	Delete TypeFeature
	@RequestMapping(value = "/deleteTypeFeature", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> deleteTypeFeature(@RequestBody TypeFeature typeFeature) {
		Optional<TypeFeature> feature = typeFeatureService.findById(typeFeature.getIdFeature());
		try {
			if (feature.isPresent()) {
				typeFeatureService.deleteById(feature.get().getIdFeature());
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthNotification.fail);
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthNotification.success);
	}
}
