package com.model.om;

public class Song extends Row
{
	public long id;
	public String artist;
	
	public Song(long id, String name, String artist)
	{
		super(name);
		this.id = id;
		this.artist = artist;
	}

	public long getId() {
		return this.id;
	}

	public String getName() 
	{
		return super.name;
	}

	public String getArtist() {
		return this.artist;
	}

	
}
