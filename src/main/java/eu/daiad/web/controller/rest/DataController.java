package eu.daiad.web.controller.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.daiad.web.controller.BaseController;
import eu.daiad.web.data.IAmphiroMeasurementRepository;
import eu.daiad.web.data.IDeviceRepository;
import eu.daiad.web.data.IWaterMeterMeasurementRepository;
import eu.daiad.web.model.DeviceMeasurementCollection;
import eu.daiad.web.model.RestResponse;
import eu.daiad.web.model.amphiro.AmphiroMeasurementCollection;
import eu.daiad.web.model.device.AmphiroDevice;
import eu.daiad.web.model.device.Device;
import eu.daiad.web.model.device.EnumDeviceType;
import eu.daiad.web.model.error.ApplicationException;
import eu.daiad.web.model.error.DeviceErrorCode;
import eu.daiad.web.model.error.SharedErrorCode;
import eu.daiad.web.model.meter.WaterMeterMeasurementCollection;
import eu.daiad.web.model.security.AuthenticatedUser;
import eu.daiad.web.security.AuthenticationService;

@RestController("RestDataController")
public class DataController extends BaseController {

	private static final Log logger = LogFactory.getLog(DataController.class);

	@Value("${tmp.folder}")
	private String temporaryPath;

	@Autowired
	private IAmphiroMeasurementRepository amphiroMeasurementRepository;

	@Autowired
	private IWaterMeterMeasurementRepository waterMeterMeasurementRepository;

	@Autowired
	private IDeviceRepository deviceRepository;

	@Autowired
	private AuthenticationService authenticator;

	@RequestMapping(value = "/api/v1/data/store", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public RestResponse store(@RequestBody DeviceMeasurementCollection data) {
		RestResponse response = new RestResponse();

		try {
			AuthenticatedUser user = this.authenticator.authenticateAndGetUser(data.getCredentials());
			if (user == null) {
				throw new ApplicationException(SharedErrorCode.AUTHENTICATION);
			}
			if (!user.hasRole("ROLE_USER")) {
				throw new ApplicationException(SharedErrorCode.AUTHORIZATION);
			}

			data.setUserKey(user.getKey());

			Device device = this.deviceRepository.getUserDeviceByKey(data.getUserKey(), data.getDeviceKey());

			if (device == null) {
				throw new ApplicationException(DeviceErrorCode.NOT_FOUND).set("key", data.getDeviceKey().toString());
			}

			switch (data.getType()) {
			case AMPHIRO:
				if (data instanceof AmphiroMeasurementCollection) {
					if (!device.getType().equals(EnumDeviceType.AMPHIRO)) {
						throw new ApplicationException(DeviceErrorCode.NOT_SUPPORTED).set("type", data.getType()
										.toString());
					}
					amphiroMeasurementRepository.storeData((AuthenticatedUser) user, (AmphiroDevice) device,
									(AmphiroMeasurementCollection) data);
				}
				break;
			case METER:
				if (data instanceof WaterMeterMeasurementCollection) {
					if (!device.getType().equals(EnumDeviceType.METER)) {
						throw new ApplicationException(DeviceErrorCode.NOT_SUPPORTED).set("type", data.getType()
										.toString());
					}
					waterMeterMeasurementRepository.storeData((WaterMeterMeasurementCollection) data);
				}
				break;
			default:
				break;
			}
		} catch (ApplicationException ex) {
			logger.error(ex);

			response.add(this.getError(ex));
		}

		return response;
	}

}
