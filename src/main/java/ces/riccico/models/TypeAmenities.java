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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="typeamenities")
public class TypeAmenities {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idTypeAmenities")
	private Integer idTypeAmenities;
	
	@Column(name = "amenitiesName")
	private String amenitiesName;
	
	@OneToMany(mappedBy ="typeAmenities", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Amenities> amenities = new HashSet<>();


	public Integer getIdTypeAmenities() {
		return idTypeAmenities;
	}

	public void setIdTypeAmenities(Integer idTypeAmenities) {
		this.idTypeAmenities = idTypeAmenities;
	}

	public String getAmenitiesName() {
		return amenitiesName;
	}

	public void setAmenitiesName(String amenitiesName) {
		this.amenitiesName = amenitiesName;
	}

	public Set<Amenities> getAmenities() {
		return amenities;
	}

	public void setAmenities(Set<Amenities> amenities) {
		this.amenities = amenities;
	}	

}
