package ces.riccico.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityAuditorAware implements AuditorAware<Integer> {

	// get userId of current user
	@Override
	public Optional<Integer> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}

		if (authentication.getPrincipal() == "anonymousUser") {
			return Optional.of(0);
		}
		return Optional.of(((AccountDetail) authentication.getPrincipal()).getUserId());

	}
}
