package ces.riccico.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "hosts")
public class Host {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hostId")
	private Integer id;

	@Column(name = "fullName", length = 100)
	private String fullName;
	
	@Column(name = "idNo", length = 12)
	private String idNo;
	
	@Column(name = "issuedOn")
	private Date issuedOn;
	
	@Column(name = "idImage")
	private String idImage;

	@OneToOne
	@JsonIgnore
	@JoinColumn(name = "accountId", nullable = false)
	private Account account;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}


	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public Date getIssuedOn() {
		return issuedOn;
	}

	public void setIssuedOn(Date issuedOn) {
		this.issuedOn = issuedOn;
	}

	public String getIdImage() {
		return idImage;
	}

	public void setIdImage(String idImage) {
		this.idImage = idImage;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}
