
package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.common.constants.CommonConstants;
import ces.riccico.common.enums.StatusBooking;
import ces.riccico.common.enums.StatusHouse;
import ces.riccico.entity.Account;
import ces.riccico.entity.Booking;
import ces.riccico.entity.House;
import ces.riccico.model.AdminStatistics;
import ces.riccico.model.BookingModel;
import ces.riccico.model.BookingPaid;
import ces.riccico.model.HouseBooking;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.BookingRepository;
import ces.riccico.repository.HouseRepository;
import ces.riccico.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {
	
	private static Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);
	
	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private HouseRepository houseRepository;
	
	@Override
	public ResponseEntity<?> findByBookingPaid(int page, int size ) {
		BookingPaid bookingPaid = new BookingPaid();
		Double netIncome = 0d;
		List<BookingModel> listBookingModel = new ArrayList<BookingModel>();

		for (Account account : accountRepository.findAll()) {
			if (account.getHouses().size() != 0) {
				BookingModel bookingModel = new BookingModel();
				List<HouseBooking> listHouseBooking = new ArrayList<HouseBooking>();
				bookingModel.setHostName(account.getUsername());

				for (House house : houseRepository.findByAccountId(account.getAccountId())) {
					HouseBooking houseBooking = new HouseBooking();

					if (StatusHouse.LISTED.getStatusName().equals(house.getStatus())) {
						List<Booking> listBooking = new ArrayList<Booking>();

						for (Booking booking : bookingRepository.findByHouseId(house.getId())) {
							if (booking.getStatus().equals(StatusBooking.PAID.getStatusName()) || booking.getStatus().equals(StatusBooking.COMPLETED.getStatusName())) {
								listBooking.add(booking);
							}
						}

						houseBooking.setHouseName(house.getTitle());
						houseBooking.setListBooking(listBooking);
					}

					listHouseBooking.add(houseBooking);
				}

				bookingModel.setListHouseBooking(listHouseBooking);
				listBookingModel.add(bookingModel);

			}

		}

		bookingPaid.setListBookingModel(listBookingModel);
		
		for (Booking booking : bookingRepository.findAll()) {
			if (booking.getStatus().equals(StatusBooking.PAID.getStatusName())) {
				netIncome += ((booking.getBill() * 15) / 100);
				CommonConstants.NET_INCOME = netIncome;
			}
		}

		bookingPaid.setNetIncome(netIncome);
		
		int fromIndex = (page) * size;
		final int numPages = (int) Math.ceil((double) listBookingModel.size() / (double) size);
		
		bookingPaid.setListBookingModel(listBookingModel.subList(fromIndex, Math.min(fromIndex + size, listBookingModel.size())));
		bookingPaid.setPageMax(numPages);

		return ResponseEntity.ok(bookingPaid);
	}


	@Override
	public ResponseEntity<?> statisticsAdmin() {
		AdminStatistics statisticsAdmin = new AdminStatistics();
		statisticsAdmin.setTotalAccountHost(houseRepository.totalAccountHost());
		statisticsAdmin.setTotalRevenue(bookingRepository.totalRevenue());
		statisticsAdmin.setTotalHouse(houseRepository.totalHouse());
        statisticsAdmin.setTotalBookingPaid(bookingRepository.totalBookingPaid());
        statisticsAdmin.setTotalBookingCompleted(bookingRepository.totalBookingCompleted());
 		return ResponseEntity.ok(statisticsAdmin);
	}
	
	
}
