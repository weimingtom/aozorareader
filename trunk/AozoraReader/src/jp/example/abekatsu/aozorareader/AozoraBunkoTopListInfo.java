package jp.example.abekatsu.aozorareader;

import java.util.ArrayList;

public class AozoraBunkoTopListInfo {

	private String tag;
	private String header;
	private String searchURLStr;
	private ArrayList<String> groups;

	public AozoraBunkoTopListInfo(String tag, String header, String searchURL,
			ArrayList<String> groups) {
		this.setTag(tag);
		this.setHeader(header);
		this.setSearchURLStr(searchURL);
		this.setGroups(groups);
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getHeader() {
		return header;
	}

	public void setSearchURLStr(String searchURLStr) {
		this.searchURLStr = searchURLStr;
	}

	public String getSearchURLStr() {
		return searchURLStr;
	}

	public void setGroups(ArrayList<String> groups) {
		this.groups = groups;
	}

	public ArrayList<String> getGroups() {
		return groups;
	}

}
