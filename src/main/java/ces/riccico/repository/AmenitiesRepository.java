
package ces.riccico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ces.riccico.models.Amenities;

@Repository
public interface AmenitiesRepository extends JpaRepository<Amenities, Integer> {

}
