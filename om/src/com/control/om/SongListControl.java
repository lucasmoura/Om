package com.control.om;


import android.content.Context;
import android.widget.Adapter;

import com.adapter.om.SongAdapter;
import com.model.om.SongList;

public class SongListControl 
{
	private Context context;
	private SongAdapter adapter;
	
	public SongListControl(Context context)
	{
		this.context = context;
	}
	
	public void setUpSongList()
	{
		SongList.getInstance().setMusicEnvironment(context);
		SongList.getInstance().retrieveMusicFromDevice();
	}
	
	public Adapter getSongListAdapter()
	{
		this.adapter = new SongAdapter(SongList.getInstance().getSongList());
		return this.adapter;
	}
	
	public int getAlphabetSize()
	{
		return SongList.getInstance().getAlphabetSize();
	}	
	
	public Object[] getAlphabetElement(int index)
	{
		return SongList.getInstance().getAlphabetElement(index);
	}
	
	public int getAlphabetPosition(String letter)
	{
		return SongList.getInstance().getAlphabetPosition(letter);
	}
}
