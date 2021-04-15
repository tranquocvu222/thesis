package ces.riccico.common.enums;

public enum StatusHouse {
	
	APPROVAL("approval"),

	LISTED("listed"),

	UNLISTED("unlisted"),

	BLOCKED("blocked"),

	DEACTIVED("deactived");

	private String statusName;

	StatusHouse(String statusName) {
		this.statusName = statusName;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
}
