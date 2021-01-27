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
	@Column(name = "idAccount", length = 5)
	private int idAccount;

	@Column(name = "username", length = 200)
	private String username;

	@Column(name = "password", length = 100)
	private String password;

	@Column(name = "isBanded")
	private boolean isBanded;

	@ManyToOne
	@JoinColumn(name = "idRole")
	private Roles role;

	public Accounts() {
		super();
	}

	public Accounts(int idAccount, String username, String password, boolean isbanded, Roles role) {
		super();
		this.idAccount = idAccount;
		this.username = username;
		this.password = password;
		isBanded = isbanded;
		this.role = role;
	}

	public int getIdAccount() {
		return idAccount;
	}

	public void setIdAccount(int idAccount) {
		this.idAccount = idAccount;
	}

	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

}
