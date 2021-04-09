package ces.riccico.common.enums;

public enum Status {

//	APPROVAL("approval"),

	CANCELED("canceled"),

	COMPLETED("completed"),

	PAID("paid"),

	PENDING_PAYMENT("pending payment"),

	REFUNDED("refunded");

	private String statusName;

	Status(String statusName) {
		this.statusName = statusName;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

}
