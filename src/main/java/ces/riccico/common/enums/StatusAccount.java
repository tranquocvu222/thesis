package ces.riccico.common.enums;

public enum StatusAccount {
	
	TRUE("true"),

	FALSE("false");

	private String statusName;

	StatusAccount(String statusName) {
		this.statusName = statusName;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	
	
	

}
