package com.model.om;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SongManager 
{
	private static SongManager singleton = null;
	private ContentResolver musicResolver;
	private Uri musicUri;
	private Cursor musicCursor;
	private ArrayList<Row> songs;
	
	public static SongManager getInstance()
	{
		if(singleton == null)
			singleton = new SongManager();
		
		return singleton;
	}
	
	private SongManager()
	{
		musicUri = null;
		musicCursor = null;
		songs = new ArrayList<Row>();
	}
	
	public ArrayList<Row> getAllSongs()
	{
		return songs;
	}
	
	public void setMusicEnvironment(Context context)
	{	
		if(context == null)
			return;
		
		this.musicResolver = context.getContentResolver();
		this.musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		this.musicCursor = musicResolver.query(musicUri, null, null, null, null);
	}
	
	public void retrieveMusicFromDevice()
	{
		
		songs.clear();
		
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
			    songs.add(new Song(thisId, thisTitle, thisArtist));
			    
			}while (musicCursor.moveToNext());
			  
		}
		
		if( musicCursor != null && !musicCursor.isClosed() )
	        musicCursor.close();
	}
	
	
}
