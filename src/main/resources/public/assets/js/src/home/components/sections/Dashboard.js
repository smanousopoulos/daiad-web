var React = require('react');
var bs = require('react-bootstrap');
var classNames = require('classnames');

var intl = require('react-intl');
var { injectIntl, FormattedMessage, FormattedRelative } = require('react-intl');

var { Responsive, WidthProvider } = require('react-grid-layout');
var ResponsiveGridLayout = WidthProvider(Responsive);
var PureRenderMixin = require('react-addons-pure-render-mixin');

var MainSection = require('../layout/MainSection');

var ChartBox = require('../helpers/ChartBox');
var StatBox = require('../helpers/StatBox');

const { IMAGES } = require('../../constants/HomeConstants');


var timeUtil = require('../../utils/time');

function ErrorDisplay (props) {
  return props.errors ? 
    <div style={{ zIndex: 100}}>
      <img src={`${IMAGES}/alert.svg`} /><span className="infobox-error">{`${props.errors}`}</span>
    </div>
    :
     (<div/>);
}


/* Be Polite, greet user */
function SayHello (props) {
  return (
    <div style={{margin: '40px 30px 20px 30px'}}>
      <h3><FormattedMessage id="dashboard.hello" values={{name:props.firstname}} /></h3>
    </div>
  );
}

function InfoBox (props) {
  const { mode, infobox, updateInfobox, removeInfobox, chartFormatter, intl } = props;
  let { id, error, period, type, display, linkToHistory, periods, displays, time } = infobox;
  if (!displays) displays = [];
  if (!periods) periods = [];
  
  const _t = intl.formatMessage;
  return (
    <div className='infobox'>
      <div className='infobox-header'>
        <div className='header-left'>
          <h4>{infobox.title}</h4>
        </div>

        <div className='header-right'>
          <div style={{marginRight:10}}>
            {
              displays.map(t => t.id!==display?(
                <a key={t.id} onClick={() => updateInfobox(id, {display:t.id})} style={{marginLeft:5}}>{t.title}</a>
                ):<span key={t}/>)
            }
          </div>
          
          <div>
            {
              periods.map(p => (
                <a key={p.id} onClick={() => updateInfobox(id, {period:p.id})} style={{marginLeft:5}}>{(p.id===period)?(<u>{_t({id: p.title})}</u>):(_t({id: p.title}))}</a>
                ))
            }
          </div>
          {
            (() => type === 'last' && time ? 
             <FormattedRelative value={time} /> : <span/>
             )()
          }
          {
            //TODO: disable delete infobox until add is created
               <a className='infobox-x' style={{float: 'right', marginLeft: 5, marginRight:5}} onClick={()=>removeInfobox(infobox.id)}><i className="fa fa-times"></i></a>
          }
        </div>
      </div>
      
      <div className='infobox-body'>
         {
           (()=>{
             if (error) {
               return (<ErrorDisplay errors={error} />);
               } 
             else {
               if (display==='stat') {
                 return (
                   <StatBox {...props} /> 
                 );
               } 
               else if (display==='chart') {
                 return (
                   <ChartBox {...props} /> 
                   );
               }
               else if (display==='tip') {
                 return (
                   <TipBox {...props} />
                   );
               }
             }
           })()
         }
       </div>
       <div className='infobox-footer'>
          <a onClick={linkToHistory}>See more</a>
       </div>
    </div>
  );
}


function TipBox (props) {
  const { title, type, highlight } = props.infobox;
  return (
    <div >
      <p>{highlight}</p>
    </div>
  );
}

function InfoPanel (props) {
  const { mode, layout, infoboxes, updateLayout, switchMode,  updateInfobox, removeInfobox, chartFormatter, intl, periods, displays } = props;
  return (
    <div>
      <ResponsiveGridLayout 
        className='layout'
        layouts={{lg:layout}}
        breakpoints={{lg:1370, md: 900, sm: 600, xs: 480, xxs: 200}}
        rowHeight={160}
        cols={{lg:8, md: 6, sm: 4, xs: 2, xxs: 1}}
        draggableHandle='.infobox-header'
        isDraggable={true}
        isResizable={false}
        onLayoutChange={(layout, layouts) => { 
          //updateLayout(layout);  
        }}
        onResizeStop={(layout, oldItem, newItem, placeholder) => { 
          updateLayout(layout);  
        }}
        onDragStop={(layout) => {
          updateLayout(layout); 
        }}
       >
       {
         infoboxes.map(function(infobox) {
           return (
             <div key={infobox.id}>
               <InfoBox {...{mode, chartFormatter, infobox, updateInfobox, removeInfobox, intl}} /> 
           </div>
           );
         })
       }
      </ResponsiveGridLayout>
     </div>
  );
}

function ButtonToolbar (props) {
  const { switchMode, mode } = props;
  return (
    <div className="pull-right">
      <bs.ButtonToolbar>
        <bs.Button onClick={()=> switchMode("add")} active={false}>Add</bs.Button>
        {
          (()=> mode==="edit"?(
            <bs.Button onClick={()=> switchMode("normal")} bsStyle="primary" active={false}>Done</bs.Button>
            ):(
            <bs.Button onClick={()=> switchMode("edit")} active={false}>Edit</bs.Button>
            ))()
        }
      </bs.ButtonToolbar>
    </div>
  );
}

var Dashboard = React.createClass({
  mixins: [ PureRenderMixin ],

  componentWillMount: function() {
    const { fetchAllInfoboxesData, switchMode } = this.props;
    //switchMode("normal");
    fetchAllInfoboxesData();

  },
  /*
  componentWillReceiveProps: function(nextProps) {
    console.log('history receiving props');   
    //console.log(nextProps);
    //console.log(this.props);
    for (let key in nextProps) {
      let prop = nextProps[key];
      if (typeof prop === 'function') { continue; }
      if (this.props[key] === prop) { continue; }
      console.log('new', key, prop);
      console.log('old', key, this.props[key]);
    }

   
  },
  */
  render: function() {
    const { firstname, mode, switchMode, amphiros, meters } = this.props;
    return (
      <MainSection id="section.dashboard">
        <SayHello firstname={firstname} />
        
        <InfoPanel {...this.props} />
        
      </MainSection>
    );
  }
});

//Dashboard = injectIntl(Dashboard);
module.exports = Dashboard;
//exports.Dashboard = Dashboard;
//exports.ChartBox = ChartBox;
