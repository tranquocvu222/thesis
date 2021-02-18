
package ces.riccico.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name ="typefeatures")
public class TypeFeature {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idFeature")
	private Integer idFeature;
	
	@Column(name = "featurename", length = 200 )
	private String featurename;

	public TypeFeature() {
		super();
	}

	public TypeFeature(Integer idFeature, String featurename) {
		super();
		this.idFeature = idFeature;
		this.featurename = featurename;
	}

	public Integer getIdFeature() {
		return idFeature;
	}

	public void setIdFeature(Integer idFeature) {
		this.idFeature = idFeature;
	}

	public String getFeaturename() {
		return featurename;
	}

	public void setFeaturename(String featurename) {
		this.featurename = featurename;
	}

	


}
