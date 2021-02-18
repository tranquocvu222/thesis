package ces.riccico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ces.riccico.models.Booking;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
	List<Booking> findByHouseId(String houseId);
}
