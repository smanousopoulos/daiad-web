var moment = require('moment');

function last24Hours () {
  return {
    startDate: moment().subtract(24, 'hours').valueOf(),
    endDate: moment().valueOf(),
    granularity: 0
  };
}

function today () {
  return {
    startDate: moment().startOf('day').valueOf(),
    endDate: moment().endOf('day').valueOf(),
    granularity: 0
  };
}

function thisWeek () {
  return {
    startDate: moment().startOf('isoweek').valueOf(),
    endDate: moment().endOf('isoweek').valueOf(),
    granularity: 2
  };
}
function thisMonth () {
  return {
    startDate: moment().startOf('month').valueOf(),
    endDate: moment().endOf('month').valueOf(),
    granularity: 3
  };
}

function thisYear () {
  return {
    startDate: moment().startOf('year').valueOf(),
    endDate: moment().endOf('year').valueOf(),
    granularity: 4
  };
}

function getPeriod (period, timestamp=moment().valueOf()) {
  return {
    startDate: moment().startOf(period).valueOf(),
    endDate: Object.assign({}, moment(timestamp)).endOf(period).valueOf(),
    granularity: convertPeriodToGranularity(period)
  };
}

function getNextPeriod (period, timestamp=moment().valueOf()) {
  return {
    startDate: moment(timestamp).startOf(period).add(1, period).valueOf(),
    endDate: Object.assign(moment(), moment(timestamp)).endOf(period).add(1, period).valueOf(),
    granularity: convertPeriodToGranularity(period)
  };
}

function getPreviousPeriod (period, timestamp=moment().valueOf()) {
  return {
    startDate: moment(timestamp).startOf(period).subtract(1, period).valueOf(),
    endDate: moment(timestamp).endOf(period).subtract(1, period).valueOf(),
    granularity: convertPeriodToGranularity(period)
  };
}

function getPreviousPeriodSoFar (period, timestamp=moment().valueOf()) {
  return {
    startDate: moment(timestamp).startOf(period).subtract(1, period).valueOf(),
    endDate: moment(timestamp).subtract(1, period).valueOf(),
    granularity: convertPeriodToGranularity(period)
  };
}

function defaultFormatter (timestamp){
  const date = new Date(timestamp);
  return `${date.getDate()}/${date.getMonth()+1}/${date.getFullYear()}`;
}

function selectTimeFormatter (key, intl) {
  switch (key) {
    case "always":
      return (x) => intl.formatDate(x);
    case "year":
      return (x) => intl.formatDate(x, { day: 'numeric', month: 'long', year: 'numeric' });
    case "month":
      return (x) => intl.formatDate(x, { day: 'numeric', month: 'short' });
    case "week":
      return (x) => intl.formatMessage({id: "weekdays." + (new Date(x).getDay()+1).toString()});
    case "day":
      return (x) => intl.formatTime(x, { hour: 'numeric', minute: 'numeric'});
    default:
      return (x) => intl.formatDate(x);
  }
}

function convertPeriodToGranularity  (period) {
  if (period === "year") return 4;
  else if (period === "month") return 3;
  else if (period === "week") return 2;
  else if (period === "day") return 0;
  else return 0;
}

function convertGranularityToPeriod  (granularity) {
  if (granularity === 4) return "year";
  else if (granularity === 3) return "month"; //(period === "month") return 3;
  else if (granularity === 2) return "week"; //(period === "week") return 2;
  else if (granularity === 1 || granularity === 0) return "day"; //(period === "day") return 0;
  else return "day";
}

function getTimeByPeriod  (period) {
  if (period === "year") return thisYear();
  else if (period === "month") return thisMonth();
  else if (period === "week") return thisWeek();
  else if (period === "day") return today();
}

function getLastPeriod (period, timestamp) {
  return moment(timestamp).subtract(period, 1).get(period).toString();
}

function getLastShowerTime  () {
  return {
    startDate: moment().subtract(3, 'month').valueOf(),
    endDate: moment().valueOf(),
    granularity: 0
  };
}

function getGranularityByDiff (start, end) {
  const diff = moment.duration(end - start);

  const years = diff.years(); 
  const months = diff.months();
  const days = diff.days();
  const milliseconds = diff.milliseconds();

  if (years > 0 || months > 6) return 4;
  else if (months > 0) return 3;
  else if (days > 0) return 2;
  else return 0;
}

function getPeriodByTimestamp (period, timestamp) {
  return moment(timestamp).get(getLowerGranularityPeriod(period));
}

function getLowerGranularityPeriod (period) {
  if (period === 'year') return 'month';
  else if (period === 'month') return 'week';
  else if (period === 'week') return 'day';
  else if (period === 'day') return 'hour';
  else throw new Error('error in get lower granularity period with', period);

}

module.exports = {
  defaultFormatter,
  selectTimeFormatter,
  last24Hours,
  today,
  thisWeek,
  thisMonth,
  thisYear,
  getPeriod,
  getNextPeriod,
  getPreviousPeriod,
  getPreviousPeriodSoFar,
  getTimeByPeriod,
  convertGranularityToPeriod,
  getGranularityByDiff,
  getLastShowerTime,
  getLastPeriod,
  getPeriodByTimestamp,
  getLowerGranularityPeriod
};
