package ces.riccico.models;

public enum Status {
	PENDING_APPROVAL("pending_approval"),
	APPROVAL("approval"),
	CANCELED ("canceled"),
	PENDING_PAYMENT("pending payment"),
	REFUNDED("refunded"),
	COMPLETED("completed");
	
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
