
package ces.riccico.common.constants;

import org.springframework.stereotype.Component;

@Component
public class Validation {

	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public static final String PASSWORD_PATTERN = "((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!.#$@_+,?-]).{6,30})";

	public static final String USERNAME_PATTERN = "^[a-z0-9._-]{4,40}$";
	//(^\\w{3}[0-9]{6}$)|(^\\w{1,2}[0-9]{7}$)|(^\\d{9}$)|(^\\d{12}$)
	public static final String IDENTIFICATION_CARD = "(^\\w{3}[0-9]{6}$)|(^\\w{1,2}[0-9]{7}$)|(^\\d{9}$)|(^\\d{12}$)"; 
	//[0-9]{9}

}
