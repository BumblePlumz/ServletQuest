package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.Map.Entry;

public class WebTool {
	
	static {
		if ( System.getProperty("NoProxy") == null ) {
			System.setProperty("http.proxyHost", "a configurer");
			System.setProperty("http.proxyPort", "3128");
			System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1|[::1]");
		}
	}
	
	private static String CHARSET = "ISO-8859-1";
	
	private String host;
	private String path;
	
	private int returnCode = -1; 
	
	public WebTool(String host, String path) {
		this.host = host;
		this.path = path;
	}
	
	public InputStream request( String method, String url, String data ) throws IOException {
		HttpURLConnection conn = null;
		
		try {
			conn = (HttpURLConnection) new URL( "http://" + host + path + url ).openConnection();
		} catch (IOException e) {
			System.err.println( "!!! Connection Error. (" + e.getClass().getName()+ ") : " + e.getMessage() );
			throw e;
		}
		
		conn.setInstanceFollowRedirects(false);

		if ( "POST".equals(method) ) {
			conn.setDoOutput( true );
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			OutputStream out = conn.getOutputStream();
			out.write( data.getBytes() );
			out.flush();
		} else {
			conn.setDoOutput( false );
			conn.setRequestMethod(method);
		}
		
		returnCode = conn.getResponseCode();
		
		if ( returnCode == 500 ) {
			System.err.println("*************** SERVER ERROR CODE 500 ***************");
			int read = -1;
			byte[] buffer = new byte[4096];
			InputStream is = conn.getErrorStream();
			while( (read = is.read(buffer)) > 0 ) {
				System.err.print( new String(buffer, 0, read) );
			}
			System.err.println();
			System.err.println("*************** SERVER ERROR CODE 500 ***************");
			conn.disconnect();
			throw new IOException( "Server return code 500 error." );
		}
		
		final InputStream in = conn.getInputStream();
		final HttpURLConnection _conn = conn;
		return new InputStream() {
			@Override
			public int read() throws IOException {
				return in.read();
			}
			@Override
			public void close() throws IOException {
				in.close();
				_conn.disconnect();
			}
		};
	}
	
	public InputStream post( String url, String data ) throws IOException {
		return request("POST", url, data);
	}
	
	public InputStream get(String url) throws IOException {
		return request("GET", url, null);
	}
	
	public int code() {
		return returnCode;
	}
	
	
	public static String encode(String key, String val) throws IOException {
		return key + "=" + URLEncoder.encode(val, CHARSET) + "&" ; 
	}
	
	public static String encodeMap(HashMap<String, String> data) throws IOException{
		StringJoiner sj = new StringJoiner("&");
		for( Entry<String, String> entry : data.entrySet() ) {
			sj.add( encode(entry.getKey(),entry.getValue()) );
		}
		return sj.toString();
	}



}
