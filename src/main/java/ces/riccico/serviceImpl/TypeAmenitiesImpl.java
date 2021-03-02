package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.Accounts;
import ces.riccico.models.Message;
import ces.riccico.models.TypeAmenities;
import ces.riccico.notification.FeatureNotification;
import ces.riccico.notification.Notification;
import ces.riccico.notification.UserNotification;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.TypeAmenitiesRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.TypeAmenitiesService;

@Service
public class TypeAmenitiesImpl implements TypeAmenitiesService {

	@Autowired
	TypeAmenitiesRepository typeAmenitiesRepository;

	@Autowired
	SecurityAuditorAware securityAuditorAware;
	
	@Autowired
	private AccountRepository accountRepository;
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
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Accounts account = accountRepository.findById(idCurrent).get();
		Message message = new Message();
		try {
			if (account == null) {
				message.setMessage(UserNotification.accountNotExist);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			} else if (typeAmenities.getAmenitiesName() == null) {
				message.setMessage(FeatureNotification.AmenitiesNull);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else {
				typeAmenitiesRepository.saveAndFlush(typeAmenities);
			}
			message.setMessage(Notification.success);
			return ResponseEntity.ok(message);
		} catch (Exception e) {
			System.out.println(e);
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

	}

//	Update TypeAmenities

	@Override
	public ResponseEntity<?> updateTypeAmenities(TypeAmenities typeAmenities, Integer idTypeamenities) {
		Message message = new Message();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Accounts account = accountRepository.findById(idCurrent).get();
		try {
			TypeAmenities amenities = typeAmenitiesRepository.findById(idTypeamenities).get();
			if (account == null) {
				message.setMessage(UserNotification.accountNotExist);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			}
			if (amenities == null) {
				message.setMessage(FeatureNotification.amenitiesNotExist);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			} else {
				amenities.setAmenitiesName(typeAmenities.getAmenitiesName());

				typeAmenitiesRepository.saveAndFlush(amenities);
				System.out.println("-------" + amenities.getAmenitiesName());
			}
			message.setMessage(Notification.success);
			return ResponseEntity.ok(message);

		} catch (Exception e) {
			System.out.println(e);
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

	}

//	Delete TypeFeature
	@Override
	public ResponseEntity<?> deleteTypeAmenities(Integer idAmenities) {
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Accounts account = accountRepository.findById(idCurrent).get();
		TypeAmenities typeAmenities = typeAmenitiesRepository.findById(idAmenities).get();
		Message message = new Message();
		try {
			if (account == null) {
				message.setMessage(UserNotification.accountNotExist);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			} else if (typeAmenities == null) {
				message.setMessage(FeatureNotification.amenitiesNotExist);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			} else {
				typeAmenitiesRepository.deleteById(idAmenities);
			}
			message.setMessage(Notification.success);
			return ResponseEntity.ok(message);

		} catch (Exception e) {
			System.out.println(e);
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

}
