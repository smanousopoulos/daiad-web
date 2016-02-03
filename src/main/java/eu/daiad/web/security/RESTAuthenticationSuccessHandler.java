package eu.daiad.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.daiad.web.data.IProfileRepository;
import eu.daiad.web.model.AuthenticationResponse;
import eu.daiad.web.model.CsrfConstants;
import eu.daiad.web.model.profile.Profile;
import eu.daiad.web.model.security.AuthenticatedUser;
import eu.daiad.web.util.AjaxUtils;

@Component
public class RESTAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private IProfileRepository profileRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
		clearAuthenticationAttributes(request);

		if (AjaxUtils.isAjaxRequest(request)) {
			if (response.isCommitted()) {
				logger.debug("Response has already been committed. Unable to send JSON response.");
				return;
			}
			try {

				Authentication auth = SecurityContextHolder.getContext().getAuthentication();

				AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();

				Profile profile = profileRepository.getProfileByUsername(user.getUsername());

				AuthenticationResponse authenticationResponse = new AuthenticationResponse(profile);

				CsrfToken sessionToken = (CsrfToken) request.getSession().getAttribute(
								CsrfConstants.DEFAULT_CSRF_TOKEN_ATTR_NAME);
				CsrfToken requestToken = (CsrfToken) request.getAttribute(CsrfConstants.REQUEST_ATTRIBUTE_NAME);

				CsrfToken token = (sessionToken == null ? requestToken : sessionToken);

				if (token != null) {
					response.setHeader(CsrfConstants.RESPONSE_HEADER_NAME, token.getHeaderName());
					response.setHeader(CsrfConstants.RESPONSE_PARAM_NAME, token.getParameterName());
					response.setHeader(CsrfConstants.RESPONSE_TOKEN_NAME, token.getToken());
				}

				response.setContentType("application/json;charset=UTF-8");
				response.setHeader("Cache-Control", "no-cache");
				response.setStatus(HttpStatus.OK.value());

				ObjectMapper mapper = new ObjectMapper();
				response.getWriter().print(mapper.writeValueAsString(authenticationResponse));
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}

		} else {
			super.onAuthenticationSuccess(request, response, authentication);
		}
	}
}