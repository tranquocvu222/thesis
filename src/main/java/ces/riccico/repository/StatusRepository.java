package ces.riccico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ces.riccico.models.Status;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
	Status findByStatusName(String status);
}
