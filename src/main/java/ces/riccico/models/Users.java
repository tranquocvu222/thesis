package ces.riccico.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "users")
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idUser", length = 500)
	private Integer id;

	@Column(name = "firstname", length = 100)
	private String firstname;

	@Column(name = "lastname", length = 100)
	private String lastname;

	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	private Date birthDay;

	@Column(name = "city", length = 100)
	private String city;

	@Column(name = "country", length = 100)
	private String country;

	@Column(name = "address", length = 100)
	private String address;


	@OneToOne
	@JoinColumn(name = "idAccount",nullable = false)
	private Accounts account;

	public Users() {

	}

	public Users(Integer id, String firstName, String lastName, Date birthDay, String city, String country,
			String address, Accounts account) {
		super();
		this.id = id;
		this.firstname = firstName;
		this.lastname = lastName;
		this.birthDay = birthDay;
		this.city = city;
		this.country = country;
		this.address = address;
		this.account = account;
	}

	public Integer getIdUser() {
		return id;
	}

	public void setIdUser(Integer idUser) {
		this.id = idUser;
	}


	public String getFirstName() {
		return firstname;
	}

	public void setFirstName(String firstName) {
		this.firstname = firstName;
	}

	public String getLastName() {
		return lastname;
	}

	public void setLastName(String lastName) {
		this.lastname = lastName;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Accounts getAccount() {
		return account;
	}

	public void setAccount(Accounts account) {
		this.account = account;
	}

	@Override
	public String toString() {

		return "Users [id=" + id + ", firstName=" + firstname + ", lastName=" + lastname
				+ ", birthDay=" + birthDay + ", city=" + city + ", country=" + country + ", address=" + address
				+ ", account=" + account + "]";
	}

	
}
