package ces.riccico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import ces.riccico.models.TypeRoom;

public interface TypeRoomService {

	ResponseEntity<?> deleteRoom(Integer idTypeRoom);

	ResponseEntity<?> createRoom(TypeRoom room);

	List<TypeRoom> getAll();

}
