package com.model.om;

public abstract class Row 
{
	protected String name;
	
	public Row(String name)
	{
		this.name = name;
	}
	
	public abstract String getName();

}
