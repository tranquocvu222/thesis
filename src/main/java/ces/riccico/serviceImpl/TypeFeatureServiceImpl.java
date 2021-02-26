

package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ces.riccico.models.TypeFeature;
import ces.riccico.models.TypeRoom;
import ces.riccico.notification.Notification;
import ces.riccico.notification.FeatureNotification;
import ces.riccico.repository.TypeFeatureReponsitory;
import ces.riccico.repository.TypeRoomRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.TypeFeatureService;

@Service
public class TypeFeatureServiceImpl implements TypeFeatureService {

	@Autowired
	TypeFeatureReponsitory typeFeatureRepository;

	@Autowired
	TypeRoomRepository typeRoomRepository;
	
	@Autowired
	SecurityAuditorAware securityAuditorAware;

//	Show list TypeFeature
	@Override
	public List<TypeFeature> getAll() {
		try {
			List<TypeFeature> listTypeFeature = new ArrayList<TypeFeature>();
			listTypeFeature = typeFeatureRepository.findAll();
			return listTypeFeature;
		} catch (Exception e) {
			System.out.println("getAll: " + e);
			return null;
		}
	}

//	Add TypeFeature
	@Override
	public ResponseEntity<?> createTypeFeature(TypeFeature typeFeature, Integer idTyperoom) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		TypeRoom typeRoom = typeRoomRepository.findById(idTyperoom).get();
		try {
			if (idCurrent == null || idCurrent.isEmpty()) {
				ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.loginRequired));
			}else if (typeRoom == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of(Notification.message, FeatureNotification.roomNotExist));
			} else if (typeFeature.getFeatureName() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message, FeatureNotification.FeatureNull));
			} else {
				typeFeature.setTypeRoom(typeRoom);
				typeFeatureRepository.saveAndFlush(typeFeature);
			}
			return ResponseEntity.ok(Map.of(Notification.message,Notification.success));
		} catch (Exception e) {
			System.out.println(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
		}
		
	}

//	Update TypeFeature

//	public ResponseEntity<?> updateTypeFeature(Integer idTypefeature ) {
//		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
//		TypeFeature feature = typeFeatureRepository.findById(idTypefeature).get();
//		try {
//			if (idCurrent == null || idCurrent.isEmpty()) {
//				 return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//						 .body(Map.of(Notification.message,Notification.loginRequired));
//			}else if (feature == null) {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND)
//						 .body(Map.of(Notification.message,FeatureNotification.featureNotExist));
//			}else {
//				feature.setFeatureName(feature.getFeatureName());
//				typeFeatureRepository.saveAndFlush(feature);
//				return ResponseEntity.ok(Notification.success);
//			}
//
//		} catch (Exception e) {
//			System.out.println(e);
//		}
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Notification.fail);
//	}

//	Delete TypeFeature
	@Override
	public ResponseEntity<?> deleteTypeFeature(Integer idTypefeature) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		TypeFeature feature = typeFeatureRepository.findById(idTypefeature).get();
		try {
			if (idCurrent == null || idCurrent.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(Notification.message,Notification.loginRequired));
			}else if (feature == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of(Notification.message,FeatureNotification.featureNotExist));
			}else {
				typeFeatureRepository.deleteById(idTypefeature);
				return ResponseEntity.ok(Map.of(Notification.message,Notification.success));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of(Notification.message,Notification.fail));
		}
		
	}


}
