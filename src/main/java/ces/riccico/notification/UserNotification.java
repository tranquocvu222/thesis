package ces.riccico.notification;

import org.springframework.stereotype.Component;

@Component
public class UserNotification {
	public static final String usernameExists = "username already exists";
	public static final String emailExists = "email already exists";;
	public static final String usernameNull = "username is null";
	public static final String passwordNull = "password is null";
	public static final String passwordNewNull = "passwordNew is null";
	public static final String wrongOldPassword = "wrong old password";
	public static final String isMactchingOldPassword = "matching old password";
	public static final String invalidUsernameFormat = "invalid Username Format";
	public static final String invalidPasswordFormat = " invalid Password Format";
	public static final String invalidEmailFormat = "invalid email format";
	public static final String invalidAccount = "username or password is wrong";
	public static final String isBanned = "account is banned";
	public static final String notActivated = "account isn't activated";
	public static final String emailNotExists = "email isn't exist";
	public static final String usernameNotExists = "username isn't exist";
	public static final String emailNull = "email is null";
	public static final String accountNotExist = "account isn't exist";
	public static final String accountNotPermission = "don't have permission";
	public static final String firstNameNull = "firstName is null";
	public static final String lastNameNull = "lastName is null";
	public static final String birthDayNull = "birthday is null";
	public static final String addressNull = "address is null";
	public static final String cityNull = "city is null";
	public static final String countryNameNull = "country is null";
}
