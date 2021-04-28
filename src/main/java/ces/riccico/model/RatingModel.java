package ces.riccico.model;

import java.io.Serializable;

public class RatingModel implements Serializable {

	private int account_id;
	private int house_id;
	private float star;

	public RatingModel() {
	}

	public RatingModel(int account_id, int house_id, float star) {
		super();
		this.account_id = account_id;
		this.house_id = house_id;
		this.star = star;
	}



	public int getAccount_id() {
		return account_id;
	}

	public void setAccount_id(int account_id) {
		this.account_id = account_id;
	}

	public int getHouse_id() {
		return house_id;
	}

	public void setHouse_id(int house_id) {
		this.house_id = house_id;
	}

	public float getStar() {
		return star;
	}

	public void setStar(float star) {
		this.star = star;
	}

}