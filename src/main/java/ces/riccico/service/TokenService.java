package ces.riccico.service;

import java.util.List;

import ces.riccico.models.Token;

public interface TokenService {
	
	List<Token> getAll();
	
	Token save(Token token);
	
	Token findByToken(String token);
	
	void deleteById(int idToken);
}
