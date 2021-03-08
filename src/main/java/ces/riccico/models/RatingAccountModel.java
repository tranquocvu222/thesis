package ces.riccico.models;

import ces.riccico.entities.Rating;

public class RatingAccountModel {
	private Rating rating;
	
	private String houseName;

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public String getHouseName() {
		return houseName;
	}

	public void setHouseName(String houseName) {
		this.houseName = houseName;
	}
	
}
