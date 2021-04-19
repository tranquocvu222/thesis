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
	
	@Query("select sum(bill) from Booking where status = 'paid' or status = 'completed'")
	Double totalRevenue();
	
	@Query("select count(id) from Booking where status = 'paid'")
	Integer totalBookingPaid();
	
	@Query("select count(id) from Booking where status = 'completed'")
	Integer totalBookingCompleted();
	
	@Query("select b from Booking b where b.status = 'paid'")
	List<Booking> findBookingPaid();
	

	
}
