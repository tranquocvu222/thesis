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
	@Column( name = "idRole")
	private Integer idRole;
	
	@Column( name = "rolename", length = 100)
	private String rolename;
	
	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	private Set<Accounts> idAccount;

	public Roles() {
	
	}

	public Roles(Integer idRole, String roleName) {
		
		this.idRole = idRole;
		this.rolename = roleName;
		
	}

	public Integer getIdRole() {
		return idRole;
	}

	public void setIdRole(Integer idRole) {
		this.idRole = idRole;
	}

	public String getRoleName() {
		return rolename;
	}

	public void setRoleName(String roleName) {
		this.rolename = roleName;
	}
	
	
}
