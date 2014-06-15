package com.model.om;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SongList 
{
	private ArrayList<Song> songList;
	private static SongList singleton = null;
	private ContentResolver musicResolver;
	private Uri musicUri;
	private Cursor musicCursor;
	private Context context;
	
	public static SongList getInstance()
	{
		if(singleton == null)
			singleton = new SongList();
		
		return singleton;
	}
	
	private SongList()
	{
		this.songList = new ArrayList<Song>();
		this.context = null;
		this.musicUri = null;
		this.musicCursor = null;
	}
	
	public ArrayList<Song> getSongList()
	{
		return this.songList;
	}
	
	private void setContext(Context context)
	{
		this.context = context;
	}
	
	public void setMusicEnvironment(Context context)
	{
		if(this.context == null)
			return;
		
		setContext(context);
		musicResolver = context.getContentResolver();
		musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		musicCursor = musicResolver.query(musicUri, null, null, null, null);
	}
	
	public void retrieveMusicFromDevice()
	{
		if(musicCursor!=null && musicCursor.moveToFirst())
		{
			int titleColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media.TITLE);
			int idColumn = musicCursor.getColumnIndex
				    (android.provider.MediaStore.Audio.Media._ID);
			int artistColumn = musicCursor.getColumnIndex
				    (android.provider.MediaStore.Audio.Media.ARTIST);
			
			do 
			{
			    long thisId = musicCursor.getLong(idColumn);
			    String thisTitle = musicCursor.getString(titleColumn);
			    String thisArtist = musicCursor.getString(artistColumn);
			    songList.add(new Song(thisId, thisTitle, thisArtist));
			    
			}while (musicCursor.moveToNext());
			  
		}
	}
	
	public void setOrderToAlphabetical()
	{
		Collections.sort(this.songList, new Comparator<Song>(){
			  public int compare(Song a, Song b){
			    return a.getName().compareTo(b.getName());
			  }
		});
	}
}
