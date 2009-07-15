package jp.example.abekatsu.aozorareader;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class AozoraBunkoAuthorList extends ListActivity {

	public static final String KEY_PHONETICINDEX = "PHONETICINDEX";
	public static final String KEY_SEARCHURL = "SEARCHURL";
	public static final String KEY_TITLE = "TITLE";
	private static final String TAG = "AozoraBunkoAuthorList";
	
	private AozoraReaderAuthorsDbAdapter mDbAdapter;
	private int phoneticCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.author_list);

		// Pick Up bundle.
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			this.phoneticCode = extras.getInt(AozoraBunkoAuthorList.KEY_PHONETICINDEX);
			String searchUrl = extras.getString(AozoraBunkoAuthorList.KEY_SEARCHURL);
			setTitle(extras.getString(AozoraBunkoAuthorList.KEY_TITLE));
			
			this.mDbAdapter = new AozoraReaderAuthorsDbAdapter(this);
			this.mDbAdapter.open();
			
			Cursor authorListCursor = null;

			try {
				authorListCursor = this.mDbAdapter.fetchAuthorStringNameList(phoneticCode);
				if (authorListCursor.getCount() == 0) {
					authorListCursor.close();
					this.mDbAdapter.updateAuthorsDB(searchUrl, phoneticCode);
					authorListCursor = this.mDbAdapter.fetchAuthorStringNameList(phoneticCode);
				}
			} catch (SQLException e) { // This exception is caught when DB file doesn't exist.
				// TODO retry? showing error message?
				e.printStackTrace();
			} finally {
				if (authorListCursor != null) {
					
					startManagingCursor(authorListCursor);
					// Create an array to specify the fields we want to display in the list (only AUTHORNAME)
					String[] from = new String[]{AozoraReaderAuthorsDbAdapter.KEY_AUTHORNAME};
					// and an array of the fields we want to bind those fields to (in this case just text1)
					int[] to = new int[]{R.id.author_row};
		        
					SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.author_list, 
							authorListCursor, from, to); 
					setListAdapter(mAdapter);
				}
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		this.mDbAdapter.close();
		super.onDestroy();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i(TAG, "position is " + position + ".");

		Intent i = new Intent(this, AozoraBunkoWorksList.class);
		Cursor c = this.mDbAdapter.fetchAuthorInfoFromPosition(position, this.phoneticCode);
		long authorId = c.getLong(1);
		String authorName = c.getString(2);
		c.close();
		
		i.putExtra(AozoraBunkoWorksList.KEY_AUTHORID, authorId);
		i.putExtra(AozoraBunkoWorksList.KEY_AUTHORNAME, authorName);

		startActivity(i);
	}
}
