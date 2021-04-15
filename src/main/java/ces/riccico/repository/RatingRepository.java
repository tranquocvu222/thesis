package ces.riccico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ces.riccico.entity.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

	List<Rating> findByBookingAccountId(int accountId);

	Rating findByBookingId(int bookingId);

	List<Rating> findByBookingHouseId(int houseId);
	
	@Query("Select COUNT(b) from Rating b where b.booking.house.account.id = ?1")
	int countByAccountId (int accountId);
	
	@Query("Select AVG(b.star) from Rating b where b.booking.house.account.id = ?1")
	float averageRatingByAccountId (int accountId);
	
}
