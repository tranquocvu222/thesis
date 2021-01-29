package ces.riccico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ces.riccico.models.Accounts;

@Repository
public interface AccountRepository extends JpaRepository<Accounts, String>{
	Accounts findByUserName(String username);
}
