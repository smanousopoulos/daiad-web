function getDefaultDevice (devices) {
  const amphiroDevices = getAvailableDevices(devices);
  const meters = getAvailableMeters(devices);
  if (amphiroDevices.length) {
    return amphiroDevices[0];
  }
  else if (meters.length) {
    return meters[0];
  }
  else {
    return null;
  }
}

function getDeviceTypeByKey (devices, key) {
  const device = getDeviceByKey(devices, key);
  if (!device) return null;
  return device.type;
}

function getDeviceKeyByName (devices, name) {
  const device = devices.find(d => d.name === name || d.serial === name);
  if (device) return device.deviceKey;
  else return null;
}

function getDeviceCount (devices) {
  if (!devices.length) return 0;
  return getAvailableDevices(devices).length;
}

function getAvailableDevices (devices) {
  if (!devices) return [];
  return devices.filter((device) => (device.type === 'AMPHIRO'));
}

function getAvailableMeters (devices) {
  if (!devices) return [];
  return devices.filter((device) => (device.type === 'METER'));
}

function getDeviceKeysByType (devices, type) {
  let available = [];
  if (type === "AMPHIRO") available = getAvailableDevices(devices);
  else if (type === "METER") available = getAvailableMeters(devices);
  else throw new Error('device type ', type, 'not supported');

  return available.map(d=>d.deviceKey);
}

function getAvailableDeviceKeys (devices) {
  if (!devices) return [];
  return getAvailableDevices(devices).map((device) => (device.deviceKey));
}

function getDeviceByKey (devices, key) {
  //TODO: if !key added below error is thrown, why?
  //if (!devices ||!Array.isArray(devices)) throw new Error (`devices ${devices} must be of type array`);
  if (!devices ||!Array.isArray(devices)) return {};
  return devices.find((device) => (device.deviceKey === key));
}

function getDeviceNameByKey (devices, key) {
  const device = getDeviceByKey(devices, key);
  if (!device) return null;
  return device.name || device.serial || device.macAddress || device.deviceKey;
}

module.exports = {
  getDefaultDevice,
  getDeviceTypeByKey,
  getDeviceCount,
  getAvailableDevices,
  getAvailableDeviceKeys,
  getAvailableMeters,
  getDeviceNameByKey,
  getDeviceByKey,
  getDeviceKeyByName,
  getDeviceKeysByType,
};
