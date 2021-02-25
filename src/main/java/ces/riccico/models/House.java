
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

	@Column(name = "name", length = 200)
	private String name;

	@Column(name = "country", length = 50)
	private String country;

	@Column(name = "city", length = 50)
	private String city;

	@Column(name = "address", length = 100)
	private String address;

	@Column(name = "location", length = 200)
	private String location;

	@Column(name = "price")
	private double price;
	
	@Column(name = "introduce", length = 1500)
	private String introduce;

	@Column(name = "isApproved")
	private boolean isApproved;

	@Column(name = "image")
	private String image;

	@Column(name ="isDeleted")
	private boolean isDeleted;
	

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_account", nullable = false)
	@JsonIgnore
	private Accounts account;
	
	@OneToMany(mappedBy ="house", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<Booking> bookings = new HashSet<>();
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "roomHouse",
        joinColumns = @JoinColumn(name = "idHouse"),
        inverseJoinColumns = @JoinColumn(name = "idTyperoom"))
	private Set<TypeRoom> typeRoom = new HashSet<>();
	
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
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
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

	public Set<TypeRoom> getTypeRoom() {
		return typeRoom;
	}

	public void setTypeRoom(Set<TypeRoom> typeRoom) {
		this.typeRoom = typeRoom;
	}

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


	
	

}
