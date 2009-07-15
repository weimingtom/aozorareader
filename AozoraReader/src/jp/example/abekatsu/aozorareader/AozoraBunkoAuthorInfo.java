package jp.example.abekatsu.aozorareader;

public class AozoraBunkoAuthorInfo {

	private String author;
	private String url;

	public AozoraBunkoAuthorInfo(String authorName, String strURL) {
		// TODO Auto-generated constructor stub
		this.setAuthor(authorName);
		this.url    = strURL;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

}
