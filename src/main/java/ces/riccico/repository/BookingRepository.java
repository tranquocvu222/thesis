package ces.riccico.repository;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ces.riccico.entity.Booking;
import ces.riccico.model.RevenueMonthly;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

	List<Booking> findByAccountId(int accountId);

	List<Booking> findByHouseId(int houseId);

	@Query(value = "SELECT b.booking_id, b.created_at, b.bill, b.create_check_in, "
			+ "b.create_end, b.status, b.account_id, b.house_id, b.modified_date"
			+ " FROM bookings b join houses h on b.house_id = h.house_id join "
			+ "accounts a on h.account_id = a.account_id WHERE a.account_id = ?1 "
			+ "ORDER BY b.created_at DESC LIMIT 5", nativeQuery = true)
	List<Booking> findAccountId(int accountId);

	@Query("select sum(bill) from Booking where status = 'paid' or status = 'completed'")
	Double totalRevenue();

	@Query("select count(id) from Booking where status = 'paid'")
	Integer totalBookingPaid();

	@Query("select count(id) from Booking where status = 'completed'")
	Integer totalBookingCompleted();

	@Query("select b from Booking b where b.status = 'paid'")
	List<Booking> findBookingPaid();

	@Query("Select SUM(b.bill) from Booking b where b.house.account.id = ?1 and (b.status = 'completed' or b.status = 'paid')")
	Long sumByAccountId(int accountId);

	@Query("Select COUNT(b) from Booking b where b.house.account.id = ?1 and (b.status = 'completed' or b.status = 'paid')")
	Integer countByAccountId(int accountId);

	@Query("Select b from Booking b where b.account.id =?1")
	Page<Booking> getAllBookingForCustomer(int accountId, Pageable pageable);

	@Query("Select b from Booking b where b.account.id =?1 and b.status =?2")
	Page<Booking> getBookingForCustomer(int accountId, String status, Pageable pageable);

	@Query("Select b from Booking b where b.house.account.id =?1")
	Page<Booking> getAllBookingForHost(int accountId, Pageable pageable);

	@Query("Select b from Booking b where b.house.account.id =?1 and b.status =?2")
	Page<Booking> getBookingForHost(int accountId, String status, Pageable pageable);

	@Query("Select b from Booking b where b.status ='pending'")
	List<Booking> getAllBookingPending();

	@Query(value = "select sum(bill), date_part('month',create_check_in) as month "
			+ "from bookings  where (status = 'completed' or status = 'paid') "
			+ "and date_part('year',create_check_in) = ?1 \r\n"
			+ "group by date_part('month',create_check_in)", nativeQuery = true)
	List<Object> getMonthlyRevenue(int year);

	@Query("Select b.house.id from Booking b where b.account.id =?1 and b.house.id != ?2")
	List<Integer> getListHouseUserBooked(int accountId, Integer houseId);
	
	
	@Query(value ="Select b.house_id from bookings b where b.house_id != ?1 "
			+ "GROUP BY b.house_id ORDER BY (COUNT(b.booking_id)) DESC LIMIT 6", nativeQuery = true)
	List<Integer> getListHouseMostBooked(Integer houseId);
 
	@Query(value = "Select b.house_id from bookings b inner join ratings r"
			+ " on b.booking_id = r.booking_id inner join houses h "
			+ "on b.house_id = h.house_id  where h.city=?1 and b.house_id != ?2 and h.status ='listed'"
			+ "GROUP BY  b.house_id ORDER BY (round(AVG(r.star),1)) "
			+ "DESC LIMIT 8 ", nativeQuery = true)
	List<Integer> getListHousePopular(String city, Integer houseId);
}
