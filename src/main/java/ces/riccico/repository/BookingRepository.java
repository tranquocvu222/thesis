package ces.riccico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ces.riccico.entities.Booking;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
	List<Booking> findByAccountIdAccount(int accountId);
	List<Booking> findByHouseId(int houseId);
}
