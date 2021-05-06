package ces.riccico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ces.riccico.entity.Rating;
import ces.riccico.model.RatingCustomerModel;
import ces.riccico.model.RatingModel;



@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

	List<Rating> findByBookingAccountId(int accountId);

	Rating findByBookingId(int bookingId);
	
	@Query("Select new ces.riccico.model.RatingCustomerModel (r.id,r.createdAt, r.modifiedDate, r.star, r.content) "
			+ "from Rating r where r.booking.id = ?1")
	RatingCustomerModel findByBooking(int bookingId);

//	List<Rating> findByBookingHouseId(int houseId);
	
	@Query("Select new ces.riccico.model.RatingModel (t.createdAt, t.modifiedDate, t.star, t.content, t.booking.account.username) "
			+ "from Rating t where t.booking.house.id = ?1")
	List<RatingModel> findByBookingHouseId(int houseId);
	
	@Query("Select COUNT(b) from Rating b where b.booking.house.account.id = ?1")
	Integer countByAccountId (int accountId);
	
	@Query("Select AVG(b.star) from Rating b where b.booking.house.account.id = ?1")
	Float averageRatingByAccountId (int accountId);
	
}
