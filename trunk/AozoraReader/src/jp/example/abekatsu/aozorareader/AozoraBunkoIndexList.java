package jp.example.abekatsu.aozorareader;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

public class AozoraBunkoIndexList extends ExpandableListActivity {
    private static final int UPDATE_ID = 0;
	// private OnClickListener mButtonListener;
	private ArrayList<AozoraBunkoTopListInfo> mTopAuthorList = null;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.index_list);
    	setTitle("作家インデックス/AozoraReader");

    	fillTopAuthorList();

    	/*
    	// Create Button Listener for "Search" one.zz
    	Button button = (Button)findViewById(R.id.searchButton);
    	this.mButtonListener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO specification is needed.
				// Search text in "aozora.gr.jp"?;
			}
    	};
    	button.setOnClickListener(mButtonListener);
    	*/
    }

    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean retval;
		retval = super.onCreateOptionsMenu(menu);
		menu.add(0, UPDATE_ID, 0, R.string.menu_update);
		return retval;
	}

	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean retval;
		retval = super.onMenuItemSelected(featureId, item);
		
		switch (item.getItemId()) {
		case UPDATE_ID:
			// TODO update Authors database.
			updateAuthorsDb();
			break;
		}
		
		return retval;
	}


	private void updateAuthorsDb() {
		// TODO Auto-generated method stub
    	try {
    		// TODO
    		// if there expandable, updating only expandble. otherwise update all.
    	} catch (SQLException e) {
    		// TODO this case should be defined.
    	} 
	}


	private void fillTopAuthorList() {
		String TAG, header, searchURL;
		ArrayList<String> groups;
		AozoraBunkoTopListInfo info;
		this.mTopAuthorList = new ArrayList<AozoraBunkoTopListInfo>();
		
		// Create あ行
		TAG = "author A";
		header = "あ行の作家"; 
		searchURL = "http://www.aozora.gr.jp/index_pages/person_a.html";
		groups = new ArrayList<String>(Arrays.asList(new String[] {"あから始まる作家", "いから始まる作家", 
				"うから始まる作家", "えから始まる作家", "おから始まる作家"}));
		info = new AozoraBunkoTopListInfo(TAG, header, searchURL, groups);
		this.mTopAuthorList.add(info);
		
		// Create か行
		TAG = "author KA";
		header = "か行の作家";
		searchURL = "http://www.aozora.gr.jp/index_pages/person_ka.html";
		groups = new ArrayList<String>(Arrays.asList(new String[] {"かから始まる作家", "きから始まる作家", 
				"くから始まる作家", "けから始まる作家", "こから始まる作家"}));
		info = new AozoraBunkoTopListInfo(TAG, header, searchURL, groups);
		this.mTopAuthorList.add(info);

		// Create さ行
		TAG = "author SA";
		header = "さ行の作家";
		searchURL = "http://www.aozora.gr.jp/index_pages/person_sa.html";
    	groups = new ArrayList<String>(Arrays.asList(new String[] {"さから始まる作家", "しから始まる作家", 
    			"すから始まる作家", "せから始まる作家", "そから始まる作家"}));
		info = new AozoraBunkoTopListInfo(TAG, header, searchURL, groups);
		this.mTopAuthorList.add(info);
		
		// Create た行
		TAG = "author TA";
		header = "た行の作家";
		searchURL = "http://www.aozora.gr.jp/index_pages/person_ta.html";
		groups = new ArrayList<String>(Arrays.asList(new String[] {"たから始まる作家", "ちから始まる作家", 
				"つから始まる作家", "てから始まる作家", "とから始まる作家"}));
		info = new AozoraBunkoTopListInfo(TAG, header, searchURL, groups);
		this.mTopAuthorList.add(info);

		// Create な行
		TAG = "author NA";
		header = "な行の作家";
		searchURL = "http://www.aozora.gr.jp/index_pages/person_na.html";
    	groups = new ArrayList<String>(Arrays.asList(new String[] {"なから始まる作家", "にから始まる作家", 
    			"ぬから始まる作家", "ねから始まる作家", "のから始まる作家"}));
		info = new AozoraBunkoTopListInfo(TAG, header, searchURL, groups);
		this.mTopAuthorList.add(info);

		// Create は行
		TAG = "author HA";
		header = "は行の作家";
		searchURL = "http://www.aozora.gr.jp/index_pages/person_ha.html";
    	groups = new ArrayList<String>(Arrays.asList(new String[] {"はから始まる作家", "ひから始まる作家", "ふから始まる作家", "へから始まる作家", "ほから始まる作家"}));
		info = new AozoraBunkoTopListInfo(TAG, header, searchURL, groups);
		this.mTopAuthorList.add(info);
		
		// Create ま行
		TAG = "author MA";
		header = "ま行の作家";
		searchURL = "http://www.aozora.gr.jp/index_pages/person_ma.html";
    	groups = new ArrayList<String>(Arrays.asList(new String[] {"まから始まる作家", "みから始まる作家", 
    			"むから始まる作家", "めから始まる作家", "もから始まる作家"}));
		info = new AozoraBunkoTopListInfo(TAG, header, searchURL, groups);
		this.mTopAuthorList.add(info);

		// Create や行
		TAG = "author YA";
		header = "や行の作家";
		searchURL = "http://www.aozora.gr.jp/index_pages/person_ya.html";
    	groups =  new ArrayList<String>(Arrays.asList(new String[] {"やから始まる作家", "ゆから始まる作家", 
    			"よから始まる作家"}));
		info = new AozoraBunkoTopListInfo(TAG, header, searchURL, groups);
		this.mTopAuthorList.add(info);

		// Create ら行
		TAG = "author RA";
		header = "ら行の作家";
		searchURL = "http://www.aozora.gr.jp/index_pages/person_ra.html";
    	groups = new ArrayList<String>(Arrays.asList(new String[] {"らから始まる作家", "りから始まる作家", 
    			"るから始まる作家", "れから始まる作家", "ろから始まる作家"}));
		info = new AozoraBunkoTopListInfo(TAG, header, searchURL, groups);
		this.mTopAuthorList.add(info);

		// Create わ行
		TAG = "author RA";
		searchURL = "http://www.aozora.gr.jp/index_pages/person_wa.html";
		header = "わ行の作家";
    	groups = new ArrayList<String>(Arrays.asList(new String[] {"わから始まる作家", "をから始まる作家", 
    			"んから始まる作家"}));
		info = new AozoraBunkoTopListInfo(TAG, header, searchURL, groups);
		this.mTopAuthorList.add(info);

		// Create その他
		TAG = "author others";
		header = "その他の作家";
		searchURL = "http://www.aozora.gr.jp/index_pages/person_zz.html";
		groups = new ArrayList<String>(Arrays.asList(new String[] {"その他の作家"}));
		info = new AozoraBunkoTopListInfo(TAG, header, searchURL, groups);
		this.mTopAuthorList.add(info);
		
		AozoraBunkoAuthorExpandableListAdapter mAdapter
		= new AozoraBunkoAuthorExpandableListAdapter(this, this.mTopAuthorList);
		
		setListAdapter(mAdapter);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		boolean result = super.onChildClick(parent, v, groupPosition, childPosition, id);
		Intent i = new Intent(this, AozoraBunkoAuthorList.class);
		AozoraBunkoTopListInfo info = this.mTopAuthorList.get(groupPosition);
		
		i.putExtra(AozoraBunkoAuthorList.KEY_PHONETICINDEX, (groupPosition * 5) + childPosition);
		i.putExtra(AozoraBunkoAuthorList.KEY_SEARCHURL, info.getSearchURLStr());
		i.putExtra(AozoraBunkoAuthorList.KEY_TITLE, info.getGroups().get(childPosition));
		
		startActivity(i);
		return result;
	}


}
