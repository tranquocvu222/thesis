
package ces.riccico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import ces.riccico.models.TypeFeature;

public interface TypeFeatureService {

	List<TypeFeature> getAll();

	ResponseEntity<?> deleteTypeFeature(Integer idTypefeature);

	ResponseEntity<?> createTypeFeature(TypeFeature typeFeature, Integer idTyperoom);





}
