package ces.riccico.notification;

import org.springframework.stereotype.Component;

@Component
public class UserNotification {
	
	public static final String usernameExists = "Username has been existed!";
	public static final String emailExists = "Email has been existed!";
	public static final String registerSuccess = "Register Successfully!";
	public static final String registerFail = "Register Fail!";
	public static final String usernameNull = "Username is required!";
	public static final String passwordNull = "Password is required!";
	public static final String invalidUsernameFormat = "Username must contain more than 6 characters and less than 12, without spaces and special characters!";
	public static final String invalidPasswordFormat = "Password must contain more than 6 characters and less than 30, include uppercase character, lowcase character, digit, and special characters!";
	public static final String invalidEmailFormat = "Incorrect email format";
	public static final String invalidAccount = "Username or password is wrong";
	public static final String isBanned = "Your account is banned";
	public static final String notActivated = "Your account isn't activated";
	public static final String loginFail = "Login Fail!";
	public static final String emailNotExists = "Email isn't exist";
	public static final String emailNull = "Email is required!";

}
