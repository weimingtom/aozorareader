package jp.example.abekatsu.aozorareader;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AozoraBunkoBookmarks extends ListActivity {

	private AozoraReaderBookmarksDbAdapter mDbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbAdapter = new AozoraReaderBookmarksDbAdapter(this);
		mDbAdapter.open();
		fillBookmarksData();
	}

	private void fillBookmarksData() {
		Cursor c = mDbAdapter.fetchAllBookmarks();
		
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, R.layout.bookmarks_list, R.id.bookmarks_row);

		if (c != null) {
			startManagingCursor(c);
			c.moveToFirst();
			do {
				String addStr = new String();
				addStr = c.getString(1)	+ "　" + c.getString(2);
				mAdapter.add(addStr);									
			} while (c.moveToNext());
		}
		setListAdapter(mAdapter);
		c.close();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Cursor c = this.mDbAdapter.fetchAllBookmarks();
		c.moveToPosition(position);
		
		Intent i = new Intent(this, AozoraBunkoViewer.class);
		i.putExtra(AozoraBunkoViewer.KEY_AUTHORID, c.getLong(1));
		i.putExtra(AozoraBunkoViewer.KEY_WORKSID,  c.getLong(2));
		i.putExtra(AozoraBunkoViewer.KEY_LOCATION, c.getString(3));

		c.close();
		startActivity(i);
	}
}
