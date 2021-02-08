package ces.riccico.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.models.Accounts;
import ces.riccico.models.TypeFeature;
import ces.riccico.models.Users;
import ces.riccico.service.TypeFeatureService;

@CrossOrigin
@RestController
public class TypeFeatureController {

	@Autowired
	TypeFeatureService typeFeatureService;
	
//	Show list TypeFeature
	@RequestMapping(value = "/typefeature", method = RequestMethod.GET)
	public List<TypeFeature> getAll() {
		try {
			List<TypeFeature> listTypeFeature = new ArrayList<TypeFeature>();
			listTypeFeature = typeFeatureService.findAll();
			return listTypeFeature;
		} catch (Exception e) {
			System.out.println("getAll: " + e);
			return null;
		}
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String addAccount(@RequestBody TypeFeature typeFeature) {
	return null;
	}
}
