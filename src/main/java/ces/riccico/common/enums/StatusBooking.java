package ces.riccico.common.enums;

public enum StatusBooking {


	CANCELED("canceled"),

	COMPLETED("completed"),

	PAID("paid"),

	PENDING("pending"),

	INCOMPLETED("incompleted");

	private String statusName;

	StatusBooking(String statusName) {
		this.statusName = statusName;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

}
