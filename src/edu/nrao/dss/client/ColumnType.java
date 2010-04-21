package edu.nrao.dss.client;

public class ColumnType {
	
	@SuppressWarnings("unchecked")
	public ColumnType(String id, String name, int length, Boolean disabled, Class clasz) {
		this.id       = id;
		this.name     = name;
		this.length   = length;
		this.setDisabled(disabled);
		this.clasz    = clasz;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public int getLength() {
		return length;
	}
	
	@SuppressWarnings("unchecked")
	public Class getClasz() {
		return clasz;
	}
	
	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	private String id;
	private String name;
	private int length;
	private Boolean disabled;
	@SuppressWarnings("unchecked")
	private Class clasz;

}
