
package ces.riccico.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ces.riccico.entity.House;
import ces.riccico.model.HouseDetailModel;
import ces.riccico.model.HouseModel;
import ces.riccico.model.RevenueMonthly;
import ces.riccico.model.TotalHouseMonthly;

@Repository
public interface HouseRepository extends JpaRepository<House, Integer> {
	
	@Query("Select new ces.riccico.model.HouseDetailModel (h.id, h.amenities, h.bedroom, h.maxGuest, h.price,"
			+ " h.size, h.address, h.content, h.country, h.image, h.images, h.phoneContact, h.city, h.title, h.status)"
			+ "from House h where h.id = ?1")
	HouseDetailModel findByHouseId(int houseId);
	
	@Query("Select h from House h where h.title like %?1% ")
	Page<House> findByTitle(String title, Pageable pageable);
	
	@Query("Select new ces.riccico.model.HouseModel (h.id, h.image, h.address, h.price, h.city, h.size, h.title, h.status, h.amenities, h.isBlock, h.modifiedDate) "
			+ "from House h where h.isBlock = false and h.status = 'listed'")
	Page<Object> findList(Pageable pageable);

	
	@Query("Select new ces.riccico.model.HouseModel (h.id, h.image, h.address, h.price, h.city, h.size, h.title, h.status, h.amenities, h.isBlock, h.modifiedDate)"
			+ " from House h where h.isBlock = false and h.country like %?1% and h.city like %?2% and h.size >= ?3 and h.size <= ?4 "
			+ "and h.price >= ?5 and h.price <= ?6  and h.maxGuest >= ?7 and h.maxGuest <= ?8 "
			+ "and h.status = 'listed' and h.isBlock = 'false'")
	List<HouseModel> searchFilter(String country, String city, Double lowestSize, Double highestSize, Double lowestPrice,
			Double highestPrice, byte lowestGuest, byte highestGuest);
	
//	@Query(value = "select * from houses h where h.is_block = false and h.country like %?1% and h.city like %?2% and h.size >= ?3 and h.size <= ?4 "
//			+ "and h.price >= ?5 and h.price <= ?6  and h.max_guest >= ?7 and h.max_guest <= ?8 "
//			+ "and h.status = 'listed' order by h.price asc", nativeQuery = true)
//	List<House> searchFilterAsc(String country, String city, Double lowestSize, Double highestSize, Double lowestPrice,
//			Double highestPrice, byte lowestGuest, byte highestGuest);
//	
//	@Query(value = "select * from houses h where h.is_block = false and h.country like %?1% and h.city like %?2% and h.size >= ?3 and h.size <= ?4 "
//			+ "and h.price >= ?5 and h.price <= ?6  and h.max_guest >= ?7 and h.max_guest <= ?8 "
//			+ "and h.status = 'listed' order by h.price desc", nativeQuery = true)
//	List<House> searchFilterDesc(String country, String city, Double lowestSize, Double highestSize, Double lowestPrice,
//			Double highestPrice, byte lowestGuest, byte highestGuest);

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

	@Query("select new ces.riccico.model.TotalHouseMonthly("
			+ "count(h.id), MONTH(h.createdAt)) from House h  where YEAR(h.createdAt) = ?1 group by MONTH(h.createdAt)")
	List<TotalHouseMonthly> getTotalHouseMonthly(int year);

	@Query("Select h from House h where h.status =?1")
	Page<House> getHouseForAdmin(String status, Pageable pageable);

}
