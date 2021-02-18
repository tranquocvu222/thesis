package ces.riccico.controller;


import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ces.riccico.models.TypeFeature;
import ces.riccico.service.TypeFeatureService;

@CrossOrigin
@RestController
public class TypeFeatureController {

	@Autowired
	TypeFeatureService typeFeatureService;

//	Show list TypeFeature
	@RequestMapping(value = "/typefeature", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('admin')")
	public List<TypeFeature> getAll() {
		return typeFeatureService.getAll();
	}
	

//	Add TypeFeature
	@RequestMapping(value = "/createTypeFeature", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> createTypeFeature(@RequestBody TypeFeature typeFeature) {
		return typeFeatureService.createTypeFeature(typeFeature);
	}
	
//	Update TypeFeature
	@RequestMapping(value = "/updateTypeFeature", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> updateTypeFeature(@RequestBody TypeFeature typeFeature) {
		return typeFeatureService.updateTypeFeature(typeFeature);
	}
	
//	Delete TypeFeature
	@RequestMapping(value = "/deleteTypeFeature", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('admin')")
	public ResponseEntity<?> deleteTypeFeature(@RequestBody TypeFeature typeFeature) {
		return typeFeatureService.deleteTypeFeature(typeFeature);

}
}
