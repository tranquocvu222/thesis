package ces.riccico.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name ="typeFeature")
public class TypeFeature {

	@Id
	@Column(name = "id", length = 500)
	private String idAccount;
	
	@Column(name = "featureName", length = 200 )
	private String featureName;

	public TypeFeature() {
		super();
	}

	public TypeFeature(String idAccount, String featureName) {
		super();
		this.idAccount = idAccount;
		this.featureName = featureName;
	}

	public String getIdAccount() {
		return idAccount;
	}

	public void setIdAccount(String idAccount) {
		this.idAccount = idAccount;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	@Override
	public String toString() {
		return "TypeFeature [idAccount=" + idAccount + ", featureName=" + featureName + "]";
	}
	
	
	
}
