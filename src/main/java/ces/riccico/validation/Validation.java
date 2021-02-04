package ces.riccico.validation;


import org.springframework.stereotype.Component;

@Component
public class Validation {
	
	// Cấu trúc 1 email thông thường
	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public static final String USERNAME_PATTERN = "^[a-z0-9._-]{6,12}$";
	
	public static final String PASSWORD_PATTERN = "((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!.#$@_+,?-]).{6,30})";
	
}
