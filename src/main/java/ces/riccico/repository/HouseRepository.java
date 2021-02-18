package ces.riccico.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ces.riccico.models.House;

@Repository
public interface HouseRepository extends JpaRepository<House, String> {
//	@Query("Select h from House h where h.name like %?1% and h.isApproved = true and h.isDeleted = false")
	@Query("Select h from House h where h.name like %?1% ")
	Page<House> findByName(String name, Pageable pageable);
}
