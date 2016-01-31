package eu.daiad.web.controller.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.daiad.web.data.IAmphiroMeasurementRepository;
import eu.daiad.web.data.IWaterMeterMeasurementRepository;
import eu.daiad.web.model.Error;
import eu.daiad.web.model.RestResponse;
import eu.daiad.web.model.amphiro.AmphiroMeasurementQuery;
import eu.daiad.web.model.amphiro.AmphiroMeasurementQueryResult;
import eu.daiad.web.model.amphiro.AmphiroSessionCollectionQuery;
import eu.daiad.web.model.amphiro.AmphiroSessionCollectionQueryResult;
import eu.daiad.web.model.amphiro.AmphiroSessionQuery;
import eu.daiad.web.model.amphiro.AmphiroSessionQueryResult;
import eu.daiad.web.model.meter.WaterMeterMeasurementQuery;
import eu.daiad.web.model.meter.WaterMeterMeasurementQueryResult;
import eu.daiad.web.model.meter.WaterMeterStatusQuery;
import eu.daiad.web.model.meter.WaterMeterStatusQueryResult;
import eu.daiad.web.model.security.AuthenticatedUser;
import eu.daiad.web.security.AuthenticationService;

@RestController
public class SearchController {

	private static final Log logger = LogFactory.getLog(SearchController.class);

	@Autowired
	private IAmphiroMeasurementRepository amphiroMeasurementRepository;

	@Autowired
	private IWaterMeterMeasurementRepository waterMeterMeasurementRepository;

	@Autowired
	private AuthenticationService authenticator;

	@RequestMapping(value = "/action/meter/status", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@Secured("ROLE_USER")
	public RestResponse query(@AuthenticationPrincipal AuthenticatedUser user, @RequestBody WaterMeterStatusQuery query) {
		try {
			query.setUserKey(user.getKey());

			WaterMeterStatusQueryResult data = waterMeterMeasurementRepository.getStatus(query);

			return data;
		} catch (Exception ex) {
			logger.error("Failed to insert measurement data.", ex);
		}
		return new RestResponse(Error.ERROR_UNKNOWN, "An unhandled exception has occurred");
	}

	@RequestMapping(value = "/action/meter/history", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@Secured("ROLE_USER")
	public RestResponse query(@AuthenticationPrincipal AuthenticatedUser user,
					@RequestBody WaterMeterMeasurementQuery query) {
		try {
			query.setUserKey(user.getKey());

			WaterMeterMeasurementQueryResult data = waterMeterMeasurementRepository.searchMeasurements(query);

			return data;
		} catch (Exception ex) {
			logger.error("Failed to insert measurement data.", ex);
		}
		return new RestResponse(Error.ERROR_UNKNOWN, "An unhandled exception has occurred");
	}

	@RequestMapping(value = "/action/device/measurement/query", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@Secured("ROLE_USER")
	public RestResponse query(@AuthenticationPrincipal AuthenticatedUser user,
					@RequestBody AmphiroMeasurementQuery query) {
		try {
			query.setUserKey(user.getKey());

			AmphiroMeasurementQueryResult data = amphiroMeasurementRepository.searchMeasurements(query);

			return data;
		} catch (Exception ex) {
			logger.error("Failed to insert measurement data.", ex);
		}
		return new RestResponse(Error.ERROR_UNKNOWN, "An unhandled exception has occurred");
	}

	@RequestMapping(value = "/action/device/session/query", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@Secured("ROLE_USER")
	public RestResponse searchAmphiroSessions(@AuthenticationPrincipal AuthenticatedUser user,
					@RequestBody AmphiroSessionCollectionQuery query) {
		try {
			query.setUserKey(user.getKey());

			AmphiroSessionCollectionQueryResult data = amphiroMeasurementRepository.searchSessions(query);

			return data;
		} catch (Exception ex) {
			logger.error("Failed to insert measurement data.", ex);
		}
		return new RestResponse(Error.ERROR_UNKNOWN, "An unhandled exception has occurred");

	}

	@RequestMapping(value = "/action/device/session", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@Secured("ROLE_USER")
	public RestResponse query(@AuthenticationPrincipal AuthenticatedUser user, @RequestBody AmphiroSessionQuery query) {
		try {
			query.setUserKey(user.getKey());

			AmphiroSessionQueryResult data = amphiroMeasurementRepository.getSession(query);

			return data;
		} catch (Exception ex) {
			logger.error("Failed to insert measurement data.", ex);
		}
		return new RestResponse(Error.ERROR_UNKNOWN, "An unhandled exception has occurred");

	}
}
