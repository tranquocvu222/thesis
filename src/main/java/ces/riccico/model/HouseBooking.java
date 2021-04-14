package ces.riccico.model;

import java.util.List;

import ces.riccico.entity.Booking;

public class HouseBooking {

	private String houseName;
	
	private List<Booking> listBooking;

	public String getHouseName() {
		return houseName;
	}

	public void setHouseName(String houseName) {
		this.houseName = houseName;
	}

	public List<Booking> getListBooking() {
		return listBooking;
	}

	public void setListBooking(List<Booking> listBooking) {
		this.listBooking = listBooking;
	}
}
