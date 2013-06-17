package meshlabs.pushq;

import java.io.IOException;

import android.util.JsonReader;
import android.util.JsonWriter;

public class PushItem {
	private String title;
	private String url;
	private long period;
	
	public PushItem(String title, String url, long period) {
		this.title = title;
		this.url = url;
		this.period = period;
	}
	
	public void asJson(JsonWriter writer) throws IOException{
		writer.beginObject();
		writer.name("title").value(title);
		writer.name("url").value(url);
		writer.name("period").value(period);
		writer.endObject();
	}
	
	@Override
	public String toString() {
		return title + " [" + url + "]";
	}
	
	
	
	public static PushItem fromJson(JsonReader reader) throws IOException {
		String title = null;
		String url = null;
		long period = 0;
		
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("title")) {
				title = reader.nextString();
			} else if (name.equals("url")) {
				url = reader.nextString();
			} else if (name.equals("period")) {
				period = reader.nextLong();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		
		if (title != null && url != null && period > 0) {
			return new PushItem(title, url, period);
		} else {
			return null;
		}
	}

}
