package ces.riccico.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.http.ResponseEntity;

import ces.riccico.model.RatingModel;

public interface RecommendationService  {
	
	Dataset<RatingModel> getDataFromDatabase() ;
	
	Dataset<Row> getPrediction();
	
	ALSModel trainModel();
	
	long countRowDbNew();
	
	long countRowDbOld() throws IOException ;
	
	double getRMSE();
	
	void writeFileRecommendForUser() throws IOException;
	
	void stop();
	
	void init();
	
	
}