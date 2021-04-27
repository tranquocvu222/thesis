
package ces.riccico.repository;

import java.util.List;

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

	@Query("Select h from House h where h.isBlock = false and h.status = 'listed'")
	Page<House> findList(Pageable pageable);

	@Query("Select h from House h where h.country like %?1% and h.city like %?2% and h.size >= ?3 and h.size <= ?4 "
			+ "and h.price >= ?5 and h.price <= ?6  and h.maxGuest >= ?7 and h.maxGuest <= ?8 "
			+ "and h.status = 'listed'")
	List<House> searchFilter(String country, String city, Double lowestSize, Double highestSize, Double lowestPrice,
			Double highestPrice, byte lowestGuest, byte highestGuest);

	@Query("select count(DISTINCT account) from House ")
	Integer totalAccountHost();

	@Query("select count(id) from  House ")
	Integer totalHouse();

	List<House> findByAccountId(int accountId);

	@Query("Select h from House h where h.account.id =?1")
	Page<House> getAllHouseForHost(int accountId, Pageable pageable);
	
	@Query("Select h from House h where h.account.id =?1 and h.status =?2 and h.isBlock = ?3")
	Page<House> getHouseForHost(int accountId, String  status, boolean block, Pageable pageable);
	
	@Query("Select h from House h where h.account.id =?1 and h.isBlock = ?2")
	Page<House> getHouseBlockForHost(int accountId, boolean block, Pageable pageable);
	
	@Query("Select h from House h")
	Page<House> getAllHouseForAdmin(Pageable pageable);
	
	@Query("Select h from House h where h.isBlock = ?1 and h.status =?2")
	Page<House> getHouseForAdmin(boolean block, String  status, Pageable pageable);
	
	@Query("Select h from House h where h.isBlock = ?1")
	Page<House> getHouseBlockForAdmin(boolean block, Pageable pageable);

	@Query("Select h from House h where h.status =?1")
	Page<House> getHouseForAdmin(String status, Pageable pageable);

}
