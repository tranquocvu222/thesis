package ces.riccico.service;

import java.util.List;
import java.util.Optional;

import ces.riccico.models.TypeFeature;

public interface TypeFeatureService {

	List<TypeFeature> findAll();

	TypeFeature save(TypeFeature entity);

	Optional<TypeFeature> findById(Integer id);

	void deleteById(Integer id);





}
