package ces.riccico.model;

import java.util.Date;

public class BookingCustomerModel {
	private Integer id;

	private double bill;

	private Date createdAt;

	private Date dateCheckIn;

	private Date dateCheckOut;

	private String status;

	private String houseName;

	private int houseId;

	private long night;

	public BookingCustomerModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BookingCustomerModel(Integer id, double bill, Date createdAt, Date dateCheckIn, Date dateCheckOut,
			String status, String houseName, int houseId) {
		super();
		this.id = id;
		this.bill = bill;
		this.createdAt = createdAt;
		this.dateCheckIn = dateCheckIn;
		this.dateCheckOut = dateCheckOut;
		this.status = status;
		this.houseName = houseName;
		this.houseId = houseId;
	}

	private RatingCustomerModel rating;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public double getBill() {
		return bill;
	}

	public void setBill(double bill) {
		this.bill = bill;
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

	public String getHouseName() {
		return houseName;
	}

	public void setHouseName(String houseName) {
		this.houseName = houseName;
	}

	public int getHouseId() {
		return houseId;
	}

	public void setHouseId(int houseId) {
		this.houseId = houseId;
	}

	public long getNight() {
		return night;
	}

	public void setNight(long night) {
		this.night = night;
	}

	public RatingCustomerModel getRating() {
		return rating;
	}

	public void setRating(RatingCustomerModel rating) {
		this.rating = rating;
	}

}
