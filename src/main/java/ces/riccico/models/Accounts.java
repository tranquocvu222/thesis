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

@Entity
@Table(name = "accounts")
public class Accounts {

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAccount", length = 5)
	private String idAccount;

	@Column(name = "userName", length = 100)
	private String userName;

	@Column(name = "passWord", length = 100)
	private String passWord;

	@Column(name = "isBanded")
	private boolean isBanded;

	@ManyToOne
	@JoinColumn(name = "idRole")
	private Roles role;

	@OneToMany(mappedBy ="account", fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	private Set<House> houses = new HashSet<>();
	
	public Accounts() {
	}

	public Accounts(String idAccount, String userName, String passWord, boolean isBanded, Roles role) {
		this.idAccount = idAccount;
		this.userName = userName;
		this.passWord = passWord;
		this.isBanded = isBanded;
		this.role = role;
	}

	public String getIdAccount() {
		return idAccount;
	}

	public void setIdAccount(String idAccount) {
		this.idAccount = idAccount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public boolean isBanded() {
		return isBanded;
	}

	public void setBanded(boolean isBanded) {
		this.isBanded = isBanded;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}

	public Set<House> getHouses() {
		return houses;
	}

	public void setHouses(Set<House> houses) {
		this.houses = houses;
	}
	

}
