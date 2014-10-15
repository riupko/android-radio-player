package co.go.simpleplayer;

import co.go.simpleplayer.adapter.CustomListAdapter;
import co.go.simpleplayer.app.AppController;
import co.go.simpleplayer.model.Station;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;


public class StationListActivity extends Activity {

    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Station json url
    private static final String url = "http://101.ru/api/getbroadcaststation.php";
    private ProgressDialog pDialog;
    private List<Station> stationList = new ArrayList<Station>();
    private ListView listView;
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);

        listView = (ListView) findViewById(R.id.stationList);
        adapter = new CustomListAdapter(this, stationList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Station station = (Station) parent.getAdapter().getItem(position);
                Toast.makeText(getApplicationContext(), "Clicked id=" + station.getId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                intent.putExtra(PlayActivity.STATION_ID, station.getId());
                startActivity(intent);
            }
        });

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // changing action bar color
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#1b1b1b")));

        // Creating volley request obj
        JsonObjectRequest stationReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        JSONArray jsonStations = null;
                        try {
                            jsonStations = response.getJSONArray("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < jsonStations.length(); i++) {
                            try {
                                JSONObject obj = jsonStations.getJSONObject(i);
                                Station station = new Station();
                                station.setId(Integer.parseInt(obj.getString("id")));
                                station.setGroupId(Integer.parseInt(obj.getString("group_id")));
                                station.setTitle(obj.getString("name"));
                                station.setThumbnailUrl(obj.getString("picUrl"));

                                // adding station to stations array
                                stationList.add(station);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stationReq);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
