package com.model.om;

public class Song 
{
	public long id;
	public String name;
	public String artist;
	
	public Song(long id, String name, String artist)
	{
		this.id = id;
		this.name = name;
		this.artist = artist;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getArtist() {
		return artist;
	}

	
}
