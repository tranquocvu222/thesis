package ces.riccico.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ces.riccico.models.Auditable;

@Entity
@Table(name ="booking")
public class Booking extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idBooking")
	private Integer id;
	
	@Column(name = "bill")
	private double bill;
	
	
	@Column(name = "createCheckIn")
	private Date createCheckIn;

	@Column(name = "createEnd")
	private Date createEnd;
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name ="id_account", nullable = false)
	@JsonIgnore
	private Accounts account;
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "idHouse", nullable = false)
	@JsonIgnore
	private House house;
	
	private String status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public double getBill() {
		return bill;
	}

	public void setBill(double bill) {
		this.bill = bill;
	}

	public Date getCreateCheckIn() {
		return createCheckIn;
	}

	public void setCreateCheckIn(Date createCheckIn) {
		this.createCheckIn = createCheckIn;
	}

	public Date getCreateEnd() {
		return createEnd;
	}

	public void setCreateEnd(Date createEnd) {
		this.createEnd = createEnd;
	}

	public Accounts getAccount() {
		return account;
	}

	public void setAccount(Accounts account) {
		this.account = account;
	}

	public House getHouse() {
		return house;
	}

	public void setHouse(House house) {
		this.house = house;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
}
