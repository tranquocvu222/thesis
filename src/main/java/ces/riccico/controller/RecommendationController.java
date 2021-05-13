
package ces.riccico.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ces.riccico.common.constants.CommonConstants;
import ces.riccico.service.RecommendationService;
import ces.riccico.serviceImpl.HouseServiceImpl;

@RestController
@CrossOrigin
@EnableAsync
@RequestMapping("/trainModel")
public class RecommendationController {
	
	private static Logger logger = LoggerFactory.getLogger(RecommendationController.class);
	public static final long DAY = 86400000; // 1 days
	
	@Autowired
	private RecommendationService recommendationService;

	@Async
	@Scheduled(fixedDelay = 10*DAY )
	@GetMapping
	public ResponseEntity<?> writeFileRecommendForUser() throws IOException {
		Path path = Paths.get(CommonConstants.FILE_RECOMMEND);
		if(!Files.exists(path)) { 
			recommendationService.init();
			logger.info("train new model ");
			recommendationService.writeFileRecommendForUser();
			recommendationService.stop();
			return ResponseEntity.ok("train new model successly");
		}
		recommendationService.init();
		long countDb = recommendationService.countRowDbNew();
		long countFile = recommendationService.countRowDbOld();
//		long countFile = 3000;
		if(countDb > (countFile + (countFile*10)/100)) {
			logger.info("retrain model ");
			recommendationService.writeFileRecommendForUser();
			recommendationService.stop();
			return ResponseEntity.ok("retrain model successly");
			
		}
		recommendationService.stop();
		logger.info("Model existed");
		return ResponseEntity.ok("Model existed");
	}
	
//	@GetMapping("/count")
//	public long countRowModel() throws IOException {
//		return recommendationService.countRowDbOld();
//	}
//	
}
