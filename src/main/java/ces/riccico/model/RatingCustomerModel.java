package ces.riccico.model;

import java.util.Date;

public class RatingCustomerModel {

	private Integer id;
	
	private Date createdAt;

	private Date modifiedDate;

	private Integer star;

	private String content;

	public RatingCustomerModel() {
		super();
	}

	public RatingCustomerModel(Integer id, Date createdAt, Date modifiedDate, Integer star, String content) {
		super();
		this.id = id;
		this.createdAt = createdAt;
		this.modifiedDate = modifiedDate;
		this.star = star;
		this.content = content;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
