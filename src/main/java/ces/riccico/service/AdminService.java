package ces.riccico.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;

public interface AdminService {



	ResponseEntity<?> findByBookingPaid(int size, int page);
	
	ResponseEntity<?> monthlyRevenue(int year);

	ResponseEntity<?> totalHouseMonthly(int year);

	ResponseEntity<?> statisticsAdmin();

}
