package ces.riccico.repository;


import org.springframework.data.jpa.repository.JpaRepository;


import ces.riccico.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	User findByAccountId (int accountId);

}
