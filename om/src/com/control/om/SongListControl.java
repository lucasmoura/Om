package com.control.om;


import java.util.ArrayList;

import android.content.Context;
import android.widget.Adapter;

import com.adapter.om.SongAdapter;
import com.model.om.Row;
import com.model.om.SongList;
import com.model.om.SongManager;

public class SongListControl 
{
	private Context context;
	private SongAdapter adapter;
	private SongList songList;
	
	public SongListControl(Context context)
	{
		this.context = context;
		songList = new SongList();
	}
	
	public void setUpSongs()
	{
		SongManager.getInstance().setMusicEnvironment(context);
		SongManager.getInstance().retrieveMusicFromDevice();
		
		songList.setOrderToAlphabetical(SongManager.getInstance().getAllSongs());
		songList.parseMusicList(SongManager.getInstance().getAllSongs());
	}
	
	public Adapter getSongListAdapter()
	{
		this.adapter = new SongAdapter(songList.getSongList());
		return this.adapter;
	}
	
	public int getAlphabetSize()
	{
		return songList.getAlphabetSize();
	}	
	
	public Object[] getAlphabetElement(int index)
	{
		return songList.getAlphabetElement(index);
	}
	
	public int getAlphabetPosition(String letter)
	{
		return songList.getAlphabetPosition(letter);
	}
	
	public ArrayList<Row> getSongList()
	{
		return songList.getSongList();
	}
}
