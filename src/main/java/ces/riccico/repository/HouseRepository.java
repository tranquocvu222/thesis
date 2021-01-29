package ces.riccico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ces.riccico.models.House;

@Repository
public interface HouseRepository extends JpaRepository<House, String>{

}
