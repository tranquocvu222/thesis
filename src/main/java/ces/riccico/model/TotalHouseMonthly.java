package ces.riccico.model;

public class TotalHouseMonthly {

	private long totalHouse;
	
	private Integer month;

	public TotalHouseMonthly(long totalHouse, Integer month) {
		super();
		this.totalHouse = totalHouse;
		this.month = month;
	}

	public long getTotalHouse() {
		return totalHouse;
	}

	public void setTotalHouse(long totalHouse) {
		this.totalHouse = totalHouse;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}
	
	
}
