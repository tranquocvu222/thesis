package ces.riccico.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "images")
public class Images {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idImage")
	private Integer id;
	
	@Column(name = "image", length = 100)
	private String image;
	
	@ManyToOne
	@JoinColumn(name = "idHouse")
	private House house;
	
}
