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

//	@Query("Select h from House h where h.country like %?1% and h.province like %?2% and h.size = ?3 and h.price >= ?4 and size.price <= ?5"
//			+ "and h.bedroom = ?6 and h.maxGuest = ?7 and h.tivi = ?8 and h.wifi = ?9 and h.air_conditioner = ?10 and h.fridge = ?11"
//			+ "h.swimPool = ?12")
//	Page<House> findByFilter(String country, String province, Double size, Double priceBelow, Double priceAbove,
//			byte bedroom, byte maxGuest, boolean tivi, boolean wifi, boolean air_conditioner, boolean fridge,
//			boolean swimPool, Pageable pageable);
}
