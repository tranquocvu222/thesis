
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


import ces.riccico.models.TypeRoom;
import ces.riccico.service.TypeRoomService;

@CrossOrigin
@RestController
public class TypeRoomController {

	@Autowired
	TypeRoomService typeRoomService;

//	Show list TypeFeature
	@RequestMapping(value = "/typeroom", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('admin','user')")
	public List<TypeRoom> getAll() {
		return typeRoomService.getAll();
	}

//	Add TypeFeature
	@RequestMapping(value = "/createTypeRoom", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> createTypeRoom(@RequestBody TypeRoom typeRoom) {
		return typeRoomService.createRoom(typeRoom);
	}

//	Update TypeFeature
//	@RequestMapping(value = "/updateTypeRoom", method = RequestMethod.POST)
//	@PreAuthorize("hasAnyAuthority('admin')")
//	public ResponseEntity<?> updateTypeRoom(@RequestBody TypeRoom typeRoom) {
//		return typeRoomService.updateRoom(typeRoom);
//	}

//	Delete TypeFeature
	@RequestMapping(value = "/deleteTypeRoom/{idTypeRoom}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> deleteTypeRoom(@PathVariable Integer idTypeRoom) {
		return typeRoomService.deleteRoom(idTypeRoom);

	}
}
