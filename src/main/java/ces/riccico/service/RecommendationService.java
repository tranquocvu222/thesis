package ces.riccico.service;

import java.io.IOException;

import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.http.ResponseEntity;

import ces.riccico.model.RatingModel;

public interface RecommendationService {
	
	Dataset<Row> getDataFromDatabase();
	
	Dataset<Row> getPrediction();
	
	ALSModel trainModel();
	
	double getRMSE();
	
	ResponseEntity<?> writeFileRecommendForUser() throws IOException;
	
	
}