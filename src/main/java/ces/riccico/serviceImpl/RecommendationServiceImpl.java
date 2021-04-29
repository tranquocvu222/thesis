package ces.riccico.serviceImpl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.common.constants.CommonConstants;
import ces.riccico.model.MessageModel;
import ces.riccico.model.RatingModel;
import ces.riccico.service.RecommendationService;

@Service
public class RecommendationServiceImpl implements RecommendationService {
	private static Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);
	private static final String SPARK_APP_NAME = "Recommendation Engine";
	private static final String SPARK_MASTER = "local";
	private static final String DATASOURCE_URL = "jdbc:postgresql://ec2-3-216-92-193.compute-1.amazonaws.com:5432/df98ln5fvc2egi";
	private static final String DATASOURCE_TABLE_RATING = "public.ratings";
	private static final String DATASOURCE_TABLE_BOOKING = "public.bookings";
	private static final String DATASOURCE_USERNAME = "qaesnrejlgjifq";
	private static final String DATASOURCE_PASSWORD = "c8c9b1b19a20814b1b21df019b2d452542ef16b18219822013b0c17d383b9e4c";
	private static SparkSession SPARK_SESSION ;

	// parametter of model to training
	private static final double REG_PARAM = 0.2;
//	private static final int RANK = 10;
	private static final int MAX_ITER = 5;
	private static final String COLD_STAR = "drop";

	// column of model
	private static final String ACCOUNT_ID = "account_id";
	private static final String HOUSE_ID = "house_id";
	private static final String BOOKING_ID = "booking_id";
	private static final String STAR = "star";

	// test set
	private static Dataset<RatingModel> TEST;
	
	//dataset rating from database
	private static Dataset<Row> ratingDb ;
	
	//dataset booking from database
	private static Dataset<Row> bookingDb ;
	@Override
	public void init() {
		SPARK_SESSION = SparkSession.builder().master(SPARK_MASTER).appName(SPARK_APP_NAME)
				.getOrCreate();

		ratingDb = SPARK_SESSION.read().format("jdbc").option("url", DATASOURCE_URL)
				.option("dbtable", DATASOURCE_TABLE_RATING).option("user", DATASOURCE_USERNAME)
				.option("password", DATASOURCE_PASSWORD).load().select(STAR, BOOKING_ID);
		 bookingDb = SPARK_SESSION.read().format("jdbc").option("url", DATASOURCE_URL)
					.option("dbtable", DATASOURCE_TABLE_BOOKING).option("user", DATASOURCE_USERNAME)
					.option("password", DATASOURCE_PASSWORD).load().select(ACCOUNT_ID, HOUSE_ID, BOOKING_ID);
	}
	@Override
	public Dataset<RatingModel> getDataFromDatabase() {
		System.setProperty("hadoop.home.dir", "D:\\hadoop\\");
		System.setProperty("hadoop.tmp.dir", "D:\\hadoop\\tmp");
		//combine dataset from ratingDb and bookingDb
		Dataset<Row> ratingsDb = ratingDb.join(bookingDb, BOOKING_ID).select(ACCOUNT_ID, HOUSE_ID, STAR);
		Dataset<RatingModel> ratings = ratingsDb.as(Encoders.bean(RatingModel.class));
//		ratings.show();
		return ratings;
	}

	@Override
	public ALSModel trainModel() {
		Dataset<RatingModel> ratings = getDataFromDatabase();
		// divide to 2 set training and test
		Dataset<RatingModel>[] splits = ratings.randomSplit(new double[] { 0.8, 0.2 });
		Dataset<RatingModel> training = splits[0];
		TEST = splits[1];
		ALS als = new ALS().setMaxIter(MAX_ITER).setRegParam(REG_PARAM).setUserCol(ACCOUNT_ID)
				.setItemCol(HOUSE_ID).setRatingCol(STAR).setColdStartStrategy(COLD_STAR).setImplicitPrefs(false)
				.setNonnegative(true);
		ALSModel model = als.fit(training);
		return model;
	}

	@Override
	public Dataset<Row> getPrediction() {
		ALSModel model = trainModel();
		Dataset<Row> predictions = model.transform(TEST);
		predictions.show();
		return predictions;
	}
	
	@Override
	public long countRowDbNew()  {
		long count = -1;
		count = ratingDb.count();
		return count;
	}
	
	@Override
	public long countRowDbOld() throws IOException {
		BufferedReader bufReader  = new BufferedReader(new FileReader(CommonConstants.FILE_COUNT_ROW));
		long count = 0;
		String lineInFile = bufReader.readLine();
		while (lineInFile != null) {
			count = Integer.parseInt(lineInFile);
			break;
		}
		bufReader.close();
		return count;
	}

	@Override
	public double getRMSE() {
		Dataset<Row> predictions = getPrediction();
		RegressionEvaluator evaluator = new RegressionEvaluator().setMetricName("rmse").setLabelCol(STAR)
				.setPredictionCol("prediction");
		double rmse = evaluator.evaluate(predictions);
		return rmse;
	}

	@Override
	public void writeFileRecommendForUser() throws IOException {
		MessageModel message = new MessageModel();
		ALSModel model = trainModel();
		Dataset<Row> userRecs = model.recommendForAllUsers(50);
		userRecs.show();
		List userReccommends = userRecs.select(ACCOUNT_ID, "recommendations.house_id").collectAsList();

		List<String> listOj = new ArrayList<String>();
		for (Object o : userReccommends) {
			String oj = o.toString();
			listOj.add(oj);
		}
		for (int i = 0; i < listOj.size(); i++) {
			listOj.set(i, listOj.get(i).replace("WrappedArray(", "").replace("[", "").replace("]", "").replace("(", "")
					.replace(")", ""));
		}
		FileWriter writer = new FileWriter(CommonConstants.FILE_RECOMMEND, false);
		for (String str : listOj) {
			writer.write(str + System.lineSeparator());
		}
		writer.close();
		
		FileWriter writerCount = new FileWriter(CommonConstants.FILE_COUNT_ROW, false);
		ratingDb.cache();
		writerCount.write(String.valueOf(ratingDb.count()));
		logger.info("count" + String.valueOf(ratingDb.count()));
		writerCount.close();
		message.setMessage(CommonConstants.SUCCESS);
	}

	@Override
	public void stop() {
		SPARK_SESSION.stop();
	}

}