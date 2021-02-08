package ces.riccico.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ces.riccico.models.TypeFeature;
import ces.riccico.repository.TypeFeatureReponsitory;
import ces.riccico.service.TypeFeatureService;

@Service
public class TypeFeatureServiceImpl implements TypeFeatureService {
	
	@Autowired
	TypeFeatureReponsitory typeFeatureRepository;

	@Override
	public TypeFeature save(TypeFeature entity) {
		return typeFeatureRepository.save(entity);
	}

	@Override
	public List<TypeFeature> findAll() {
		return typeFeatureRepository.findAll();
	}
	
	

}
