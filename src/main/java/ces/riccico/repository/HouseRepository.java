package ces.riccico.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ces.riccico.entity.House;

@Repository
public interface HouseRepository extends JpaRepository<House, Integer> {

	@Query("Select h from House h where h.title like %?1% ")
	Page<House> findByTitle(String title, Pageable pageable);

	@Query("Select h from House h where h.isApproved = true and h.isDeleted = false")
	Page<House> findList(Pageable pageable);

	@Query("Select h from House h where h.country like %?1% and h.city like %?2% and h.size >= ?3 and h.size <= ?4 "
			+ "and h.price >= ?5 and h.price <= ?6  and h.maxGuest >= ?7 and h.maxGuest <= ?8 "
			+ "and h.isApproved = true and h.isDeleted = false")
	Page<House> searchFilter(String country, String city, Double lowestSize, Double highestSize,
			Double lowestPrice, Double highestPrice, byte lowestGuest, byte highestGuest, Pageable pageable);

}
