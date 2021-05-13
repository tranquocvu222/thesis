package ces.riccico.service;

import java.io.IOException;

import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import ces.riccico.model.RatingModel;

public interface RecommendationService  {
	
	Dataset<RatingModel> getDataFromDatabase() ;
	
	Dataset<Row> getPrediction();
	
	ALSModel trainModel();
	
	double getRMSE();
	
	void writeFileRecommendForUser() throws IOException;
	
	void stop();
	
	void init();
	
	
}