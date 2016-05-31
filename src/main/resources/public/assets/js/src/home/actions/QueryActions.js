require('es6-promise').polyfill();

var types = require('../constants/ActionTypes');
const { CACHE_SIZE } = require('../constants/HomeConstants');

var deviceAPI = require('../api/device');
var meterAPI = require('../api/meter');

var { reduceSessions, getLastSession, getSessionIndexById, getDeviceTypeByKey, updateOrAppendToSession, getDeviceKeysByType, filterDataByDeviceKeys } = require('../utils/device');
var { getCacheKey } = require('../utils/general');


const requestedQuery = function() {
  return {
    type: types.QUERY_REQUEST_START,
  };
};

const receivedQuery = function(success, errors) {
  return {
    type: types.QUERY_REQUEST_END,
    success: success,
    errors: errors,
  };
};

const QueryActions = {
 
  queryDeviceOrMeter: function(deviceKeys, type, time) {
    return function(dispatch, getState) {
      if (!Array.isArray(deviceKeys)) throw Error('device keys ', deviceKeys, 'must be of type Array');

      if (!deviceKeys.length) return new Promise(resolve => resolve([]));
      if (!type || !(type === 'AMPHIRO' || type === 'METER')) throw new Error('type not found');

      if (type === 'AMPHIRO') {
        return dispatch(QueryActions.queryDeviceSessions(deviceKeys, {type: 'SLIDING', length: 10}))
               .catch(error => { throw error; });
      }
      else if (type === 'METER') {
        return dispatch(QueryActions.fetchMeterHistory(deviceKeys, time))
               .catch(error => { throw error; });
      }
    };
  },
  queryDeviceSessions: function(deviceKeys, options) {
    return function(dispatch, getState) {
      
      if (!deviceKeys) throw new Error(`Not sufficient data provided for device sessions query: deviceKey:${deviceKeys}`);

       
      if (getState().query.cache[getCacheKey('AMPHIRO', options.length)]) {
        console.log('found in cache!');
        dispatch(QueryActions.cacheItemRequested('AMPHIRO', options.length));
        //return new Promise(resolve => resolve(getState().query.cache[getCacheKey('AMPHIRO', options.length)].data.filter(x => deviceKeys.findIndex(k => k===x.deviceKey)>-1)));
        return new Promise(resolve => resolve(filterDataByDeviceKeys(getState().query.cache[getCacheKey('AMPHIRO', options.length)].data, deviceKeys)));
      }

      dispatch(requestedQuery());

      //fetch all items to save in cache
      const data = Object.assign({}, options, {deviceKey:getDeviceKeysByType(getState().user.profile.devices, 'AMPHIRO')}, {csrf: getState().user.csrf});

      return deviceAPI.querySessions(data)
      .then(response => {
        dispatch(receivedQuery(response.success, response.errors, response.devices) );
        
        if (!response.success) {
          throw new Error (response.errors);
        }
        dispatch(QueryActions.saveToCache('AMPHIRO', options.length, response.devices));

        //return only the items requested
        return filterDataByDeviceKeys(response.devices, deviceKeys);
        })
        .catch((error) => {
          dispatch(receivedQuery(false, error));
          throw error;
        });
    };
  },
  fetchDeviceSession: function(id, deviceKey) {
    return function(dispatch, getState) {
      
      if (!id || !deviceKey) throw new Error(`Not sufficient data provided for device session fetch: id: ${id}, deviceKey:${deviceKey}`);
      
      dispatch(requestedQuery());

      const data = Object.assign({}, {sessionId:id, deviceKey: deviceKey}, {csrf: getState().user.csrf});

      return deviceAPI.getSession(data)
        .then((response) => {
          dispatch(receivedQuery(response.success, response.errors, response.session));
          if (!response.success) {
            throw new Error (response.errors);
          }
          return response.session;
        })
        .catch((error) => {
          dispatch(receivedQuery(false, error));
          throw error;
        });
    };
  },
  fetchLastDeviceSession: function(deviceKeys) {
    return function(dispatch, getState) {
      return dispatch(QueryActions.queryDeviceSessions(deviceKeys, {type: 'SLIDING', length: 1}))
      .then(sessions => {
        
        const reduced = reduceSessions(getState().user.profile.devices, sessions);        
        //find last
        const lastSession = reduced.reduce((prev, curr) => (prev.timestamp>curr.timestamp)?prev:curr, {});
         
        const { device, id, index, timestamp } = lastSession;

        if (!id) throw new Error(`last session id doesnt exist in response: ${response}`);
        const devSessions = sessions.find(x=>x.deviceKey === device);
        
        return dispatch(QueryActions.fetchDeviceSession(id, device))
        .then(session => ({data: updateOrAppendToSession([devSessions], Object.assign({}, session, {deviceKey:device})), device, index, id, timestamp}) )
        .catch(error => { throw error; });
      });
    };
  },
  fetchMeterHistory: function(deviceKeys, time) {
    return function(dispatch, getState) {
      if (!deviceKeys || !time) throw new Error(`Not sufficient data provided for meter history query: deviceKey:${deviceKeys}, time: ${time}`);

      if (getState().query.cache[getCacheKey('METER', time)]) {
        console.log('found in cache!');
        dispatch(QueryActions.cacheItemRequested('METER', time));
        return new Promise(resolve => resolve(filterDataByDeviceKeys(getState().query.cache[getCacheKey('METER', time)].data, deviceKeys)));
      }
      dispatch(requestedQuery());

      //fetch all meters requested in order to save to cache 
      const data = Object.assign({}, time, {deviceKey:getDeviceKeysByType(getState().user.profile.devices, 'METER')}, {csrf: getState().user.csrf});

      return meterAPI.getHistory(data)
        .then((response) => {
          dispatch(receivedQuery(response.success, response.errors, response.session));
          if (!response.success) {
            throw new Error (response.errors);
          }
          dispatch(QueryActions.saveToCache('METER', time, response.series));

          //return only the meters requested  
          return filterDataByDeviceKeys(response.series, deviceKeys);
        })
        .catch((error) => {
          dispatch(receivedQuery(false, error));
          throw error;
        });
    };
  },
  fetchMeterStatus: function(deviceKeys) {
    return function(dispatch, getState) {

      if (!deviceKeys) throw new Error(`Not sufficient data provided for meter status: deviceKeys:${deviceKeys}`);

      dispatch(requestedMeterStatus());
      
      const data = {deviceKey: deviceKeys, csrf: getState().user.csrf };
      return meterAPI.getStatus(data)
        .then((response) => {
          dispatch(receivedMeterStatus(response.success, response.errors, response.devices?response.devices:[]) );
          
          if (!response.success) {
            throw new Error (response.errors);
          }
          return response;
        })
        .catch((error) => {
          dispatch(receivedQuery(false, error));
          throw error;
        });
    };
  },
  cacheItemRequested: function(deviceType, timeOrLength) {
    return {
      type: types.QUERY_CACHE_ITEM_REQUESTED,
      key:getCacheKey(deviceType, timeOrLength),
    };
  },
  setCache: function(cache) {
    return {
      type: types.QUERY_SET_CACHE,
      cache
    };
  },
  saveToCache: function(deviceType, timeOrLength, data) {
    return function(dispatch, getState) {
      const { cache } = getState().query;
      if (Object.keys(cache).length >= CACHE_SIZE) {
        console.warn('Cache limit exceeded, making space...');
        
        const newCacheKeys = Object.keys(cache)
        .sort((a, b) => cache[b].counter - cache[a].counter)
        .filter((x, i) => i < Object.keys(cache).length-1);

        let newCache = {};
        newCacheKeys.forEach(key => {
          newCache[key] = cache[key];
        });
        
        dispatch(QueryActions.setCache(newCache));
      }
      dispatch({
        type: types.QUERY_SAVE_TO_CACHE,
        key:getCacheKey(deviceType, timeOrLength),
        data
      });
    };
  }

};

module.exports = QueryActions;
