
package ces.riccico.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name ="typefeatures")
public class TypeFeature {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idFeature")
	private Integer idFeature;
	
	@Column(name = "featureName", length = 200 )
	private String featureName;
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinColumn(name = "idTyperoom", nullable = false)
	private TypeRoom typeRoom;
	
	@ManyToMany(mappedBy = "typeFeature", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JsonIgnore
	private Set<House> house = new HashSet<>();
	

	public TypeFeature() {
		super();
	}

	public TypeFeature(Integer idFeature, String featureName) {
		super();
		this.idFeature = idFeature;
		this.featureName = featureName;
	}

	public Integer getIdFeature() {
		return idFeature;
	}

	public void setIdFeature(Integer idFeature) {
		this.idFeature = idFeature;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public TypeRoom getTypeRoom() {
		return typeRoom;
	}

	public void setTypeRoom(TypeRoom typeRoom) {
		this.typeRoom = typeRoom;
	}

	public Set<House> getHouse() {
		return house;
	}

	public void setHouse(Set<House> house) {
		this.house = house;
	}



	


}
