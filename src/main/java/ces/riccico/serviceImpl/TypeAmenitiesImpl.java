package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.TypeAmenities;
import ces.riccico.notification.FeatureNotification;
import ces.riccico.notification.Notification;
import ces.riccico.repository.TypeAmenitiesRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.TypeAmenitiesService;

@Service
public class TypeAmenitiesImpl implements TypeAmenitiesService {
	
	@Autowired
	TypeAmenitiesRepository typeAmenitiesRepository;
	
	@Autowired
	SecurityAuditorAware securityAuditorAware;
	
//	Show list TypeFeature
	@Override
	public List<TypeAmenities> getAll() {
		try {
			List<TypeAmenities> listTypeAmenities = new ArrayList<TypeAmenities>();
			listTypeAmenities = typeAmenitiesRepository.findAll();
			return listTypeAmenities;
		} catch (Exception e) {
			System.out.println("getAll: " + e);
			return null;
		}
	}

//	Add TypeFeature
	@Override
	public ResponseEntity<?> createTypeAmenities(TypeAmenities typeAmenities) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		try {
			if (idCurrent == null || idCurrent.isEmpty()) {
				ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.loginRequired));
			} else if (typeAmenities.getAmenitiesName() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, FeatureNotification.AmenitiesNull));
			} else {
			
				typeAmenitiesRepository.saveAndFlush(typeAmenities);
			}
			return ResponseEntity.ok(Map.of(Notification.message,Notification.success));
		} catch (Exception e) {
			System.out.println(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
		}
		
	}
	
//	Update TypeAmenities
	
	@Override
	public ResponseEntity<?> updateTypeAmenities(TypeAmenities typeAmenities, Integer idTypeamenities ) {

		try {
			String idCurrent = securityAuditorAware.getCurrentAuditor().get();
			TypeAmenities amenities = typeAmenitiesRepository.findById(idTypeamenities).get();
			if (idCurrent == null || idCurrent.isEmpty()) {
				 return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						 .body(Map.of(Notification.message,Notification.loginRequired));
			}
			if (amenities == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						 .body(Map.of(Notification.message,FeatureNotification.amenitiesNotExist));
			}else {
				amenities.setAmenitiesName(typeAmenities.getAmenitiesName());
				
				typeAmenitiesRepository.saveAndFlush(amenities);
				System.out.println("-------" + amenities.getAmenitiesName());
			}
			return ResponseEntity.ok(Map.of(Notification.message,Notification.success));

		} catch (Exception e) {
			System.out.println(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
		}
		
	}
	
//	Delete TypeFeature
	@Override
	public ResponseEntity<?> deleteTypeAmenities(Integer idAmenities) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		TypeAmenities typeAmenities = typeAmenitiesRepository.findById(idAmenities).get();
		try {
			if (idCurrent == null || idCurrent.isEmpty()) {
				ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.loginRequired));
			} else if (typeAmenities == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of(Notification.message,FeatureNotification.amenitiesNotExist));
			}else {
				typeAmenitiesRepository.deleteById(idAmenities);
			}
			return ResponseEntity.ok(Map.of(Notification.message,Notification.success));
		} catch (Exception e) {
			System.out.println(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
		}
	}

}
