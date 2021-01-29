package ces.riccico.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ces.riccico.models.House;

public interface HouseRepository extends JpaRepository<House, String>{

}
