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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity
@Table(name = "typerooms")
public class TypeRoom {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idTyperoom")
	private Integer idTyperoom;
	
	@Column(name = "roomName")
	private String roomName;
	
	
	@ManyToMany(mappedBy = "typeRoom", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JsonIgnore
	private Set<House> house = new HashSet<>();
	
	@OneToMany(mappedBy ="typeRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<TypeFeature> typeFeature = new HashSet<>();


	public Set<House> getHouse() {
		return house;
	}

	public void setHouse(Set<House> house) {
		this.house = house;
	}

	public Integer getIdTyperoom() {
		return idTyperoom;
	}

	public void setIdTyperoom(Integer idTyperoom) {
		this.idTyperoom = idTyperoom;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}


	
	


	
	
}
