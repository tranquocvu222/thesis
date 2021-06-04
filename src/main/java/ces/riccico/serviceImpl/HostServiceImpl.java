package ces.riccico.serviceImpl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ces.riccico.common.constants.CommonConstants;
import ces.riccico.common.constants.UserConstants;
import ces.riccico.common.constants.Validation;
import ces.riccico.common.enums.Role;
import ces.riccico.entity.Account;
import ces.riccico.entity.Host;
import ces.riccico.model.HostModel;
import ces.riccico.model.MessageModel;
import ces.riccico.repository.AccountRepository;
import ces.riccico.repository.HostRepository;
import ces.riccico.security.SecurityAuditorAware;
import ces.riccico.service.HostService;

@Service
public class HostServiceImpl implements HostService{

	private static Logger logger = LoggerFactory.getLogger(HostServiceImpl.class);

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private HostRepository hostRepository;
	
	@Autowired
	private SecurityAuditorAware securityAuditorAware;
	
	@Autowired
	private ModelMapper mapper;

	
	@Override
	public ResponseEntity<?> register(HostModel hostModel) {
		Integer idCurrent = securityAuditorAware.getCurrentAuditor().get();
		Account account = accountRepository.findById(idCurrent).get();
		MessageModel message = new MessageModel();
		if (account == null) {
			message.setMessage(UserConstants.ACCOUNT_NOT_EXISTS);
			message.setStatus(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}
//		String idTemp = null;
		String idNo = hostModel.getIdNo();
		logger.info(idNo);
//		logger.info(String.valueOf(Integer.MAX_VALUE));
//		try {
//			DecimalFormat dcf=new DecimalFormat("#.##");
//			idTemp = dcf.format(idNo);
//			logger.info(String.valueOf(idTemp));
//		} catch (NumberFormatException e) {
//			logger.error(e.getMessage());
//		}
		if(idNo.isEmpty()) {
			message.setMessage(UserConstants.IDENTIFICATION_CARD_NULL);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
		if (!idNo.matches(Validation.IDENTIFICATION_CARD)) {
			message.setMessage(UserConstants.INVALID_CARD);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
		
		if (hostRepository.findByIdNo(idNo) != null) {
			message.setMessage(UserConstants.CARD_EXISTS);
			message.setStatus(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
		
		Host host = new Host();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		Date issuedOn = null;
		try {
			issuedOn  = dateFormat.parse(hostModel.getIssuedOn());
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		host.setIdNo(idNo);
		host.setFullName(hostModel.getFullName());
		host.setIdImage(hostModel.getIdImage());
		host.setIssuedOn(issuedOn);
		host.setAccount(account);
		hostRepository.saveAndFlush(host);
		account.setRole(Role.HOST.getRole());
		accountRepository.saveAndFlush(account);
		message.setMessage(CommonConstants.SUCCESS);
		message.setStatus(HttpStatus.OK.value());
		message.setData(host);
		return ResponseEntity.ok(message);
	}
}
