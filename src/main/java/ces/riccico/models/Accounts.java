package ces.riccico.models;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "accounts")
public class Accounts {

	@Id
	@Column(name = "idAccount", length = 500)
	private String idAccount;

	@Column(name = "isActive")
	private boolean isActive;

	@Column(name = "email", length = 200)
	private String email;

	@Column(name = "username", length = 100)
	private String username;

	@Column(name = "password", length = 100)
	private String password;

	@Column(name = "isBanned")
	private boolean isBanned;

	@ManyToOne
	@JoinColumn(name = "idRole")
	@JsonIgnore
	private Roles role;

	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<House> houses = new HashSet<>();

	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<Booking> bookings = new HashSet<>();

	public Accounts() {

	}

	public Accounts(String idAccount, String username, String email, String password, boolean isBanned,
			boolean isActive, Roles role) {
		super();
		this.idAccount = idAccount;
		this.username = username;
		this.email = email;
		this.password = password;
		this.isBanned = isBanned;
		this.isActive = isActive;
		this.role = role;
	}

	public String getIdAccount() {
		return idAccount;
	}

	public void setIdAccount(String idAccount) {
		this.idAccount = idAccount;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String userName) {
		this.username = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String passWord) {
		this.password = passWord;
	}

	public boolean isBanned() {
		return isBanned;
	}

	public void setBanned(boolean isBanned) {
		this.isBanned = isBanned;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<House> getHouses() {
		return houses;
	}

	public void setHouses(Set<House> houses) {
		this.houses = houses;
	}

	public Set<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(Set<Booking> bookings) {
		this.bookings = bookings;
	}

	@Override
	public String toString() {
		return "Accounts [idAccount=" + idAccount + ", isActive=" + isActive + ", email=" + email + ", username="
				+ username + ", password=" + password + ", isBanned=" + isBanned + ", role=" + role + ", houses="
				+ houses + "]";
	}

}
