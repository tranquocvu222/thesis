package ces.riccico.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import ces.riccico.models.Accounts;

public interface AccountRepository extends JpaRepository<Accounts, String>{
	Accounts findByUserName(String username);
}
