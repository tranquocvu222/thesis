package ces.riccico.service;

import java.util.List;

import ces.riccico.models.TypeFeature;

public interface TypeFeatureService {

	List<TypeFeature> findAll();

	TypeFeature save(TypeFeature entity);

}
