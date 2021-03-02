package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.Accounts;
import ces.riccico.models.Amenities;
import ces.riccico.models.Message;
import ces.riccico.models.TypeAmenities;
import ces.riccico.models.TypeFeature;
import ces.riccico.models.TypeRoom;
import ces.riccico.notification.FeatureNotification;
import ces.riccico.notification.Notification;
import ces.riccico.notification.UserNotification;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.AmenitiesRepository;
import ces.riccico.repository.TypeAmenitiesRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.AmenitiesService;

@Service
public class AmenitiesServiceImpl implements AmenitiesService {

	@Autowired
	AmenitiesRepository amenitiesRepository;
	
	@Autowired
	AccountRepository accountRepository;

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
		Message message = new Message();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Accounts account = accountRepository.findById(idCurrent).get();
		TypeAmenities typeAmenities = typeAmenitiesRepository.findById(idTypeAmenities).get();
		try {
			
			if (account == null) {
				message.setMessage(UserNotification.accountNotExist);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			} else if (typeAmenities == null) {
				message.setMessage(FeatureNotification.amenitiesNotExist);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			} else if (amenities.getAmenitiesName() == null) {
				message.setMessage(FeatureNotification.AmenitiesNull);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			} else {
				amenities.setTypeAmenities(typeAmenities);
				amenitiesRepository.saveAndFlush(amenities);
				message.setMessage(Notification.success);
				return ResponseEntity.ok(message);
			}
		} catch (Exception e) {
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

	}

//	Delete Amenities
	@Override
	public ResponseEntity<?> deleteAmenities(Integer idAmenities) {
		Message message = new Message();
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Accounts account = accountRepository.findById(idCurrent).get();
		Amenities amenities = amenitiesRepository.findById(idAmenities).get();
		try {
			if (account == null) {
				message.setMessage(UserNotification.accountNotExist);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
			} else if (amenities == null) {
				message.setMessage(FeatureNotification.amenitiesNotExist);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
			} else {
				amenitiesRepository.deleteById(idAmenities);
				message.setMessage(Notification.success);
				return ResponseEntity.ok(message);
			}
		} catch (Exception e) {
			message.setMessage(Notification.fail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

	}

}
