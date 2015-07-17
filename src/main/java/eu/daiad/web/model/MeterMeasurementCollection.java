package eu.daiad.web.model;

import java.util.ArrayList;

public class MeterMeasurementCollection extends MeasurementCollection {

	private String deviceKey;
	
	private ArrayList<SmartMeterMeasurement> measurements;

	public void setDeviceKey(String value) {
		this.deviceKey = value;
	}
	
	public String getDeviceKey() {
		return this.deviceKey;
	}
	
	public void setMeasurements(ArrayList<SmartMeterMeasurement> value) {
		this.measurements = value;
	}
	
	public ArrayList<SmartMeterMeasurement> getMeasurements() {
		return this.measurements;
	}
	
	@Override public EnumDeviceType getType() {
		return EnumDeviceType.METER;
	}
	
}