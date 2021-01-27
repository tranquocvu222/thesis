package ces.riccico.models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;



@Entity
@Table(name = "role")
public class Roles {

	@Id
	@Column( name = "idRole", length = 5)
	private String idRole;
	
	@Column( name = "roleName", length = 100)
	private String roleName;
	
	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
	private Set<Accounts> idAccount;

	public Roles() {
	
	}

	public Roles(String idRole, String roleName) {
		this.idRole = idRole;
		this.roleName = roleName;
	}

	public String getIdRole() {
		return idRole;
	}

	public void setIdRole(String idRole) {
		this.idRole = idRole;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	
}
