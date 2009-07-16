package jp.example.abekatsu.aozorareader;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AozoraReader extends ListActivity {

	private AozoraReaderBookmarksDbAdapter mDbAdapter;
	private int bookmarkSize;
    private static final int INDEX_ID = 0;
	private static final int ActivityIndex = 0;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	this.mDbAdapter = new AozoraReaderBookmarksDbAdapter(this);
		this.mDbAdapter.open();
		Cursor c = this.mDbAdapter.fetchAllBookmarks();
		this.bookmarkSize = c.getCount();
		c.close();
		
		if (this.bookmarkSize == 0) {
			// No bookmarks so go to Index List.
			startIndexListActivity();
		} else {
			// Display bookmarks as List.
			setContentView(R.layout.main);
			fillBookmarks();
		}
    }

	private void fillBookmarks() {
		Cursor c = this.mDbAdapter.fetchAllBookmarks();
		this.bookmarkSize = c.getCount();

		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, R.layout.main, R.id.main_row);
		c.moveToLast();
		do {
			String addStr = new String();
			addStr = c.getString(1) + "　" + c.getString(2);
			mAdapter.add(addStr);	
		} while (c.moveToPrevious());
		setListAdapter(mAdapter);
		// finally close cursor.
		c.close();
	}

	private void startIndexListActivity() {
		Intent i = new Intent(this, AozoraBunkoIndexList.class);
		startActivityForResult(i, ActivityIndex);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Cursor c = this.mDbAdapter.fetchAllBookmarks();
		c.moveToPosition(c.getCount() - 1 - position);

		String authorName = c.getString(1);
		String worksName  = c.getString(2);
		String location   = c.getString(3);
		c.close();
		
		Intent i = new Intent(this, AozoraBunkoViewer.class);

		i.putExtra(AozoraBunkoViewer.KEY_AUTHORID,   -1);
		i.putExtra(AozoraBunkoViewer.KEY_LOCATION, location);
		i.putExtra(AozoraBunkoViewer.KEY_AUTHORNAME, authorName);
		i.putExtra(AozoraBunkoViewer.KEY_WORKSNAME, worksName);
		i.putExtra(AozoraBunkoViewer.KEY_BOOKMARKED, true);
		
		startActivity(i);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean retval;
		retval = super.onCreateOptionsMenu(menu);
		menu.add(0, INDEX_ID, 0, R.string.menu_index);
		return retval;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean retval;
		retval = super.onMenuItemSelected(featureId, item);
		
		switch (item.getItemId()) {
		case INDEX_ID:
			startIndexListActivity();
			break;
		}
		return retval;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillBookmarks();
	}

	
}
