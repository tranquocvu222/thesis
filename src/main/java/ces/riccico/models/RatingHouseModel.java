package ces.riccico.models;

import java.util.Date;

import ces.riccico.entities.Rating;

public class RatingHouseModel {

	private Rating rating;

	private String username;

	private Date createdAt;

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

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
