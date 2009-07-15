package jp.example.abekatsu.aozorareader;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class AozoraBunkoAuthorExpandableListAdapter extends
		BaseExpandableListAdapter {

	private Context mContext;
	private ArrayList<AozoraBunkoTopListInfo> mTopAuthorList;

	public AozoraBunkoAuthorExpandableListAdapter(Context context,
			ArrayList<AozoraBunkoTopListInfo> topAuthorList) {
		this.mContext = context;
		this.mTopAuthorList = topAuthorList;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		AozoraBunkoTopListInfo topListInfo = this.mTopAuthorList.get(groupPosition);
		return topListInfo.getGroups().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView textView = getGenericView();
		textView.setText(getChild(groupPosition, childPosition).toString());
		return textView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.mTopAuthorList.get(groupPosition).getGroups().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.mTopAuthorList.get(groupPosition).getHeader();
	}

	@Override
	public int getGroupCount() {
		return this.mTopAuthorList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView textView = getGenericView();
		textView.setText(getGroup(groupPosition).toString());
		return textView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private TextView getGenericView() {
		// Layout parameters for the ExpandableListView
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 64);

		TextView textView = new TextView(this.mContext);
		textView.setLayoutParams(lp);
		// Center the text vertically
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		// Set the text starting position
		textView.setPadding(36, 0, 0, 0);
		return textView;
	}
	
}
