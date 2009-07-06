package edu.nrao.dss.client;

public class ColumnType {
	
	@SuppressWarnings("unchecked")
	public ColumnType(String id, String name, int length, Class clasz) {
		this.id     = id;
		this.name   = name;
		this.length = length;
		this.clasz  = clasz;
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
	
	private String id;
	private String name;
	private int length;
	@SuppressWarnings("unchecked")
	private Class clasz;

}
