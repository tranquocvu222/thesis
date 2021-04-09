package ces.riccico.model;

import java.util.List;

public class BookingByStatus {
	private List<BookingDetailModel> listBookingCanceled;
	
	private List<BookingDetailModel> listBookingPending;
	
	private List<BookingDetailModel> listBookingPaid;
	
	private List<BookingDetailModel> listBookingCompleted;
	
	private List<BookingDetailModel> listBookingRefunded;

	public List<BookingDetailModel> getListBookingCanceled() {
		return listBookingCanceled;
	}

	public void setListBookingCanceled(List<BookingDetailModel> listBookingCanceled) {
		this.listBookingCanceled = listBookingCanceled;
	}

	public List<BookingDetailModel> getListBookingPending() {
		return listBookingPending;
	}

	public void setListBookingPending(List<BookingDetailModel> listBookingPending) {
		this.listBookingPending = listBookingPending;
	}

	public List<BookingDetailModel> getListBookingPaid() {
		return listBookingPaid;
	}

	public void setListBookingPaid(List<BookingDetailModel> listBookingPaid) {
		this.listBookingPaid = listBookingPaid;
	}

	public List<BookingDetailModel> getListBookingCompleted() {
		return listBookingCompleted;
	}

	public void setListBookingCompleted(List<BookingDetailModel> listBookingCompleted) {
		this.listBookingCompleted = listBookingCompleted;
	}

	public List<BookingDetailModel> getListBookingRefunded() {
		return listBookingRefunded;
	}

	public void setListBookingRefunded(List<BookingDetailModel> listBookingRefunded) {
		this.listBookingRefunded = listBookingRefunded;
	}
	
}
