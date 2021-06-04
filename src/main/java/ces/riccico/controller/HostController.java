package ces.riccico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import ces.riccico.model.HostModel;
import ces.riccico.service.HostService;

@CrossOrigin
@RestController
public class HostController {
	
	@Autowired
	HostService hostService;
	
	@ResponseBody
	@PostMapping("/registerHost")
	public ResponseEntity<?> registerHost(@RequestBody HostModel hostModel) {
		return hostService.register(hostModel);
	}
}
