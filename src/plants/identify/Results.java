package plants.identify;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Results extends Activity{
	private ListView lv;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);
		lv = (ListView)findViewById(R.id.listView2);
		
		try {
			
			JSONArray jArray = new JSONArray(getIntent().getExtras().getString("RESULTS"));
			ArrayList<String> array = new ArrayList<String>();
			for(int i=0; i<jArray.length(); i++){
				JSONObject item = (JSONObject) jArray.get(i);
				array.add(item.getString("name"));
			}
			final JSONArray jArray_ = jArray;
			lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array));
			lv.setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> a, View view, int position, long id) {

					Intent myIntent = new Intent(view.getContext(), Details.class);
					
					try {
						myIntent.putExtra("name", ((JSONObject)jArray_.get(position)).getString("name"));
						myIntent.putExtra("wiki", ((JSONObject)jArray_.get(position)).getString("wiki"));
						myIntent.putExtra("photo", ((JSONObject)jArray_.get(position)).getString("photo"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                startActivityForResult(myIntent, 0);
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}