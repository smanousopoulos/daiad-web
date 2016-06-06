const timeUtil = require('./time');

//TODO: type label already present in messages
//remove extra maps
function combineMessages (categories) {
  return categories.map(cat => 
                            cat.values.map(msg => Object.assign({}, msg, {category: cat.name})))
                       .reduce(((prev, curr) => prev.concat(curr)), [])
                       .sort((a, b) => b.createdOn - a.createdOn);

}

function getTypeByCategory (category) {
  if (category === 'alerts') return 'ALERT';
  else if (category === 'announcements') return 'ANNOUNCEMENT';
  else if (category === 'recommendations') return 'RECOMMENDATION_DYNAMIC';
  else if (category === 'tips') return 'RECOMMENDATION_STATIC';
  else { throw new Error('category not supported: ', category); }
}

function getAlertMedia (message) {
  if (message.alert === 'WATER_LEAK') {
    return {
      type: 'forecast',
      display: 'chart',
      //time: timeUtil.thisYear(),
      period: 'year',
      deviceType: 'METER',
      metric: 'difference',
      data: []
    };
  }
  else if (message.alert === 'REDUCED_WATER_USE') {
    return {
      type: 'total',
      display: 'chart',
      //time: timeUtil.last24Hours(),
      period: 'year',
      deviceType: 'METER',
      metric: 'difference',
      data: []
    };
  }
  else {
    return null;
  }
}

module.exports = {
  combineMessages,
  getTypeByCategory,
  getAlertMedia
};
