package ces.riccico.common.enums;

public enum Role {

	ADMIN("admin"),

	USER("user"),
	
	HOST("host");

	private String role;

	Role(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
