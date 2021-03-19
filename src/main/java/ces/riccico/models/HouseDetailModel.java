package ces.riccico.models;

import java.util.HashSet;
import java.util.Set;

import ces.riccico.entities.Images;

public class HouseDetailModel {
	private Integer id;

	private String title;
	
	private String address;
	
	private String country;

	private String province;

	private Double size;

	private String image;

	private double price;

	private String content;

	private String phoneContact;

	private boolean wifi;

	private boolean tivi;

	private boolean fridge;

	private boolean air_conditioner;

	private boolean swimPool;

	private byte bedroom;

	private byte maxGuest;

	private Set<Images> images = new HashSet<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public Double getSize() {
		return size;
	}

	public void setSize(Double size) {
		this.size = size;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPhoneContact() {
		return phoneContact;
	}

	public void setPhoneContact(String phoneContact) {
		this.phoneContact = phoneContact;
	}

	public boolean isWifi() {
		return wifi;
	}

	public void setWifi(boolean wifi) {
		this.wifi = wifi;
	}

	public boolean isTivi() {
		return tivi;
	}

	public void setTivi(boolean tivi) {
		this.tivi = tivi;
	}

	public boolean isFridge() {
		return fridge;
	}

	public void setFridge(boolean fridge) {
		this.fridge = fridge;
	}

	public boolean isAir_conditioner() {
		return air_conditioner;
	}

	public void setAir_conditioner(boolean air_conditioner) {
		this.air_conditioner = air_conditioner;
	}

	public boolean isSwimPool() {
		return swimPool;
	}

	public void setSwimPool(boolean swimPool) {
		this.swimPool = swimPool;
	}

	public byte getBedroom() {
		return bedroom;
	}

	public void setBedroom(byte bedroom) {
		this.bedroom = bedroom;
	}

	public byte getMaxGuest() {
		return maxGuest;
	}

	public void setMaxGuest(byte maxGuest) {
		this.maxGuest = maxGuest;
	}

	public Set<Images> getImages() {
		return images;
	}

	public void setImages(Set<Images> images) {
		this.images = images;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}


}
