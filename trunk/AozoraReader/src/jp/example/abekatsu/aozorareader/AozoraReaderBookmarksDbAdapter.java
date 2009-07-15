package jp.example.abekatsu.aozorareader;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class AozoraReaderBookmarksDbAdapter {
	private static final String KEY_ROWID      ="_id";
	private static final String KEY_AUTHORNAME = "author";
	private static final String KEY_AUTHORID   = "authorId";
	private static final String KEY_TITLE      = "title";
	private static final String KEY_WORKSID    = "worksId";
	private static final String KEY_LOCATION   = "location";
	
	private static final String DATABASE_NAME = "bookmarks.db";
	private static final String DATABASE_TABLE = "bookmarks";

	
	private static final String DATABASE_CREATE =
        "CREATE TABLE " + DATABASE_TABLE + " ("
        + KEY_ROWID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + KEY_AUTHORNAME  + " TEXT NOT NULL, "
        + KEY_AUTHORID    + " INTEGER, "
        + KEY_TITLE       + " TEXT NOT NULL, "
        + KEY_WORKSID     + " INTEGER, "
        + KEY_LOCATION    + " TEXT NOT NULL "
        + ");";
	
	private static final String TAG = "ARBookmarksDbAdapter";
    
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
	private static final long maxDBsize = 10;
	
	/**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public AozoraReaderBookmarksDbAdapter(Context ctx) {
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

	public Cursor fetchAllBookmarks() {
		SQLiteDatabase db = this.mDbHelper.getReadableDatabase();
		Cursor mCursor = db.query(true, DATABASE_TABLE, 
            			new String[] {KEY_ROWID, KEY_AUTHORNAME, KEY_TITLE, KEY_LOCATION},
            			null, null, null, null, null, null);
		return mCursor;
	}

	public void insertInfo(String authorName, long authorId, String worksName,
			long worksId, String xhtmlUrl) {

		SQLiteDatabase db = this.mDbHelper.getWritableDatabase();
		String whereSt = KEY_AUTHORID + " = " + authorId + " AND " + KEY_WORKSID + " = " + worksId;
		Cursor mCursor = db.query(true, DATABASE_TABLE, 
    			new String[] {KEY_ROWID},
    			whereSt, null, null, null, null, null);
		long size = mCursor.getCount();
		mCursor.close();
		if (size > 0) {
			return ; // Nothing to do here.
		}
		
		// Insert here.
		db.beginTransaction();
		try {
			SQLiteStatement stmt = db.compileStatement("insert into " + DATABASE_TABLE + " values (NULL, ?, ?, ?, ?, ?);");
			stmt.bindString(1, authorName);
			stmt.bindLong(2, authorId);
			stmt.bindString(3, worksName);
			stmt.bindLong(4, worksId);
			stmt.bindString(5, xhtmlUrl);
			stmt.executeInsert();
			db.setTransactionSuccessful();
		} catch (SQLiteFullException e) {
			// Counter _id is over-flowed.
			SQLiteStatement stmt = db.compileStatement("update " + DATABASE_TABLE 
      				+ " set _id = _id - (select min(_id) - 1 from " + DATABASE_TABLE + " );");
			stmt.execute();
			db.setTransactionSuccessful();
			db.endTransaction();
			// insert again.
			insertInfo(authorName, authorId, worksName, worksId, xhtmlUrl);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}

		// remove the oldest record from table.
	    db.beginTransaction();
	    try {
	    	SQLiteStatement stmt = db.compileStatement("delete from " + DATABASE_TABLE + " where _id <= " +
	    			"(select max(_id) from " + DATABASE_TABLE + ") - " + AozoraReaderBookmarksDbAdapter.maxDBsize + ";");
	    	stmt.execute();
	    	db.setTransactionSuccessful();
	    } catch (SQLException e) {
			e.printStackTrace();
	    } finally {
	    	db.endTransaction();
	    }
	    
	}

}
