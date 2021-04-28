
package ces.riccico.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.service.RecommendationService;

@RestController
@CrossOrigin
@RequestMapping("/trainModel")
public class RecommendationController {
	@Autowired
	private RecommendationService recommendationService;

	@GetMapping
	public ResponseEntity<?> writeFileRecommendForUser() throws IOException {
		return recommendationService.writeFileRecommendForUser();
	}
}
