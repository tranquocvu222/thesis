package ces.riccico.service;

import org.springframework.http.ResponseEntity;

public interface AdminService {

	ResponseEntity<?> statisticsAdmin();

	ResponseEntity<?> findByBookingPaid(int size, int page);
	
	ResponseEntity<?> monthlyRevenue(int year);

}
