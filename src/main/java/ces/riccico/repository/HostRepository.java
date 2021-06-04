package ces.riccico.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ces.riccico.entity.Host;

public interface HostRepository extends JpaRepository<Host, Integer> {
	
	Host findByIdNo (String idNo);
}
