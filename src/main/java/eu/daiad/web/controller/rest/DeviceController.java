package eu.daiad.web.controller.rest;

import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.daiad.web.controller.BaseRestController;
import eu.daiad.web.model.RestResponse;
import eu.daiad.web.model.device.AmphiroDeviceRegistrationRequest;
import eu.daiad.web.model.device.AmphiroDeviceRegistrationResponse;
import eu.daiad.web.model.device.Device;
import eu.daiad.web.model.device.DeviceAmphiroConfiguration;
import eu.daiad.web.model.device.DeviceConfigurationCollection;
import eu.daiad.web.model.device.DeviceConfigurationRequest;
import eu.daiad.web.model.device.DeviceConfigurationResponse;
import eu.daiad.web.model.device.DeviceRegistrationQuery;
import eu.daiad.web.model.device.DeviceRegistrationQueryResult;
import eu.daiad.web.model.device.DeviceRegistrationRequest;
import eu.daiad.web.model.device.DeviceRegistrationResponse;
import eu.daiad.web.model.device.DeviceResetRequest;
import eu.daiad.web.model.device.NotifyConfigurationRequest;
import eu.daiad.web.model.device.ShareDeviceRequest;
import eu.daiad.web.model.device.WaterMeterDeviceRegistrationRequest;
import eu.daiad.web.model.error.ApplicationException;
import eu.daiad.web.model.error.DeviceErrorCode;
import eu.daiad.web.model.security.AuthenticatedUser;
import eu.daiad.web.model.security.EnumRole;
import eu.daiad.web.repository.application.IDeviceRepository;
import eu.daiad.web.repository.application.IUserRepository;

@RestController("RestDeviceController")
public class DeviceController extends BaseRestController {

	private static final Log logger = LogFactory.getLog(DeviceController.class);

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private IDeviceRepository deviceRepository;

	@RequestMapping(value = "/api/v1/device/register", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public RestResponse registerAmphiro(@RequestBody DeviceRegistrationRequest data) {
		RestResponse response = new RestResponse();

		UUID deviceKey = null;

		try {
			AuthenticatedUser user = this.authenticate(data.getCredentials(), EnumRole.ROLE_USER);

			switch (data.getType()) {
				case AMPHIRO:
					if (data instanceof AmphiroDeviceRegistrationRequest) {
						AmphiroDeviceRegistrationRequest amphiroData = (AmphiroDeviceRegistrationRequest) data;

						Device device = deviceRepository.getUserAmphiroDeviceByMacAddress(user.getKey(),
										amphiroData.getMacAddress());

						if (device != null) {
							throw new ApplicationException(DeviceErrorCode.ALREADY_EXISTS).set("id",
											amphiroData.getMacAddress());
						}

						deviceKey = deviceRepository.createAmphiroDevice(user.getKey(), amphiroData.getName(),
										amphiroData.getMacAddress(), amphiroData.getAesKey(),
										amphiroData.getProperties());

						ArrayList<DeviceConfigurationCollection> deviceConfigurationCollection = deviceRepository
										.getConfiguration(user.getKey(), new UUID[] { deviceKey });

						AmphiroDeviceRegistrationResponse deviceResponse = new AmphiroDeviceRegistrationResponse();
						deviceResponse.setDeviceKey(deviceKey.toString());

						if (deviceConfigurationCollection.size() == 1) {
							for (DeviceAmphiroConfiguration configuration : deviceConfigurationCollection.get(0)
											.getConfigurations()) {
								deviceResponse.getConfigurations().add(configuration);
							}
						}

						return deviceResponse;
					}
					break;
				default:
					throw new ApplicationException(DeviceErrorCode.NOT_SUPPORTED)
									.set("type", data.getType().toString());
			}
		} catch (ApplicationException ex) {
			logger.error(ex.getMessage(), ex);

			response.add(this.getError(ex));
		}

		return response;
	}

	@RequestMapping(value = "/api/v1/meter/register", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public RestResponse registerMeter(@RequestBody DeviceRegistrationRequest data) {
		RestResponse response = new RestResponse();

		UUID deviceKey = null;

		try {
			AuthenticatedUser user = this.authenticate(data.getCredentials(), EnumRole.ROLE_ADMIN);

			switch (data.getType()) {
				case METER:
					if (data instanceof WaterMeterDeviceRegistrationRequest) {
						WaterMeterDeviceRegistrationRequest meterData = (WaterMeterDeviceRegistrationRequest) data;

						AuthenticatedUser owner = userRepository.getUserByUtilityAndKey(user.getUtilityId(),
										meterData.getUserKey());

						if (owner == null) {
							throw new ApplicationException(DeviceErrorCode.DEVICE_OWNER_NOT_FOUND).set("meter",
											meterData.getSerial()).set("key",
											(meterData.getUserKey() == null ? "" : meterData.getUserKey().toString()));
						}

						Device device = deviceRepository.getWaterMeterDeviceBySerial(meterData.getSerial());

						if (device != null) {
							throw new ApplicationException(DeviceErrorCode.ALREADY_EXISTS).set("id",
											meterData.getSerial());
						}

						deviceKey = deviceRepository.createMeterDevice(owner.getUsername(), meterData.getSerial(),
										meterData.getProperties(), meterData.getLocation());

						DeviceRegistrationResponse deviceResponse = new DeviceRegistrationResponse();
						deviceResponse.setDeviceKey(deviceKey.toString());

						return deviceResponse;
					}
					break;
				default:
					throw new ApplicationException(DeviceErrorCode.NOT_SUPPORTED)
									.set("type", data.getType().toString());
			}
		} catch (ApplicationException ex) {
			logger.error(ex.getMessage(), ex);

			response.add(this.getError(ex));
		}

		return response;
	}

	@RequestMapping(value = "/api/v1/device/query", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public RestResponse list(@RequestBody DeviceRegistrationQuery query) {
		RestResponse response = new RestResponse();

		try {
			AuthenticatedUser user = this.authenticate(query.getCredentials(), EnumRole.ROLE_USER);

			ArrayList<Device> devices = deviceRepository.getUserDevices(user.getKey(), query);

			DeviceRegistrationQueryResult queryResponse = new DeviceRegistrationQueryResult();
			queryResponse.setDevices(devices);

			return queryResponse;
		} catch (ApplicationException ex) {
			logger.error(ex.getMessage(), ex);

			response.add(this.getError(ex));
		}

		return response;
	}

	@RequestMapping(value = "/api/v1/device/share", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public RestResponse share(@RequestBody ShareDeviceRequest request) {
		RestResponse response = new RestResponse();

		try {
			AuthenticatedUser user = this.authenticate(request.getCredentials(), EnumRole.ROLE_USER);

			deviceRepository.shareDevice(user.getKey(), request.getAssignee(), request.getDevice(), request.isShared());
		} catch (ApplicationException ex) {
			logger.error(ex.getMessage(), ex);

			response.add(this.getError(ex));
		}

		return response;
	}

	@RequestMapping(value = "/api/v1/device/config", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public RestResponse configuration(@RequestBody DeviceConfigurationRequest request) {
		RestResponse response = new RestResponse();

		try {
			AuthenticatedUser user = this.authenticate(request.getCredentials(), EnumRole.ROLE_USER);

			DeviceConfigurationResponse configuration = new DeviceConfigurationResponse();

			configuration.setDevices(deviceRepository.getConfiguration(user.getKey(), request.getDeviceKey()));

			return configuration;
		} catch (ApplicationException ex) {
			logger.error(ex.getMessage(), ex);

			response.add(this.getError(ex));
		}

		return response;
	}

	@RequestMapping(value = "/api/v1/device/notify", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public RestResponse notify(@RequestBody NotifyConfigurationRequest request) {
		RestResponse response = new RestResponse();

		try {
			AuthenticatedUser user = this.authenticate(request.getCredentials(), EnumRole.ROLE_USER);

			deviceRepository.notifyConfiguration(user.getKey(), request.getDeviceKey(), request.getVersion(),
							request.getUpdatedOn());
		} catch (ApplicationException ex) {
			logger.error(ex.getMessage(), ex);

			response.add(this.getError(ex));
		}

		return response;
	}

	@RequestMapping(value = "/api/v1/device/reset", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public RestResponse remove(@RequestBody DeviceResetRequest request) {
		RestResponse response = new RestResponse();

		try {
			this.authenticate(request.getCredentials(), EnumRole.ROLE_ADMIN);

			this.deviceRepository.removeDevice(request.getDeviceKey());
		} catch (ApplicationException ex) {
			logger.error(ex.getMessage(), ex);

			response.add(this.getError(ex));
		}

		return response;
	}
}
