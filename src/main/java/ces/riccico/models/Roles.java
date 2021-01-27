package ces.riccico.models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;



@Entity
@Table(name = "roles")
public class Roles {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column( name = "idRole", length = 5)
	private String idRole;
	
	@Column( name = "roleName", length = 100)
	private String roleName;
	
	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
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
