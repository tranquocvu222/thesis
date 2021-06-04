package ces.riccico.service;

import org.springframework.http.ResponseEntity;

import ces.riccico.model.HostModel;

public interface HostService {

	ResponseEntity<?> register(HostModel hostModel);
}
