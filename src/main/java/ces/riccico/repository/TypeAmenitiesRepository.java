 package ces.riccico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ces.riccico.models.TypeAmenities;

@Repository
public interface TypeAmenitiesRepository extends JpaRepository<TypeAmenities, Integer> {

}
