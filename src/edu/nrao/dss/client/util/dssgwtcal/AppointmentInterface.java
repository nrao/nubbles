package edu.nrao.dss.client.util.dssgwtcal;

import java.util.Date;

public interface AppointmentInterface extends Comparable<AppointmentInterface> {

	public String getTitle();
	public String getDescription();
	public void setTitle(String title);
	public void setDescription(String desc);
	public Date getStart();
	public void setStart(Date start);
	public Date getEnd();
	public void setEnd(Date end);
	public boolean isSelected();
	public void setSelected(boolean isSelected);
	public void formatTimeline(float top, float height);

    float getTop();
    float getLeft();
    float getWidth();
    float getHeight();
    void setTop(float top);
    void setLeft(float left);
    void setWidth(float width);
    void setHeight(float height); 
}
