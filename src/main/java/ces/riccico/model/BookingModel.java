package ces.riccico.model;

import java.util.List;

import ces.riccico.entity.Booking;

public class BookingModel {
	
	private String hostName;

	private List<HouseBooking> listHouseBooking;
	

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public List<HouseBooking> getListHouseBooking() {
		return listHouseBooking;
	}

	public void setListHouseBooking(List<HouseBooking> listHouseBooking) {
		this.listHouseBooking = listHouseBooking;
	}

	
	
	


}
