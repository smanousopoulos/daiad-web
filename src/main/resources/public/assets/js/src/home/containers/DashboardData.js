var React = require('react');
var bs = require('react-bootstrap');
var { injectIntl } = require('react-intl');
var { bindActionCreators } = require('redux');
var { connect } = require('react-redux');
var { push } = require('react-router-redux');

var { STATIC_RECOMMENDATIONS, STATBOX_DISPLAYS, DEV_METRICS, METER_METRICS, DEV_PERIODS, METER_PERIODS, DEV_SORT, METER_SORT } = require('../constants/HomeConstants');

var Dashboard = require('../components/sections/Dashboard');

var { linkToHistory:link } = require('../actions/HistoryActions');
var DashboardActions = require('../actions/DashboardActions');


function mapStateToProps(state, ownProps) {
  return {
    firstname: state.user.profile.firstname,
    devices: state.user.profile.devices,
    layout: state.section.dashboard.layout,
    tempInfoboxData: state.section.dashboard.tempInfoboxData,
    infoboxes: state.section.dashboard.infobox,
  };
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators(Object.assign({}, DashboardActions, {link}), dispatch);
}

function mergeProps(stateProps, dispatchProps, ownProps) {

  return Object.assign({}, ownProps,
               dispatchProps,
               stateProps,
               {
                 infoboxes: stateProps.infoboxes.map(infobox => Object.assign({}, infobox, {linkToHistory: () => dispatchProps.link(infobox)}))
               });
}


var DashboardData = connect(mapStateToProps, mapDispatchToProps, mergeProps)(Dashboard);
DashboardData = injectIntl(DashboardData);

module.exports = DashboardData;
//exports.DashboardData = DashboardData;
//exports.transformInfoboxData = transformInfoboxData;
