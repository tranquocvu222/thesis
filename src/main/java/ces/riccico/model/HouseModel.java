package ces.riccico.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class HouseModel {

	private Integer id;

	private String image;

	private String address;

	private double price;

	private String city;

	private Double size;

	private String title;

	private String status;
	
	@JsonIgnore
	private String amenities;

	private boolean isBlock;

	private Date modifiedDate;

	public HouseModel() {
		super();
	}
	
	public HouseModel(Integer id, String image, String address, double price, String city, Double size, String title,
			String status, String amenities, boolean isBlock, Date modifiedDate) {
		super();
		this.id = id;
		this.image = image;
		this.address = address;
		this.price = price;
		this.city = city;
		this.size = size;
		this.title = title;
		this.status = status;
		this.amenities = amenities;
		this.isBlock = isBlock;
		this.modifiedDate = modifiedDate;
	}



	public boolean isBlock() {
		return isBlock;
	}

	public void setBlock(boolean isBlock) {
		this.isBlock = isBlock;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Double getSize() {
		return size;
	}

	public void setSize(Double size) {
		this.size = size;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getAmenities() {
		return amenities;
	}

	public void setAmenities(String amenities) {
		this.amenities = amenities;
	}
	
	

}
