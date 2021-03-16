package ces.riccico.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ces.riccico.entities.House;

@Repository
public interface HouseRepository extends JpaRepository<House, Integer> {
	// @Query("Select h from House h where h.name like %?1% and h.isApproved = true
	// and h.isDeleted = false")
	@Query("Select h from House h where h.title like %?1% ")
	Page<House> findByTitle(String title, Pageable pageable);

	@Query("Select h from House h where h.isApproved = true and h.isDeleted = false")
	Page<House> findList(Pageable pageable);

	@Query("Select h from House h where h.country like %?1% and h.province like %?2% and h.size >= ?3 and h.size <= ?4 "
			+ "and h.price >= ?5 and h.price <= ?6 and h.tivi = ?7 and h.wifi = ?8 and h.air_conditioner = ?9 and h.fridge = ?10 "
			+ "and h.swimPool = ?11 and h.maxGuest >= ?12 and h.maxGuest < ?13 and h.isApproved = true and h.isDeleted = false")
	Page<House> findFilter(String country, String province, Double sizeBelow, Double sizeAbove, Double priceBelow,
			Double priceAbove, boolean tivi, boolean wifi, boolean air_conditioner, boolean fridge, boolean swim_pool,
			 byte guestAbove, byte guestBelow, Pageable pageable);

}
