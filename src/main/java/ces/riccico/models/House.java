
package ces.riccico.models;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "houses")
public class House extends Auditable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idHouse")
	private Integer id;


	@Column(name = "title", length = 1500)
	private String title;

	@Column(name = "country", length = 50)
	private String country;

	@Column(name = "province", length = 50)
	private String province;

	@Column(name = "address", length =1500)
	private String address;

	@Column(name = "price")
	private double price;

	@Column(name = "size")
	private Double size;

	@Column(name = "content", length = 15000)
	private String content;

	@Column(name = "isApproved")
	private boolean isApproved;

	@Column(name = "image")
	private String image;
	
	@Column(name = "phoneContact")
	private String phoneContact;

	@Column(name = "isDeleted")
	private boolean isDeleted;

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_account", nullable = false)
	@JsonIgnore
	private Accounts account;

	@OneToMany(mappedBy = "house", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<Booking> bookings = new HashSet<>();
	
//	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JoinTable(name = "roomHouse",
//        joinColumns = @JoinColumn(name = "idHouse"),
//        inverseJoinColumns = @JoinColumn(name = "idTyperoom"))
//	private Set<TypeRoom> typeRoom = new HashSet<>();
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "feature",
        joinColumns = @JoinColumn(name = "idHouse"),
        inverseJoinColumns = @JoinColumn(name = "idFeature"))
	private Set<TypeFeature> typeFeature = new HashSet<>();
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "amenitiesHouse",
        joinColumns = @JoinColumn(name = "idHouse"),
        inverseJoinColumns = @JoinColumn(name = "idAmenities"))
	private Set<Amenities> amenities = new HashSet<>();
	
	
	@OneToMany(mappedBy = "house", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private Set<Images> images = new HashSet<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	

	public boolean isApproved() {
		return isApproved;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Accounts getAccount() {
		return account;
	}

	public void setAccount(Accounts account) {
		this.account = account;
	}

	public Set<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(Set<Booking> bookings) {
		this.bookings = bookings;
	}

//	public Set<TypeRoom> getTypeRoom() {
//		return typeRoom;
//	}
//
//	public void setTypeRoom(Set<TypeRoom> typeRoom) {
//		this.typeRoom = typeRoom;
//	}

	public Set<TypeFeature> getTypeFeature() {
		return typeFeature;
	}

	public void setTypeFeature(Set<TypeFeature> typeFeature) {
		this.typeFeature = typeFeature;
	}

	public Set<Amenities> getAmenities() {
		return amenities;
	}

	public void setAmenities(Set<Amenities> amenities) {
		this.amenities = amenities;
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

	public Set<Images> getImages() {
		return images;
	}

	public void setImages(Set<Images> images) {
		this.images = images;
	}

	public String getAddress() {
		return address;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	

}
