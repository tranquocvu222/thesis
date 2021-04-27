package ces.riccico.model;

import java.util.List;

public class RevenueMonthly {
	
	private Double revenue;
	
	private Integer month;
	
	

	public RevenueMonthly(Double revenue, Integer month) {
		super();
		this.revenue = revenue;
		this.month = month;
	}

	public Double getRevenue() {
		return revenue;
	}

	public void setRevenue(Double revenue) {
		this.revenue = revenue;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	@Override
	public String toString() {
		return "RevenueMonthly [revenue=" + revenue + ", month=" + month + "]";
	}
	
	
	
	
	
	

}
