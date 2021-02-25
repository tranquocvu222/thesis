package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.models.TypeRoom;
import ces.riccico.notification.Notification;
import ces.riccico.notification.FeatureNotification;

import ces.riccico.repository.TypeRoomRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.TypeRoomService;

@Service
public class TypeRoomServiceImpl implements TypeRoomService {

	@Autowired
	TypeRoomRepository roomRepository;

	@Autowired
	SecurityAuditorAware securityAuditorAware;

//	Show list Rooms
	@Override
	public List<TypeRoom> getAll() {
		try {
			List<TypeRoom> listRoom = new ArrayList<TypeRoom>();
			listRoom = roomRepository.findAll();
			return listRoom;
		} catch (Exception e) {
			System.out.println("getAll: " + e);
			return null;
		}
	}

//	Add TypeFeature
	@Override
	public ResponseEntity<?> createRoom(TypeRoom room) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		try {
			if (idCurrent == null || idCurrent.isEmpty()) {
				ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.loginRequired));
			}else if (room.getRoomName() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,FeatureNotification.RoomNull));
			}else {
			roomRepository.saveAndFlush(room);
			}
			return ResponseEntity.ok(Map.of(Notification.message,Notification.success));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
		}
	}

//	Update TypeFeature
//	@Override
//	public ResponseEntity<?> updateRoom(Integer idTypeRoom, TypeRoom rooms ) {
//
//		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
//		try {
//			if (idCurrent == null ||idCurrent.isEmpty()) {
//				ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthNotification.loginRequired);
//			}
//			Optional<TypeRoom> room = roomRepository.findById(rooms.getIdTyperoom());
//			if (room.isPresent()) {
//				room.get().setRoomName(rooms.getRoomName());
//				roomRepository.saveAndFlush(room.get());
//			}
//			return ResponseEntity.ok(AuthNotification.success);
//		} catch (Exception e) {
//			System.out.println(e);
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthNotification.fail);
//			
//		}
//
//	}

//	Delete TypeFeature
	@Override
	public ResponseEntity<?> deleteRoom(Integer idTypeRoom) {
		String idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Optional<TypeRoom> room = roomRepository.findById(idTypeRoom);
		try {
			if (idCurrent == null || idCurrent.isEmpty()) {
				ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.loginRequired));
			} else if (room.isPresent()) {
				roomRepository.deleteById(idTypeRoom);
			}
			return ResponseEntity.ok(Map.of(Notification.message,Notification.success));
		} catch (Exception e) {
			System.out.println(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(Notification.message,Notification.fail));
		}

	}

}
