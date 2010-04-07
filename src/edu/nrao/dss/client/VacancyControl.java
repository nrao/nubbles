package edu.nrao.dss.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DataListEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.DataList;
import com.extjs.gxt.ui.client.widget.DataListItem;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

import edu.nrao.dss.client.util.TimeUtils;

public class VacancyControl extends FormPanel {
	
//	private static final DateTimeFormat DATETIME_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
	
	DateField vacancyDate;
	private TimeField vacancyTime;
	private SimpleComboBox<String> hours;
	public CheckBoxGroup nomineeOptions = new CheckBoxGroup();
	
	private Schedule schedule;
	private class Hole {
		public Hole(Date d, Time t, Integer m) {
			date = d;
			time = t;
			duration = m;
		}
		public Date date;
		public Time time;
		public Integer duration;
	};
	private HashMap<String, Hole> holes = new HashMap<String, Hole>();
	final private DataList vacancyShortcut = new DataList();
	
	public VacancyControl(Schedule sched) {
		schedule = sched;
		initLayout();
	}
	
	public void setVacancyOptions(List<BaseModelData> data) {
		holes.clear();
		vacancyShortcut.removeAll();
		if (data.size() <= 0) {
			return;
		}
		Date end = TimeUtils.toDate(data.get(0));
		for (BaseModelData datum : data) {
			Date t = TimeUtils.toDate(datum);
			long t_msec = t.getTime();
			long end_msec = end.getTime();
			// Is there a hole?
			if (end_msec < t_msec) {
				long msecs = t_msec - end_msec;
				String hm_str = TimeUtils.min2sex((int)(msecs/(60*1000)));
				String key = "gap at " + TimeUtils.DATETIME_FORMAT.format(end) + " for " + hm_str;
				holes.put(key, new Hole(datePart(end), timePart(end), msec2minutes(msecs)));
				vacancyShortcut.add(key);
			}
			end = getEnd(t, toDuration(datum));
		}
	}
	
	private void initLayout() {
		setHeading("Vacancy Control");
		setBorders(false);
	    setWidth("40%");
	    VBoxLayout thisLayout = new VBoxLayout();
	    thisLayout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
	    this.setLayout(thisLayout);
	    
		LayoutContainer topContainer = new LayoutContainer();
		topContainer.setBorders(false);
		//topContainer.setSize(100, 100);
		topContainer.setHeight("75%");
		HBoxLayout topLayout = new HBoxLayout();
	    topLayout.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
		topContainer.setLayout(topLayout);
		this.add(topContainer);
		
		LayoutContainer leftContainer = new LayoutContainer();
		leftContainer.setBorders(false);
		leftContainer.setWidth("60%");
		leftContainer.setLayout(new FormLayout());
		topContainer.add(leftContainer);
		LayoutContainer rightContainer = new LayoutContainer();
		rightContainer.setBorders(false);
		rightContainer.setLayout(new FormLayout());
		topContainer.add(rightContainer);
		
		// Nominee vacancies
	    vacancyShortcut.setToolTip("Selectable list of unscheduled gaps in the displayed schedule");
	    vacancyShortcut.setScrollMode(Style.Scroll.AUTOY);  // TODO does not work
	    rightContainer.add(vacancyShortcut);
	    Listener shortcutListener = new Listener<DataListEvent>() {
	    	public void handleEvent(DataListEvent be) {
	    		List<DataListItem> selection = be.getSelected();
	    		if (selection == null || selection.size() == 0) {
	    			return;
	    		}
	    		DataListItem dli = selection.get(0);
	    		Hole h = holes.get(dli.getText());
	    		vacancyDate.setValue(h.date);
	    		schedule.startVacancyDate = h.date;
	    		vacancyTime.setDateValue(h.time.getDate());
	    		schedule.startVacancyTime = h.time;
	    		hours.setSimpleValue(TimeUtils.min2sex(h.duration));
	    		schedule.numVacancyMinutes = h.duration;
	    		schedule.updateNominees();
	    	}
	    };
	    vacancyShortcut.addListener(Events.SelectionChange, shortcutListener);
			
		// Nominee date
	    vacancyDate = new DateField();
	    //vacancyDate.setValue(startVacancyDate);
	    vacancyDate.setFieldLabel("Start Date");
		vacancyDate.setToolTip("Set the start day for the vacancy to be filled");
	    vacancyDate.addListener(Events.Valid, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	            schedule.startVacancyDate = vacancyDate.getValue();
	    	}
	    });
	    leftContainer.add(vacancyDate);
	    
	    // Nominee time
	    vacancyTime = new TimeField();
	    vacancyTime.setFormat(DateTimeFormat.getFormat("HH:mm"));
	    //vacancyTime.setValue(startVacancyTime);
	    vacancyTime.setFieldLabel("Start Time");
		vacancyTime.setToolTip("Set the start time for the vacancy to be filled");
	    vacancyTime.addListener(Events.Change, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	            schedule.startVacancyTime = vacancyTime.getValue();
	    	}
	    });
	    leftContainer.add(vacancyTime);

		// Nominee maximum duration
		hours = new SimpleComboBox<String>();
		final HashMap<String, Integer> durChoices = new HashMap<String, Integer>();
		String noChoice = new String("none");
		durChoices.put(noChoice, 0);
		hours.add(noChoice);
		hours.setForceSelection(true);
		for (int m = 15; m < 24*60+15; m += 15) {
			String key = TimeUtils.min2sex(m);
			durChoices.put(key, m);
			hours.add(key);
		}
		hours.setToolTip("Set the maximum vacancy duration");
		hours.setFieldLabel("Duration");
		hours.setEditable(false);
	    hours.addListener(Events.Select, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		schedule.numVacancyMinutes = durChoices.get(hours.getSimpleValue()); 
	    	}
	    });
		leftContainer.add(hours);
		
		// Nominee options		
		//add(new LabelField());
		//final CheckBoxGroup nomineeOptions = new CheckBoxGroup();
		nomineeOptions.setSpacing(15);
		nomineeOptions.setFieldLabel("Selection Options");
		// timeBetween
		CheckBox timeBetween = new CheckBox();
		timeBetween.setBoxLabel("ignore timeBetween?");
		timeBetween.setTitle("Ignore sessions' timeBetween limits?");
		timeBetween.setValue(false);
		nomineeOptions.add(timeBetween);
		// minimum
		CheckBox minimum = new CheckBox();
		minimum.setBoxLabel("ignore minimum?");
		minimum.setTitle("Ignore sessions' minimum duration limits?");
		minimum.setValue(false);
		nomineeOptions.add(minimum);
		// blackout
		CheckBox blackout = new CheckBox();
		blackout.setBoxLabel("ignore blackout?");
		blackout.setTitle("Ignore observers' blackout periods?");
		blackout.setValue(false);
		nomineeOptions.add(blackout);
		// backup
		CheckBox backup = new CheckBox();
		backup.setBoxLabel("only backups?");
		backup.setTitle("Use only sessions marked as backups?");
		backup.setValue(false);
		nomineeOptions.add(backup);
		// completed
		CheckBox completed = new CheckBox();
		completed.setBoxLabel("use completed?");
		completed.setTitle("Include completed sessions?");
		completed.setValue(false);
		nomineeOptions.add(completed);
		// rfi
		CheckBox rfi = new CheckBox();
		rfi.setBoxLabel("ignore RFIexclusion?");
		rfi.setTitle("Ignore sessions' day time RFI exclusion?");
		rfi.setValue(false);
		nomineeOptions.add(rfi);
		
		this.add(nomineeOptions);
		
	    // Fetch nominees
		final Button nomineesButton = new Button("Nominees");
		nomineesButton.setToolTip("Request possible periods for the selected time");
	    nomineesButton.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	            schedule.updateNominees();
	    	}
	    });
		leftContainer.add(new AdapterField(nomineesButton));
	}

	
	private Date datePart(Date d) {
		return new Date(d.getYear(), d.getMonth(), d.getDate());
	}
	
	private Time timePart(Date d) {
		return new Time(d.getHours(), d.getMinutes());
	}
	
	private Integer msec2minutes(long ms) {
		return (int) (ms/(60*1000));
	}
	
	private Double toDuration(BaseModelData d) {
		Object value = d.get("duration");
		// This is needed because newly entered values from the user are of type
		// String, but only become Double upon being returned from the server.
		if (value.getClass() == String.class) {
			return Double.valueOf(value.toString());
		} else {
		    return d.get("duration");
	    }
	}
	
    private Date getEnd(Date start, Double duration) {
    	long startSecs = start.getTime();
    	// add the duration (in hours) to this time in milli-seconds
    	long endMsecs = (long) (startSecs + (duration * 60.0 * 60.0 * 1000.0));
    	return new Date(endMsecs);
    }
}
