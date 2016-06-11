var mirrorToPath = require('../helpers/path-mirror.js');

var types = mirrorToPath({
  
  LOCALE_CHANGE : null,
  LOCALE_REQUEST_MESSAGES: null,
  LOCALE_RECEIVED_MESSAGES: null,
  
  USER_REQUESTED_LOGIN: null,
  USER_RECEIVED_LOGIN: null,
  USER_REQUESTED_LOGOUT: null,
  USER_RECEIVED_LOGOUT: null,
  USER_PROFILE_REFRESH : null,
  USER_PROFILE_UPDATE: null,
  
  MODEMNG_FILTER_ADD: null,
  MODEMNG_FILTER_REMOVE: null,
  MODEMNG_SET_NAME_FILTER: null,
  MODEMNG_SET_MODAL: null,
  MODEMNG_SET_MODES: null,
  MODEMNG_SET_ACTIVE_PAGE: null,
  MODEMNG_REQUEST_FILTER_OPTIONS: null,
  MODEMNG_RECEIVED_FILTER_OPTIONS: null,
  MODEMNG_REQUEST_USERS: null,
  MODEMNG_RECEIVED_USERS: null,
  MODEMNG_SET_CHANGED_MODES: null,
  MODEMNG_SAVE_MODE_CHANGES: null,
  MODEMNG_MARK_USER_DEACTIVATION: null,
  MODEMNG_DEACTIVATE_USER: null,
  
  ADMIN_REQUESTED_ACTIVITY: null,
  ADMIN_RECEIVED_ACTIVITY: null,
  
  ADMIN_RESET_USER_DATA: null,
  
  ADMIN_REQUESTED_SESSIONS: null,
  ADMIN_RECEIVED_SESSIONS: null,
  
  ADMIN_REQUESTED_METERS: null,
  ADMIN_RECEIVED_METERS: null,
  
  ADMIN_FILTER_USER: null,

  ADMIN_EXPORT_REQUEST: null,
  ADMIN_EXPORT_COMPLETE: null,
  
  ADMIN_ADD_USER_SHOW: null,
  ADMIN_ADD_USER_HIDE: null,
  ADMIN_ADD_USER_SELECT_GENDER_MALE: null,
  ADMIN_ADD_USER_SELECT_GENDER_FEMALE: null,
  ADMIN_ADD_USER_SELECT_UTILITY: null,
  ADMIN_ADD_USER_FILL_FORM: null,
  ADMIN_ADD_USER_VALIDATION_ERRORS_OCCURRED: null,
  ADMIN_ADD_USER_SHOW_MESSAGE_ALERT: null,
  ADMIN_ADD_USER_HIDE_MESSAGE_ALERT: null,
  ADMIN_ADD_USER_MAKE_REQUEST: null,
  ADMIN_ADD_USER_RECEIVE_RESPONSE: null,
  ADMIN_ADD_USER_GET_UTILITIES_MAKE_REQUEST: null,
  ADMIN_ADD_USER_GET_UTILITIES_RECEIVE_RESPONSE: null,
  
    
  DEMOGRAPHICS_REQUEST_GROUPS_AND_FAVOURITES: null,
  DEMOGRAPHICS_RECEIVE_GROUPS: null,
  DEMOGRAPHICS_SET_GROUPS_FILTER: null,
  DEMOGRAPHICS_RECEIVE_FAVOURITES: null,
  DEMOGRAPHICS_SET_FAVOURITES_FILTER: null,
  DEMOGRAPHICS_SHOW_NEW_GROUP_FORM: null,
  DEMOGRAPHICS_HIDE_NEW_GROUP_FORM: null,
  DEMOGRAPHICS_RECEIVE_NEW_GROUP_POSSIBLE_MEMBERS: null,
  DEMOGRAPHICS_REQUEST_GROUP_MEMBERS: null,
  DEMOGRAPHICS_RECEIVE_GROUP_MEMBERS: null,
  DEMOGRAPHICS_TOGGLE_CANDIDATE_GROUP_MEMBER_TO_ADD: null,
  DEMOGRAPHICS_TOGGLE_CANDIDATE_GROUP_MEMBER_TO_REMOVE: null,
  DEMOGRAPHICS_ADD_SELECTED_GROUP_MEMBERS: null,
  DEMOGRAPHICS_REMOVE_SELECTED_GROUP_MEMBERS: null,
  DEMOGRAPHICS_CREATE_GROUP_SET_NAME: null,
  DEMOGRAPHICS_CREATE_GROUP_VALIDATION_ERRORS_OCCURRED: null,
  DEMOGRAPHICS_CREATE_GROUP_HIDE_MESSAGE_ALERT: null,
  DEMOGRAPHICS_CREATE_GROUP_SET_MAKE_REQUEST: null,
  DEMOGRAPHICS_CREATE_GROUP_SET_RECEIVE_RESPONSE: null,
  DEMOGRAPHICS_HIDE_FAVOURITE_GROUP_FORM: null,
  DEMOGRAPHICS_RESET_COMPONENT: null,
  DEMOGRAPHICS_SHOW_FAVOURITE_GROUP_FORM: null,
  DEMOGRAPHICS_DELETE_GROUP_REQUEST_MADE: null,
  DEMOGRAPHICS_DELETE_GROUP_RESPONSE_RECEIVED: null,
  DEMOGRAPHICS_SHOW_MODAL: null,
  DEMOGRAPHICS_HIDE_MODAL: null,
  DEMOGRAPHICS_DELETE_FAVOURITE_REQUEST_MADE: null,
  DEMOGRAPHICS_DELETE_FAVOURITE_RESPONSE_RECEIVED: null,
  DEMOGRAPHICS_HIDE_MESSAGE_ALERT: null,
  
  UPSERT_FAVOURITE_FORM_ACCOUNT_INFO_REQUEST: null,
  UPSERT_FAVOURITE_FORM_ACCOUNT_INFO_RESPONSE: null,
  UPSERT_FAVOURITE_FORM_GROUP_INFO_REQUEST: null,
  UPSERT_FAVOURITE_FORM_GROUP_INFO_RESPONSE: null,
  UPSERT_FAVOURITE_FORM_SET_FAVOURITE_LABEL: null,
  UPSERT_FAVOURITE_FORM_UPSERT_FAVOURITE_REQUEST: null,
  UPSERT_FAVOURITE_FORM_UPSERT_FAVOURITE_RESPONSE: null,
  UPSERT_FAVOURITE_FORM_VALIDATION_ERRORS_OCCURRED: null,
  UPSERT_FAVOURITE_FORM_HIDE_MESSAGE_ALERT: null,
  
  GROUP_REQUEST_GROUP: null,
  GROUP_RECEIVE_GROUP_INFO: null,
  GROUP_RECEIVE_GROUP_MEMBERS: null,
  GROUP_SHOW_FAVOURITE_GROUP_FORM: null,
  GROUP_HIDE_FAVOURITE_GROUP_FORM: null,
  GROUP_RESET_COMPONENT: null,
  GROUP_SHOW_FAVOURITE_ACCOUNT_FORM: null,
  GROUP_HIDE_FAVOURITE_ACCOUNT_FORM: null,

  // Client Configuration

  config: {
    utility: {
      REQUEST_CONFIGURATION: null,
      SET_CONFIGURATION: null,
    },
    reports: {
      SET_CONFIGURATION: null,
    },
  },

  // Reports

  reports: {
    
    // Reports on measurements 
    
    measurements: {
      INITIALIZE: null,
      SET_SOURCE: null,
      SET_TIMESPAN: null,
      SET_POPULATION: null,
      REQUEST_DATA: null,
      SET_DATA: null,
    },

    // Reports on system utilization

    system: {
      INITIALIZE: null, 
      REQUEST_DATA: null,
      SET_DATA: null,
    },
  },
  
  overview: {
    SET_REFERENCE_TIME: null,
  },

  // Alerts

 
  USER_REQUEST_USER: null,
  USER_RECEIVE_USER_INFO: null,
  USER_RECEIVE_GROUP_MEMBERSHIP_INFO: null,
  USER_SHOW_FAVOURITE_ACCOUNT_FORM: null,
  USER_HIDE_FAVOURITE_ACCOUNT_FORM: null,
        
  //manage alerts
  ADMIN_REQUESTED_UTILITIES: null,
  ADMIN_RECEIVED_UTILITIES: null,        
  ADMIN_SELECTED_UTILITY_FILTER: null,      
  ADMIN_REQUESTED_STATIC_TIPS: null,
  ADMIN_RECEIVED_STATIC_TIPS: null,        
  SAVE_BUTTON_DISABLED: null,
  SAVE_BUTTON_CLICKED: null,
  ADMIN_CLICKED_SAVE_BUTTON: null,
  ADMIN_SAVE_BUTTON_RESPONSE: null,
  ADMIN_SAVED_ACTIVE_TIPS: null,
  CHECKBOX_CLICKED: null,
  ADMIN_REQUESTED_ADD_TIP: null,
  ADMIN_ADD_TIP_RESPONSE: null,
  ADMIN_ADD_TIP_SHOW: null,
  ADMIN_CANCEL_ADD_TIP_SHOW: null,
  ADMIN_EDIT_TIP: null,
  STATIC_TIPS_ACTIVE_PAGE: null,
  ADMIN_TIPS_ACTIVE_STATUS_CHANGE: null,
  ADMIN_EDITED_TIP: null,
  ADMIN_TIPS_SAVE_BUTTON_DISABLED: null,
  ADMIN_CHECKBOX_CLICKED: null,
  ADMIN_DELETE_TIP: null,
  ADMIN_DELETE_TIP_REQUEST: null,
  ADMIN_DELETE_TIP_RESPONSE: null,
  MESSAGES_DELETE_MODAL_SHOW: null,
  MESSAGES_DELETE_MODAL_HIDE: null,
  
  //Announcements
  ANNC_REQUESTED_USERS: null,
  ANNC_RECEIVED_USERS: null,
  ANNC_SET_INITIAL_USERS: null,
  ANNC_INITIAL_USERS_SET_SELECTED: null,
  ANNC_ADDED_USERS_SET_SELECTED: null,
  ANNC_ADD_USERS_BUTTON_CLICKED: null,
  ANNC_REMOVE_USERS_BUTTON_CLICKED: null,
  ANNC_SHOW_FORM: null,
  ANNC_CANCEL_SHOW_FORM: null,
  ANNC_BROADCAST_ANNOUNCEMENT_REQUEST: null,
  ANNC_BROADCAST_ANNOUNCEMENT_RESPONSE: null,
  ANNC_REQUESTED_ANNOUNCEMENT_HISTORY: null,
  ANNC_RECEIVED_ANNOUNCEMENT_HISTORY: null,
  ANNC_FILTER_USERS: null,
  //
  
  QUERY_SUBMIT: null,
  QUERY_RESPONSE: null,

  DEBUG_CREATE_USER: null,
  DEBUG_USER_CREATED: null,
  DEBUG_CREATE_AMPHIRO: null,
  DEBUG_AMPHIRO_CREATED: null,
  DEBUG_SET_TIMEZONE: null,
  DEBUG_SET_ERRORS: null,
  DEBUG_AMPHIRO_DATA_GENERATE_REQUEST: null,
  DEBUG_AMPHIRO_DATA_GENERATED: null,
  DEBUG_GET_FEATURES: null
});

module.exports = types;
