package ces.riccico.model;

import java.util.Date;

import ces.riccico.entity.Rating;

public class RatingHouseModel {

	private RatingModel rating;

	private String username;

	private Date createdAt;

	public RatingModel getRating() {
		return rating;
	}

	public void setRating(RatingModel rating) {
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
