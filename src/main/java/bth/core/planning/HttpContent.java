package bth.core.planning;

public class HttpContent {
	
	private final String content;
	private final long lastModified;
	
	public HttpContent(final String p_content, final long p_lastModified)
	{
		content = p_content;
		lastModified = p_lastModified;
	}

	public String getContent() {
		return content;
	}

	/**
	 * @return the lastModified
	 */
	public long getLastModified() {
		return lastModified;
	}
	
	@Override
	public String toString()
	{
		return content;
	}

}
