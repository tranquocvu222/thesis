package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.Amenities;
import ces.riccico.models.TypeAmenities;
import ces.riccico.models.TypeFeature;
import ces.riccico.models.TypeRoom;
import ces.riccico.notification.FeatureNotification;
import ces.riccico.notification.Notification;
import ces.riccico.repository.AmenitiesRepository;
import ces.riccico.repository.TypeAmenitiesRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.AmenitiesService;

@Service
public class AmenitiesServiceImpl implements AmenitiesService {

	@Autowired
	AmenitiesRepository amenitiesRepository;
	
	@Autowired
	SecurityAuditorAware securityAuditorAware;
	
	@Autowired
	TypeAmenitiesRepository typeAmenitiesRepository;
	
//	Show list Amenities
	@Override
	public List<Amenities> getAll() {
		try {
			List<Amenities> listAmenities = new ArrayList<Amenities>();
			listAmenities = amenitiesRepository.findAll();
			return listAmenities;
		} catch (Exception e) {
			System.out.println("getAll: " + e);
			return null;
		}
	}
	
//	Add Amenities
	@Override
	public ResponseEntity<?> createAmenities(Amenities amenities, Integer idTypeAmenities) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		TypeAmenities typeAmenities = typeAmenitiesRepository.findById(idTypeAmenities).get();
		try {
			if (idCurrent == null || idCurrent.isEmpty()) {
				ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.loginRequired));
			}else if (typeAmenities == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of(Notification.message, FeatureNotification.amenitiesNotExist));
			} else if (amenities.getAmenitiesName() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, FeatureNotification.AmenitiesNull));
			} else {
				amenities.setTypeAmenities(typeAmenities);
				amenitiesRepository.saveAndFlush(amenities);
			}
			return ResponseEntity.ok(Map.of(Notification.message,Notification.success));
		} catch (Exception e) {
			System.out.println(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
		}
		
	}
	
//	Delete Amenities
	@Override
	public ResponseEntity<?> deleteAmenities(Integer idAmenities) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Amenities amenities = amenitiesRepository.findById(idAmenities).get();
		try {
			if (idCurrent == null || idCurrent.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message,Notification.loginRequired));
			}else if (amenities == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of(Notification.message,FeatureNotification.amenitiesNotExist));
			}else {
				amenitiesRepository.deleteById(idAmenities);
				return ResponseEntity.ok(Map.of(Notification.message,Notification.success));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of(Notification.message,Notification.fail));
		}
		
	}
	
}
