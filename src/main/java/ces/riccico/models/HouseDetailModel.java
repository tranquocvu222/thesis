package ces.riccico.models;

import java.util.HashSet;
import java.util.Set;

import ces.riccico.entities.Images;

public class HouseDetailModel {

	private Integer id;

	private boolean air_conditioner;

	private boolean fridge;

	private boolean swimPool;

	private boolean tivi;

	private boolean wifi;

	private byte bedroom;

	private byte maxGuest;

	private double price;

	private Double size;

	private String address;

	private String content;

	private String country;

	private String image;

	private String phoneContact;

	private String province;

	private String title;

	private Set<Images> images = new HashSet<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isAir_conditioner() {
		return air_conditioner;
	}

	public void setAir_conditioner(boolean air_conditioner) {
		this.air_conditioner = air_conditioner;
	}

	public boolean isFridge() {
		return fridge;
	}

	public void setFridge(boolean fridge) {
		this.fridge = fridge;
	}

	public boolean isSwimPool() {
		return swimPool;
	}

	public void setSwimPool(boolean swimPool) {
		this.swimPool = swimPool;
	}

	public boolean isTivi() {
		return tivi;
	}

	public void setTivi(boolean tivi) {
		this.tivi = tivi;
	}

	public boolean isWifi() {
		return wifi;
	}

	public void setWifi(boolean wifi) {
		this.wifi = wifi;
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Double getSize() {
		return size;
	}

	public void setSize(Double size) {
		this.size = size;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getPhoneContact() {
		return phoneContact;
	}

	public void setPhoneContact(String phoneContact) {
		this.phoneContact = phoneContact;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Set<Images> getImages() {
		return images;
	}

	public void setImages(Set<Images> images) {
		this.images = images;
	}

}