package ces.riccico.model;

import java.util.Date;

public class RatingCustomerModel {

	private Date createdAt;

	private Date modifiedDate;

	private Integer star;

	private String content;

	public RatingCustomerModel() {
		super();
	}

	public RatingCustomerModel(Date createdAt, Date modifiedDate, Integer star, String content) {
		super();
		this.createdAt = createdAt;
		this.modifiedDate = modifiedDate;
		this.star = star;
		this.content = content;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Integer getStar() {
		return star;
	}

	public void setStar(Integer star) {
		this.star = star;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
