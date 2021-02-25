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
@Table(name="amenities")
public class Amenities {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAmenities")
	private Integer idAmenities;
	
	@Column(name = "amenitiesName", length = 200 )
	private String amenitiesName;
	
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "idTypeAmenities", nullable = false)
	private TypeAmenities typeAmenities;
	
	@ManyToMany(mappedBy = "amenities", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JsonIgnore
	private Set<House> house = new HashSet<>();

	public Integer getIdAmenities() {
		return idAmenities;
	}

	public void setIdAmenities(Integer idAmenities) {
		this.idAmenities = idAmenities;
	}

	public String getAmenitiesName() {
		return amenitiesName;
	}

	public void setAmenitiesName(String amenitiesName) {
		this.amenitiesName = amenitiesName;
	}


	public TypeAmenities getTypeAmenities() {
		return typeAmenities;
	}

	public void setTypeAmenities(TypeAmenities typeAmenities) {
		this.typeAmenities = typeAmenities;
	}

	public Set<House> getHouse() {
		return house;
	}

	public void setHouse(Set<House> house) {
		this.house = house;
	}

	
	
	
}
