package ces.riccico.service;

import ces.riccico.models.Token;

public interface TokenService {
	Token save(Token token);
	Token findByToken(String token);
}
