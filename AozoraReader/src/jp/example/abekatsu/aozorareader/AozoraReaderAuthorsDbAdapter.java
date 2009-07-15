package jp.example.abekatsu.aozorareader;

// import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class AozoraReaderAuthorsDbAdapter {

	private static final String KEY_ROWID         ="_id";
	private static final String KEY_AUTHORID      = "author_id";
	private static final String KEY_TOPLETTERID   = "topletter_id";
	public  static final String KEY_AUTHORNAME    = "authors_name";
	
	private static final String DATABASE_NAME = "authors.db";
	private static final String DATABASE_TABLE = "authors";

	
	private static final String DATABASE_CREATE =
        "CREATE TABLE " + DATABASE_TABLE + " ("
        + KEY_ROWID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + KEY_AUTHORID    + " INTEGER, "
        + KEY_TOPLETTERID + " INTEGER, "
        + KEY_AUTHORNAME  + " TEXT NOT NULL" 
        + ");";
	
	private static final String TAG = "ARAuthorsDbAdapter";
    
    private static int mDataBaseVersion = 1;	    	

	private static class DatabaseHelper extends SQLiteOpenHelper {
    	
		public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, mDataBaseVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	// TODO
        	Log.i(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO
        	Log.i(TAG, "enter onUpgrade()");
        	db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }


	private Context mCtx;
	private DatabaseHelper mDbHelper;
	
	/**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public AozoraReaderAuthorsDbAdapter(Context ctx) {
    	this.mCtx = ctx;
	}

	/**
     * Open the authors database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public void open() throws SQLException {
        mDbHelper = new DatabaseHelper(this.mCtx);
        return;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    /**
     * Create an Authors SQLite DB corresponding to phonetic code.
     */
    public boolean createAuthorsDB() {
    	boolean retvalue = true;
    	int index = 0;
    	
    	ArrayList<String> url_array = new ArrayList<String>();
    	url_array.add("http://www.aozora.gr.jp/index_pages/person_a.html");
    	url_array.add("http://www.aozora.gr.jp/index_pages/person_ka.html");
    	url_array.add("http://www.aozora.gr.jp/index_pages/person_sa.html");
    	url_array.add("http://www.aozora.gr.jp/index_pages/person_ta.html");
    	url_array.add("http://www.aozora.gr.jp/index_pages/person_ha.html");
    	url_array.add("http://www.aozora.gr.jp/index_pages/person_ma.html");
    	url_array.add("http://www.aozora.gr.jp/index_pages/person_ya.html");
    	url_array.add("http://www.aozora.gr.jp/index_pages/person_ra.html");
    	url_array.add("http://www.aozora.gr.jp/index_pages/person_wa.html");
    	url_array.add("http://www.aozora.gr.jp/index_pages/person_zz.html");

    	for (String url:url_array) {
    		retvalue = (retvalue & createAuthorsDB(url, index));
    		index += 1;
    	}
    	
    	return retvalue;
    }
    
    public boolean createAuthorsDB(String search_url, int index) {
    	boolean retvalue = true;
    	
    	try {
    		URL url = new URL(search_url);
    		HttpURLConnection http = (HttpURLConnection) url.openConnection();
    		http.setRequestMethod("GET");
			http.connect();
			InputStream in = http.getInputStream();
			String charEncoding = http.getContentEncoding();
			BufferedReader reader;
			if (charEncoding == null) {
				// Currently we suppose that the encoding of Aozora Bunko is "EUC-JP".
				// Aozora Bunko returns <meta http-equiv="Content-Type" content="text/html; charset=euc-jp">.
				// TODO We should parse the result of getContentType and pick up "euc-jp".
				reader = new BufferedReader(new InputStreamReader(in, "EUC-JP"));
			} else {
				reader = new BufferedReader(new InputStreamReader(in, charEncoding));
			}

			String line;
			Pattern sec_pattern    = Pattern.compile("<a name=\"sec(\\d)\">.+</a>");
			Pattern person_pattern = Pattern.compile("<a href=\"person(\\d+)\\.html#sakuhin_list_1\">(.+)</a>.*公開中.+$");
			int section = 0;

			while ((line = reader.readLine()) != null) {
				// 1. Find "<a name="sec.">" such as <a name="sec2">イC</a>.
				Matcher sec_matcher    = sec_pattern.matcher(line);
				Matcher person_matcher = person_pattern.matcher(line);
				if (sec_matcher.find()) {
					// since the number of section begins from 1. We'd like to start from `0' to use as index.
					section = Integer.parseInt(sec_matcher.group(1)) - 1;
				} else if (person_matcher.find()) {
					int author_id = Integer.parseInt(person_matcher.group(1));
					int topletter_id = (index / 5 * 5) + section;
					String author_name = person_matcher.group(2);
					insertAuthors(author_id, topletter_id, author_name);
				}
			}
			reader.close();
			in.close();
			http.disconnect();
    		
    	} catch (MalformedURLException e) {
			e.printStackTrace();
			retvalue = false;
		} catch (IOException e) {
			e.printStackTrace();
			retvalue = false;
		}

		return retvalue;
    }

	/**
     * Update an Authors SQLite DB corresponding to phonetic code.
     * Actually, drop existing DB and create again.
     *
	 * @param searchUrl
	 * @param phoneticCode  
     */
    public boolean updateAuthorsDB(String searchUrl, int phoneticCode) {
    	return createAuthorsDB(searchUrl, phoneticCode);
    }
    
    private void insertAuthors(int authorId, int topletterId, String authorName) {
    	SQLiteDatabase db = mDbHelper.getWritableDatabase();
    	Log.i(TAG, "Top letter ID " + topletterId + " author_id " + authorId + " author name " + authorName);

    	if (authorInfoExist(authorId, db) == true) {
    		return; // Here is duplication check. Since author Id is guaranteed as uniquely, so nothing to do is here.
    	}
    	
    	db.beginTransaction();
    	try {
    		SQLiteStatement stmt = db.compileStatement("insert into " + DATABASE_TABLE + " values (NULL, ?, ?, ?);");
    		stmt.bindLong(1, authorId);
    		stmt.bindLong(2, topletterId);
    		stmt.bindString(3, authorName);
    		stmt.executeInsert();
    		db.setTransactionSuccessful();
    	} catch (SQLException e) {
			e.printStackTrace();
    	} finally {
    		db.endTransaction();
    	}
	}

    private boolean authorInfoExist(int authorId, SQLiteDatabase db) {
    	boolean retValue = false;
    	String whereSt = KEY_AUTHORID + " = " + authorId;

    	Cursor mCursor = db.query(true, DATABASE_TABLE, 
            			new String[] {KEY_ROWID, KEY_AUTHORNAME},
            			whereSt, null, null, null, null, null);

		if (mCursor.getCount() > 0) {
    		retValue = true;
    	}

    	mCursor.close();

    	return retValue;
	}

	/**
     * Return a Cursor over the list of all Authors 
     * (letter ID, name and URL) in the database.
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllAuthors() {
    	SQLiteDatabase mDb = mDbHelper.getReadableDatabase();
        Cursor c = mDb.query(DATABASE_TABLE, 
        			new String[] {KEY_ROWID, KEY_AUTHORID, KEY_AUTHORNAME}, 
        			null, null, null, null, null);
        return c;
    }


	/**
	 * Return Authors Cursor which top letter is the same with given one.
	 * 
	 * @param phoneticCode
	 * @return Cursor over all authors which top letter is the given phonetic code.
	 */
	public Cursor fetchAuthorStringNameList(long phoneticCode) {
		SQLiteDatabase db = this.mDbHelper.getReadableDatabase();
    	Cursor mCursor = db.query(true, DATABASE_TABLE, 
            			new String[] {KEY_ROWID, KEY_AUTHORNAME},
            			KEY_TOPLETTERID + " = " + phoneticCode,
            			null, null, null, null, null);
    	return mCursor;
	}

	public Cursor fetchAuthorInfoFromPosition(int position, int phoneticCode) {
		SQLiteDatabase db = this.mDbHelper.getReadableDatabase();
		Cursor mCursor = db.query(true, DATABASE_TABLE, 
    			new String[] {KEY_ROWID, KEY_AUTHORID, KEY_AUTHORNAME},
    			KEY_TOPLETTERID + " = " + phoneticCode,
    			null, null, null, null, null);
		mCursor.moveToPosition(position);
		return mCursor;
	}
	
}
