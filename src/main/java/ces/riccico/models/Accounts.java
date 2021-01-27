package ces.riccico.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAccount", length = 500)
	private String idAccount;



	@Column(name = "username", length = 100)
	private String username;

	@Column(name = "password", length = 100)
	private String password;

	@Column( name = "isBanded")
	private boolean isBanded;

	@ManyToOne
	@JoinColumn(name = "idRole")
	private Roles role;

	public Accounts() {

	}


	public Accounts(String idAccount, String userName, String passWord, boolean isBanded, Roles role) {
		super();
		this.idAccount = idAccount;
		this.username = userName;
		this.password = passWord;
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


	@Override
	public String toString() {
		return "Accounts [idAccount=" + idAccount + ", userName=" + username + ", passWord=" + password + ", isBanded="
				+ isBanded + ", role=" + role + "]";
	}
}
