package ces.riccico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ces.riccico.entity.Booking;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

	List<Booking> findByAccountId(int accountId);

	List<Booking> findByHouseId(int houseId);
	
	@Query("Select SUM(b.bill) from Booking b where b.house.account.id = ?1 and (b.status = 'completed' or b.status = 'paid')")
	double sumByAccountId(int accountId);
	
	@Query("Select COUNT(b) from Booking b where b.house.account.id = ?1 and (b.status = 'completed' or b.status = 'paid')")
	int countByAccountId (int accountId);

	
}
