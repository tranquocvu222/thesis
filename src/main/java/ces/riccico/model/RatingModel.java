package ces.riccico.model;

import java.io.Serializable;
import java.util.Date;

public class RatingModel  {
	
	private Date createdAt;
	
	private Date modifiedDate;

	private Integer star;
	
	private String content;
	
	private String username;
	

	public RatingModel() {
		super();
	}

	public RatingModel(Date createdAt, Date modifiedDate, Integer star, String content, String username) {
		super();
		this.createdAt = createdAt;
		this.modifiedDate = modifiedDate;
		this.star = star;
		this.content = content;
		this.username = username;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
}