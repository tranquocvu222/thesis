package ces.riccico.models;

import java.util.Date;

import ces.riccico.entities.Rating;

public class RatingAccountModel {

	private Rating rating;

	private String houseName;

	private Date createdAt;

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

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
