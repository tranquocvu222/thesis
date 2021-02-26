package ces.riccico.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ces.riccico.models.TypeFeature;
import ces.riccico.notification.HouseNotification;
import ces.riccico.repository.TypeFeatureReponsitory;
import ces.riccico.service.TypeFeatureService;

@Service
public class TypeFeatureServiceImpl {
//
//	@Autowired
//	TypeFeatureReponsitory typeFeatureRepository;
//
//
////	Show list TypeFeature
//	@Override
//	public List<TypeFeature> getAll() {
//		try {
//			List<TypeFeature> listTypeFeature = new ArrayList<TypeFeature>();
//			listTypeFeature = typeFeatureRepository.findAll();
//			return listTypeFeature;
//		} catch (Exception e) {
//			System.out.println("getAll: " + e);
//			return null;
//		}
//	}
//
////	Add TypeFeature
//	@Override
//	public ResponseEntity<?> createTypeFeature(TypeFeature typeFeature) {
//		try {
//			if (typeFeature.getFeaturename() == null) {
//				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HouseNotification.FeatureNull);
//			}
//			typeFeatureRepository.saveAndFlush(typeFeature);
//			return ResponseEntity.ok(AuthNotification.success);
//		} catch (Exception e) {
//			System.out.println(e);
//		}
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthNotification.fail);
//	}
//
////	Update TypeFeature
//	@Override
//	public ResponseEntity<?> updateTypeFeature(TypeFeature typeFeature) {
//		Optional<TypeFeature> feature = typeFeatureRepository.findById(typeFeature.getIdFeature());
//		try {
//			if (feature.isPresent()) {
//				feature.get().setFeaturename(typeFeature.getFeaturename());
//				typeFeatureRepository.saveAndFlush(feature.get());
//				return ResponseEntity.ok(AuthNotification.success);
//			}
//		} catch (Exception e) {
//			System.out.println(e);
//		}
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthNotification.fail);
//	}
//
////	Delete TypeFeature
//	@Override
//	public ResponseEntity<?> deleteTypeFeature(TypeFeature typeFeature) {
//		Optional<TypeFeature> feature = typeFeatureRepository.findById(typeFeature.getIdFeature());
//		try {
//			if (feature.isPresent()) {
//				typeFeatureRepository.deleteById(feature.get().getIdFeature());
//				return ResponseEntity.ok(AuthNotification.success);
//			}
//		} catch (Exception e) {
//		
//		}
//		return ResponseEntity.ok(AuthNotification.fail);
//	}

}
