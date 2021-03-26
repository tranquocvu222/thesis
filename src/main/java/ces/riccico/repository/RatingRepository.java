package ces.riccico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ces.riccico.entity.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer>{
	
	List<Rating> findByBookingAccountIdAccount(int accountId);
	
	Rating findByBookingId(int bookingId);
	
	List<Rating> findByBookingHouseId(int houseId);
	
}
