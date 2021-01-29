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
	
	@Column( name = "userName", length = 100)
	private String userName;
	
	@Column( name = "passWord", length = 100)
	private String passWord;
	
	@Column( name = "isBanded")
	private boolean isBanded;
	
//	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
//    @PrimaryKeyJoinColumn
//    private Users user;
	
	@ManyToOne
	@JoinColumn(name = "idRole")
	private Roles role;

	public Accounts() {
		
	}

	public Accounts(String idAccount, String userName, String passWord, boolean isBanded, Roles role) {
		super();
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

	@Override
	public String toString() {
		return "Accounts [idAccount=" + idAccount + ", userName=" + userName + ", passWord=" + passWord + ", isBanded="
				+ isBanded + ", role=" + role + "]";
	}

	

}
