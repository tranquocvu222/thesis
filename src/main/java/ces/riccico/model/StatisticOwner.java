package ces.riccico.model;

import java.util.List;

import ces.riccico.entity.Booking;
import ces.riccico.entity.House;

public class StatisticOwner {
	
	private Long revenue;
	
	private Integer totalBooking;
	
	private Integer totalRating;
	
	private Float averageRating;
	
	private List<BookingDetailModel> listBooking;

	public Long getRevenue() {
		return revenue;
	}

	public void setRevenue(Long revenue) {
		this.revenue = revenue;
	}

	public Integer getTotalBooking() {
		return totalBooking;
	}

	public void setTotalBooking(Integer totalBooking) {
		this.totalBooking = totalBooking;
	}

	public Integer getTotalRating() {
		return totalRating;
	}

	public void setTotalRating(Integer totalRating) {
		this.totalRating = totalRating;
	}

	public Float getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Float averageRating) {
		this.averageRating = averageRating;
	}

	public List<BookingDetailModel> getListBooking() {
		return listBooking;
	}

	public void setListBooking(List<BookingDetailModel> listBooking) {
		this.listBooking = listBooking;
	}

	
	
	

	
	
}
	
