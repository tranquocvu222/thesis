package ces.riccico.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;



@Entity
@Table(name = "accounts")
public class Accounts {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column( name = "idAccount", length = 5)
	private String idAccount;
	
	@Column( name = "Username", length = 200)
	private String Username;
	
	@Column( name = "Password", length = 100)
	private String Password;
	
	@Column( name = "isBanded")
	private boolean Isbanded;
	
	@ManyToOne
	@JoinColumn(name = "idRole")
	private Roles role;

	public Accounts() {
		super();
	}

	public Accounts(String idAccount, String username, String password, boolean isbanded, Roles role) {
		super();
		this.idAccount = idAccount;
		Username = username;
		Password = password;
		Isbanded = isbanded;
		this.role = role;
	}

	public String getIdAccount() {
		return idAccount;
	}

	public void setIdAccount(String idAccount) {
		this.idAccount = idAccount;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public boolean isIsbanded() {
		return Isbanded;
	}

	public void setIsbanded(boolean isbanded) {
		Isbanded = isbanded;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}
	
}
