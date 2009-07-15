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

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AozoraBunkoViewer extends Activity {

	public static final String KEY_AUTHORID = "Viewer_AUTHORID";
	public static final String KEY_WORKSID  = "Viewer_WORKSID";
	public static final String KEY_LOCATION = "Viewer_Location";
	public static final String KEY_AUTHORNAME = "Viewer_AUTHORNAME";
	public static final String KEY_WORKSNAME = "Viewer_WORKSNAME";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		long authorId, worksId;
		String authorName, worksName;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewer);
		
		// Pick Up bundle.
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			authorId = extras.getLong(AozoraBunkoViewer.KEY_AUTHORID);
			worksId  = extras.getLong(AozoraBunkoViewer.KEY_WORKSID);
			authorName = extras.getString(AozoraBunkoViewer.KEY_AUTHORNAME);
			worksName  = extras.getString(AozoraBunkoViewer.KEY_WORKSNAME);
				
			String xhtmlUrl = extras.getString(AozoraBunkoViewer.KEY_LOCATION);

			if (xhtmlUrl == null) {
				String cardUrl = String.format("http://www.aozora.gr.jp/cards/%06d/card%d.html",
						authorId, worksId);
				String tmpUrl = getXHTMLURLString(cardUrl, authorId, worksId);
				if (tmpUrl == null) {
					xhtmlUrl = cardUrl;
				} else {
					xhtmlUrl = tmpUrl;
				}
				AozoraReaderBookmarksDbAdapter mDbAdapter = new AozoraReaderBookmarksDbAdapter(this);
				mDbAdapter.open();
				mDbAdapter.insertInfo(authorName, authorId, worksName, worksId, xhtmlUrl);
				mDbAdapter.close();
			}
			
			// Here edit Activity's label.
			String title = worksName + "/" + authorName;
			setTitle(title);

			WebView webview = (WebView)findViewById(R.id.aozora_webview);
			webview.getSettings().setJavaScriptEnabled(true);
			webview.loadUrl(xhtmlUrl);
		}
		
	}

	private String getXHTMLURLString(String urlStr, long authorId, long worksId) {
		String retStr = null;
		
		URL url;
		try {
			url = new URL(urlStr);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			InputStream in = http.getInputStream();
			String charEncoding = http.getContentEncoding();
			BufferedReader reader;
			if (charEncoding == null) {
				String contentType = http.getContentType();
				Pattern contentTypePattern = Pattern.compile(".+charset=(.+)");
				Matcher contentTypeMatcher = contentTypePattern.matcher(contentType);
				if (contentTypeMatcher.find()) {
					String matchType = contentTypeMatcher.group(1);
					reader = new BufferedReader(new InputStreamReader(in, matchType));
				} else {
					reader = new BufferedReader(new InputStreamReader(in, "EUC-JP"));
				}
			} else {
				reader = new BufferedReader(new InputStreamReader(in, charEncoding));
			}

			String line;
			/*
			 * To match the following lines:
			 *［<a href="#download">ファイルのダウンロード</a>｜<a href="./files/236_19996.html">いますぐXHTML版で読む</a>］
			 * 
			 */
			Pattern xhtml_pattern = Pattern.compile("<a href=\"\\./(files/\\d+_\\d+\\.html)\">いますぐXHTML版で読む</a>");
			while ((line = reader.readLine()) != null) {
				Matcher xhtml_matcher = xhtml_pattern.matcher(line);
				if (xhtml_matcher.find()) {
					String xhtmlLoc = xhtml_matcher.group(1);
					retStr = new String();
					retStr = String.format("http://www.aozora.gr.jp/cards/%06d/%s",
							authorId, xhtmlLoc);
				}
			}
			reader.close();
			in.close();
			http.disconnect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retStr;
	}
}
