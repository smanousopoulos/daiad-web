var React = require('react');

function StatBox (props) {
  const { id, title, type, improved, data, highlight, metric, measurements, period, device, deviceDetails, index, time, better, comparePercentage, mu } = props.infobox;
  let improvedDiv = <div/>;
  if (improved === true) {
    improvedDiv = (<img src={`${IMAGES}/success.svg`}/>);
  }
  else if (improved === false) {
    improvedDiv = (<img src={`${IMAGES}/warning.svg`}/>);
  }
  const duration = data?(Array.isArray(data)?null:data.duration):null;
  const arrowClass = better?"fa-arrow-down green":"fa-arrow-up red";
  const bow = (better==null || comparePercentage == null) ? false : true;
  return (
    <div>
      <div style={{float: 'left', width: '50%'}}>
        <h2>{highlight}<span style={{fontSize:'0.5em', marginLeft:5}}>{mu}</span></h2>
      </div>
      <div style={{float: 'left', width: '50%'}}>
        <div>
          {
            (() => bow ? 
             <span><i className={`fa ${arrowClass}`}/>{better ? `${comparePercentage}% better than last ${period} so far!` : `${comparePercentage}% worse than last ${period} so far`}</span>
             :
               <span>No data</span>
               )()
          }
        </div>
      </div>
    </div>
  );
}
module.exports = StatBox;

