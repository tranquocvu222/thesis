package ces.riccico.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;



@Entity
@Table(name = "accounts")
public class Accounts {
	
	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column( name = "idAccount", length = 500)
	private String idAccount;
	
	@Column( name = "username", length = 100)
	private String username;
	
	@Column( name = "email", length = 200)
	private String email;
	
	@Column( name = "password", length = 100)
	private String password;
	
	@Column( name = "isBanded")
	private boolean isBanded;
	
	@Column( name = "isActive")
	private boolean isActive;

	
//	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
//    @PrimaryKeyJoinColumn
//    private Users user;
	
	@ManyToOne
	@JoinColumn(name = "idRole")
	private Roles role;

	public Accounts() {
		
	}

	public Accounts(String idAccount, String username, String email, String password, boolean isBanded,
			boolean isActive, Roles role) {
		super();
		this.idAccount = idAccount;
		this.username = username;
		this.email = email;
		this.password = password;
		this.isBanded = isBanded;
		this.isActive = isActive;
		this.role = role;
	}

	public String getIdAccount() {
		return idAccount;
	}

	public void setIdAccount(String idAccount) {
		this.idAccount = idAccount;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String userName) {
		this.username = userName;
	}

	public String getPassWord() {
		return password;
	}

	public void setPassWord(String passWord) {
		this.password = passWord;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "Accounts [idAccount=" + idAccount + ", username=" + username + ", email=" + email + ", password="
				+ password + ", isBanded=" + isBanded + ", isActive=" + isActive + ", role=" + role + "]";
	}
	
	



	
	

}
