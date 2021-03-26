package ces.riccico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ces.riccico.entity.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
	Token findByToken(String token);
}
