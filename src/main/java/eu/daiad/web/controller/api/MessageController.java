package eu.daiad.web.controller.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.daiad.web.controller.BaseRestController;
import eu.daiad.web.model.AuthenticatedRequest;
import eu.daiad.web.model.EnumApplication;
import eu.daiad.web.model.RestResponse;
import eu.daiad.web.model.message.EnumMessageType;
import eu.daiad.web.model.message.MessageAcknowledgementRequest;
import eu.daiad.web.model.message.MessageRequest;
import eu.daiad.web.model.message.MessageResult;
import eu.daiad.web.model.message.MultiTypeMessageResponse;
import eu.daiad.web.model.message.SingleTypeMessageResponse;
import eu.daiad.web.model.profile.Profile;
import eu.daiad.web.model.security.AuthenticatedUser;
import eu.daiad.web.model.security.EnumRole;
import eu.daiad.web.repository.application.IProfileRepository;
import eu.daiad.web.service.message.IMessageService;

/**
 * Provides actions for loading messages and saving acknowledgments.
 */
@RestController("RestRecommendationController")
public class MessageController extends BaseRestController {

    /**
     * Logger instance for writing events using the configured logging API.
     */
    private static final Log logger = LogFactory.getLog(MessageController.class);

    /**
     * Repository for accessing profile data.
     */
    @Autowired
    private IProfileRepository profileRepository;

    /**
     * Service for accessing messages.
     */
    @Autowired
    private IMessageService service;

    /**
     * Loads messages i.e. alerts, recommendations and tips. Optionally filters messages.
     *
     * @param request the request.
     * @return the messages.
     */
    @RequestMapping(value = "/api/v1/message", method = RequestMethod.POST, produces = "application/json")
    public RestResponse getMessages(@RequestBody MessageRequest request)
    {
        try {
            AuthenticatedUser user = authenticate(request.getCredentials(), EnumRole.ROLE_USER);
            Profile profile = profileRepository.getProfileByUserKey(user.getKey(), EnumApplication.MOBILE);
            if(!profile.isSendMessageEnabled()) {
                return new MultiTypeMessageResponse();
            } else {
                MessageResult result = service.getMessages(user, request);
                return new MultiTypeMessageResponse(result);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new RestResponse(getError(ex));
        }
    }

    /**
     * Saves one or more message acknowledgments.
     *
     * @param request the messages to acknowledge.
     * @return the controller response.
     */
    @RequestMapping(value = "/api/v1/message/acknowledge", method = RequestMethod.POST, produces = "application/json")
    public RestResponse acknowledgeMessage(@RequestBody MessageAcknowledgementRequest request)
    {
        RestResponse response = new RestResponse();

        try {
            AuthenticatedUser user = authenticate(request.getCredentials(), EnumRole.ROLE_USER);
            service.acknowledgeMessages(user, request.getMessages());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.add(this.getError(ex));
        }

        return response;
    }


    /**
     * Gets localized tips
     *
     * @param request user credentials.
     * @param locale the locale
     * @return the static recommendations.
     */
    @RequestMapping(value = "/api/v1/tip/localized/{locale}", method = RequestMethod.POST, produces = "application/json")
    public RestResponse getRecommendations(@RequestBody AuthenticatedRequest request, @PathVariable String locale)
    {
        try {
            authenticate(request.getCredentials(), EnumRole.ROLE_SYSTEM_ADMIN, EnumRole.ROLE_UTILITY_ADMIN);
            SingleTypeMessageResponse messages = new SingleTypeMessageResponse();
            messages.setType(EnumMessageType.TIP);
            messages.setMessages(service.getTips(locale));
            return messages;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new RestResponse(getError(ex));
        }
    }

}
