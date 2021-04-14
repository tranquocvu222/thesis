package ces.riccico.model;

import java.util.List;

public class BookingPaid {
	
	private int pageMax;
	
	private List<BookingModel> listBookingModel;
	
	private Double netIncome;
	
	public List<BookingModel> getListBookingModel() {
		return listBookingModel;
	}
	public void setListBookingModel(List<BookingModel> listBookingModel) {
		this.listBookingModel = listBookingModel;
	}
	public Double getNetIncome() {
		return netIncome;
	}
	public void setNetIncome(Double netIncome) {
		this.netIncome = netIncome;
	}
	public int getPageMax() {
		return pageMax;
	}
	public void setPageMax(int pageMax) {
		this.pageMax = pageMax;
	}
	
	

}
