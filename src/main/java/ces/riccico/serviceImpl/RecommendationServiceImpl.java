package ces.riccico.serviceImpl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import ces.riccico.model.RatingModel;
import ces.riccico.service.RecommendationService;

@Service
public class RecommendationServiceImpl implements RecommendationService {
	private static Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);
	private static final String SPARK_APP_NAME = "Recommendation Engine";
	private static final String SPARK_MASTER = "local";
	private static final String DATASOURCE_URL = "jdbc:postgresql://localhost/login";
	private static final String DATASOURCE_TABLE_RATING = "public.ratings";
	private static final String DATASOURCE_TABLE_BOOKING = "public.bookings";
	private static final String DATASOURCE_USERNAME = "postgres";
	private static final String DATASOURCE_PASSWORD = "123456";
	private final SparkSession SPARK_SESSION = SparkSession.builder().master(SPARK_MASTER).appName(SPARK_APP_NAME)
			.getOrCreate();

	// parametter of model to training
	private static final double REG_PARAM = 0.15;
	private static final int RANK = 20;
	private static final int MAX_ITER = 20;
	private static final String COLD_STAR = "drop";

	// column of model
	private static final String ACCOUNT_ID = "account_id";
	private static final String HOUSE_ID = "house_id";
	private static final String BOOKING_ID = "booking_id";
	private static final String STAR = "star";

	// test set
	private static Dataset<Row> TEST;

	@Override
	public Dataset<Row> getDataFromDatabase() {
		System.setProperty("hadoop.home.dir", "D:\\hadoop\\");
		System.setProperty("hadoop.tmp.dir", "D:\\hadoop\\tmp");
		Dataset<Row> ratingDb = SPARK_SESSION.read().format("jdbc").option("url", DATASOURCE_URL)
				.option("dbtable", DATASOURCE_TABLE_RATING).option("user", DATASOURCE_USERNAME)
				.option("password", DATASOURCE_PASSWORD).load().select(STAR, BOOKING_ID);
		
		Dataset<Row> bookingDb = SPARK_SESSION.read().format("jdbc").option("url", DATASOURCE_URL)
				.option("dbtable", DATASOURCE_TABLE_BOOKING).option("user", DATASOURCE_USERNAME)
				.option("password", DATASOURCE_PASSWORD).load().select(ACCOUNT_ID, HOUSE_ID, BOOKING_ID);
		
		//combine data
		Dataset<Row> ratingsDb = ratingDb.join(bookingDb, BOOKING_ID).select(ACCOUNT_ID, HOUSE_ID, STAR);
//		Dataset<RatingModel> ratings = ratingsDb.as(Encoders.bean(RatingModel.class));
//		ratings.show();
		return ratingsDb;
	}

	@Override
	public ALSModel trainModel() {
		Dataset<Row> ratings = getDataFromDatabase();
		// divide to 2 set training and test
		Dataset<Row>[] splits = ratings.randomSplit(new double[] { 0.8, 0.2 });
		Dataset<Row> training = splits[0];
		TEST = splits[1];
		ALS als = new ALS().setMaxIter(MAX_ITER).setRegParam(REG_PARAM).setRank(RANK).setUserCol(ACCOUNT_ID)
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
	public double getRMSE() {
		Dataset<Row> predictions = getPrediction();
		RegressionEvaluator evaluator = new RegressionEvaluator().setMetricName("rmse").setLabelCol(STAR)
				.setPredictionCol("prediction");
		double rmse = evaluator.evaluate(predictions);
		return rmse;
	}

	@Override
	public ResponseEntity<?> writeFileRecommendForUser() throws IOException {
		ALSModel model = trainModel();
		Dataset<Row> userRecs = model.recommendForAllUsers(15);
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
			logger.info(listOj.get(i));
		}
		FileWriter writer = new FileWriter("output2.txt", false);
		for (String str : listOj) {
			writer.write(str + System.lineSeparator());
		}
		SPARK_SESSION.stop();
		return ResponseEntity.ok(userReccommends);
	}

}