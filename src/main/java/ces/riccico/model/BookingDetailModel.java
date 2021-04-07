package ces.riccico.model;

import java.util.Date;

import ces.riccico.entity.Booking;
import ces.riccico.entity.Rating;

public class BookingDetailModel {
	private Integer id;

	private double bill;
	
	private Date createdAt;

	private Date dateCheckIn;
	
	private Date dateCheckOut;
	
	private String status;
	
	private String houseName;
	
	private int customerId;

	private String customerName;
	
	private int houseId;
	
	private Rating rating;
	

	public int getHouseId() {
		return houseId;
	}

	public void setHouseId(int houseId) {
		this.houseId = houseId;
	}

	public String getHouseName() {
		return houseName;
	}

	public void setHouseName(String houseName) {
		this.houseName = houseName;
	}


	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public Integer getId() {
		return id;
	}


	public double getBill() {
		return bill;
	}

	public void setBill(double bill) {
		this.bill = bill;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	

}
