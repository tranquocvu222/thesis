package ces.riccico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ces.riccico.models.TypeFeature;

@Repository
public interface TypeFeatureReponsitory extends JpaRepository<TypeFeature, String> {

}
