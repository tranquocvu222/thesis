package ces.riccico.validation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ces.riccico.models.Accounts;
import ces.riccico.service.AccountService;

@Component
public class Validation {
	
	@Autowired
	AccountService accountService;
	
	Accounts account;


	// Cấu trúc 1 email thông thường
	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public static final String USERNAME_PATTERN = "^[a-z0-9._-]{6,12}$";
	
	public static final String PASSWORD_PATTERN = "((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!.#$@_+,?-]).{6,30})";
	
//	public List<Accounts>  usernameExist() {
////		return accountService.findByUsername(account.getUserName());
////	}
 
//    public  EmailValidator() {
//        Pattern pattern  =  Pattern.compile(EMAIL_PATTERN);
//    }
    
//    public boolean validate(final String hex) {	 
//        Pattern pattern = null;
//		Matcher matcher = pattern.matcher(hex);
//        return matcher.matches();
// 
//    }
    
//    public boolean validateEmail() {
//    	EmailValidator validator = new EmailValidator();
//    	return true;
//    }
}
