package edu.nrao.dss.client.util;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Subject {
	
	private ArrayList<ObserverContentPanel> observers;
	
	public Subject(){
		observers = new ArrayList<ObserverContentPanel>();
	}
	
	public void attach(ObserverContentPanel observer) {
		observers.add(observer);
	}
	
	public void detach(ObserverContentPanel observer) {
		observers.remove(observer);
	}
	
	public void notifyObservers() {
		for (ObserverContentPanel o : observers) {
			o.update(this);
		}
	}
	
	public abstract HashMap<String, Object> getState();
}
