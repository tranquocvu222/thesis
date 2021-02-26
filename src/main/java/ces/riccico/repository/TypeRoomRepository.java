package ces.riccico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ces.riccico.models.TypeRoom;

@Repository
public interface TypeRoomRepository extends JpaRepository<TypeRoom, Integer> {

	TypeRoom findByRoomName (String roomName);
}
