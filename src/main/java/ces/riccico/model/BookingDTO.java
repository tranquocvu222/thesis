package ces.riccico.model;

import java.util.Date;

public class BookingDTO {
	
	private Date dateCheckIn;

	private Date dateCheckOut;

	public BookingDTO() {
		super();
	}

	public BookingDTO(Date dateCheckIn, Date dateCheckOut) {
		super();
		this.dateCheckIn = dateCheckIn;
		this.dateCheckOut = dateCheckOut;
	}

	public Date getDateCheckIn() {
		return dateCheckIn;
	}

	public void setDateCheckIn(Date dateCheckIn) {
		this.dateCheckIn = dateCheckIn;
	}

	public Date getDateCheckOut() {
		return dateCheckOut;
	}

	public void setDateCheckOut(Date dateCheckOut) {
		this.dateCheckOut = dateCheckOut;
	}
	
	
}
