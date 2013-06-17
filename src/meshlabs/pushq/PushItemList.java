package meshlabs.pushq;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.util.JsonReader;
import android.util.JsonWriter;

public class PushItemList {
	List<PushItem> items = new ArrayList<PushItem>();
	
	private PushItemList(JsonReader reader) throws IOException {
		
		reader.beginArray();
		while (reader.hasNext()) {
			items.add(PushItem.fromJson(reader));
		}
		reader.endArray();
	}
	
	public void add(PushItem item) {
		items.add(item);
	}
	
	public PushItemList() {
		
	}
	
	public void outputJson(OutputStream out) throws IOException {
		JsonWriter writer = new JsonWriter(new OutputStreamWriter(out));
		writer.beginArray();
		for (PushItem item : items) {
			item.asJson(writer);
		}
		writer.endArray();
		writer.close();
	}
	
	public static PushItemList getItems(InputStream in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
			return new PushItemList(reader);
		} finally {
			reader.close();
		}
	}
	
	
}