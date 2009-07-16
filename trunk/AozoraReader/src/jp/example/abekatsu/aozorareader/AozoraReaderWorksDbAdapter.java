package jp.example.abekatsu.aozorareader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * @author abekatsu
 *
 */
public class AozoraReaderWorksDbAdapter {
	
	public static final String KEY_ROWID     = "_id";
	public static final String KEY_WORKSID   = "works_id";
	public static final String KEY_AUTHORID  = "authors_id";
	public static final String KEY_WORKSNAME = "works_name";
	public static final String KEY_KANAZUKAI = "kanazukai";
	public static final String KEY_LOCATION  = "location";
	
	private static final String DATABASE_NAME  = "works.db";
	private static final String DATABASE_TABLE = "works";
	private static final String TAG = "AozoraReaderWorksDbAdapter";
    private static int mDataBaseVersion = 1;

	private static class WorksDatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_CREATE =
	        "CREATE TABLE " + DATABASE_TABLE + " ("
	        + KEY_ROWID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
	        + KEY_WORKSID     + " INTEGER, "
	        + KEY_AUTHORID    + " INTEGER, "
	        + KEY_WORKSNAME   + " TEXT NOT NULL, "
	        + KEY_KANAZUKAI   + " INTEGER, "
	        + KEY_LOCATION    + " TEXT"
	        + ");";

		public WorksDatabaseHelper(Context context) {
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
	private WorksDatabaseHelper mDbHelper;
	
	/**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public AozoraReaderWorksDbAdapter(Context ctx) {
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
        mDbHelper = new WorksDatabaseHelper(this.mCtx);
        return;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    /**
     * Create works DB for each author.
     * 
     * @param search_url
     * @return
     */
    public boolean createWorksDB(String search_url, long authorId) {
    	boolean retvalue = false;
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

			/*
			 * To match the following lines:
			 * <li><a href="../cards/000035/card1578.html">愛と美について</a>　（新字新仮名、作品ID：1578）　</li> 
			 * <li><a href="../cards/000035/card46597.html">青森</a>　（新字新仮名、作品ID：46597）　</li>
			 */ 
			Pattern works_pattern = Pattern.compile("<li><a href=\"\\.\\./cards/(\\d+)/card(\\d+)\\.html\">(.+)</a>.*（(.+)、作品ID：\\d+）.*</li>");
			while ((line = reader.readLine()) != null) {
				Matcher works_matcher = works_pattern.matcher(line);
				if (works_matcher.find()) {
					String author_id_str = works_matcher.group(1);
					int works_id = Integer.parseInt(works_matcher.group(2));
					String works_title = works_matcher.group(3);
					String kanazukai = works_matcher.group(4); /* その他，旧字旧仮名，新字旧仮名，新字新仮名，*/
					String location = author_id_str + "/" + "card" + works_id + ".html";
					insertWorks(authorId, works_id, works_title, kanazukai, location);
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

	public void updateWorksDB(long authorId) {
		String searchUrl = new String();
		searchUrl = "http://www.aozora.gr.jp/index_pages/person" + authorId + ".html";
		createWorksDB(searchUrl, authorId);
	}

    private void insertWorks(long authorId, long worksId, String worksTitle, String kanazukai, String location) {
    	int kanazukaiId;
    	SQLiteDatabase db = mDbHelper.getWritableDatabase();
    	Log.i(TAG, "Works ID " + worksId+ " Title " + worksTitle + " Kanazukai " + kanazukai);
    	Log.i(TAG, "Loc " + location);
    	
    	if (worksInfoExist(authorId, worksId, db) == true) {
    		return; // Here is duplication check. Since works Id is guaranteed as uniquely, so nothing to do is here.
    	}
    	
    	if (kanazukai.equals("新字新仮名")) {
    		kanazukaiId = 0;
    	} else if (kanazukai.equals("新字旧仮名")) {
    		kanazukaiId = 1;
    	} else if (kanazukai.equals("旧字旧仮名")) {
    		kanazukaiId = 2;
    	} else {
    		kanazukaiId = -1;
    	}

    	db.beginTransaction();
    	try {
    		SQLiteStatement stmt = db.compileStatement("insert into " + DATABASE_TABLE + " values (NULL, ?, ?, ?, ?, ?);");
    		stmt.bindLong(1, worksId);
    		stmt.bindLong(2, authorId);
    		stmt.bindString(3, worksTitle);
    		stmt.bindLong(4, kanazukaiId);
    		stmt.bindString(5, location);
    		stmt.executeInsert();
    		db.setTransactionSuccessful();
    	} catch (SQLException e) {
			e.printStackTrace();
    	} finally {
    		db.endTransaction();
    	}
	}
    
	private boolean worksInfoExist(long authorId, long worksId,
			SQLiteDatabase db) {
    	boolean retValue = false;
    	String whereSt = "(" + KEY_AUTHORID + " = " + authorId + " AND "
    		+ KEY_WORKSID + " = " + worksId + ")";

    	Cursor mCursor = db.query(true, DATABASE_TABLE, 
            			new String[] {KEY_ROWID, KEY_WORKSNAME},
            			whereSt, null, null, null, null, null);

		if (mCursor.getCount() > 0) {
    		retValue = true;
    	}

    	mCursor.close();

    	return retValue;
	}
	
	public Cursor fetchWorksList(long authorId) {
		SQLiteDatabase db = this.mDbHelper.getReadableDatabase();
		
		String whereSt = KEY_AUTHORID + " = " + authorId;

		Cursor mCursor = db.query(true, DATABASE_TABLE, 
            			new String[] {KEY_ROWID, KEY_WORKSNAME, KEY_KANAZUKAI, KEY_LOCATION},
            			whereSt, null, null, null, null, null);
		
    	return mCursor;
	}
	
	public String getKanazukaiType(long kanazukaiId) {
		String retStr;
		
		if (kanazukaiId == 0) {
			retStr = "新字新仮名";
		} else if (kanazukaiId == 1) {
			retStr = "新字旧仮名";
		} else if (kanazukaiId == 2) {
			retStr = "旧字旧仮名";
		} else {
			retStr = "その他";
		}
		
		return retStr;
	}
	
	public Cursor fetchWorksInfoFromPosition(long position, long authorId) {
		SQLiteDatabase db = this.mDbHelper.getReadableDatabase();
		Cursor mCursor = db.query(true, DATABASE_TABLE, 
    			new String[] {KEY_ROWID, KEY_WORKSID, KEY_WORKSNAME, KEY_LOCATION},
    			KEY_AUTHORID + " = " + authorId,
    			null, null, null, null, null);
		mCursor.moveToPosition((int) position);
		return mCursor;
	}
	
}
