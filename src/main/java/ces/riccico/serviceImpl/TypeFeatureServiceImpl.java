package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.Accounts;
import ces.riccico.models.Message;
import ces.riccico.models.TypeFeature;
import ces.riccico.models.TypeRoom;
import ces.riccico.notification.Notification;
import ces.riccico.notification.UserNotification;
import ces.riccico.notification.FeatureNotification;
import ces.riccico.repository.AccountRepository;
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
	private AccountRepository accountRepository;
	
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
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Accounts account = accountRepository.findById(idCurrent).get();
		TypeRoom typeRoom = typeRoomRepository.findById(idTyperoom).get();
		Message message = new Message();
		try {
			if (account == null) {
				message.setMessage(UserNotification.accountNotExist);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			}else if (typeRoom == null) {
				message.setMessage(FeatureNotification.roomNotExist);
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(message);
			} else if (typeFeature.getFeatureName() == null) {
				message.setMessage( FeatureNotification.FeatureNull);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(message);
			} else {
				typeFeature.setTypeRoom(typeRoom);
				typeFeatureRepository.saveAndFlush(typeFeature);
			}
			message.setMessage(Notification.success);
			return ResponseEntity.ok(message);
		} catch (Exception e) {
			System.out.println(e);
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
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
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Accounts account = accountRepository.findById(idCurrent).get();
		TypeFeature feature = typeFeatureRepository.findById(idTypefeature).get();
		Message message = new Message();
		try {
			if (account == null) {
				message.setMessage(UserNotification.accountNotExist);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			}else if (feature == null) {
				message.setMessage(FeatureNotification.featureNotExist);
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(message);
			}else {
				typeFeatureRepository.deleteById(idTypefeature);
				message.setMessage(Notification.success);
				
			}
			return ResponseEntity.ok(message);
		} catch (Exception e) {
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
		
	}
}
