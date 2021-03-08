package ces.riccico.models;

import ces.riccico.entities.Rating;

public class RatingHouseModel {
	private Rating rating;
	
	private String username;

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
}
