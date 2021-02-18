package ces.riccico.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import ces.riccico.models.TypeFeature;

public interface TypeFeatureService {


	ResponseEntity<?> deleteTypeFeature(TypeFeature typeFeature);

	ResponseEntity<?> updateTypeFeature(TypeFeature typeFeature);

	ResponseEntity<?> createTypeFeature(TypeFeature typeFeature);

	List<TypeFeature> getAll();





}
