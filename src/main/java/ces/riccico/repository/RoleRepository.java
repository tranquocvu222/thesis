package ces.riccico.repository;


import org.springframework.data.jpa.repository.JpaRepository;



import ces.riccico.models.Roles;



public interface RoleRepository extends JpaRepository<Roles, Integer> {

	Roles findByRolename (String role);
}
