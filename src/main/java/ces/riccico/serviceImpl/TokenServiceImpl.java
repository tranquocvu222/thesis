package ces.riccico.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ces.riccico.models.Token;
import ces.riccico.repository.TokenRepository;
import ces.riccico.service.TokenService;

@Service
public class TokenServiceImpl implements TokenService {

	@Autowired
	TokenRepository tokenRepository;
	
	@Override
	public Token save(Token token) {
		// TODO Auto-generated method stub
		return tokenRepository.saveAndFlush(token);
	}

	@Override
	public Token findByToken(String token) {
		// TODO Auto-generated method stub
		return tokenRepository.findByToken(token);
	}

}
