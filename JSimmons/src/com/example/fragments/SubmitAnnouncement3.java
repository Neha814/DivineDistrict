package com.example.fragments;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.MultiAutoCompleteTextView.Tokenizer;

import com.example.functions.Constants;
import com.example.jsimmons.R;
import com.example.utils.GPSTracker;
import com.example.utils.TransparentProgressDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



public class SubmitAnnouncement3 extends Fragment {

	private View rootView;
	ProgressBar progressBar;

	Button next_button;
	LinearLayout next_layout;

	GoogleMap mGoogleMap;
	SupportMapFragment fm;
	Location location;
	LatLng utilis;
	
	List<HashMap<String, String>> mapresult;
	
	ParserTask parserTask;
	
	TransparentProgressDialog db;
	
	PlacesTask placesTask;
	
	PlacesTask1 placesTask1;
	MultiAutoCompleteTextView address , city , state;
///	String API = "AIzaSyAmCQt1GXMDTYbQsqUuVbpIw3U97igBc6Q";
	String API = "AIzaSyBHv_xhbxCdSUfK-g_fVG2cMlBmt7nAG5E";
	private static final String PLACES_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
	String place_id;
	Boolean isCity = false, isState = false, isCountry = false;
	
	List<Address> addresses;
	Geocoder geocoder;
	
	getLatLngTask getLatLngObj;
	
	String Info_for_a_ann;
	
	 String placeText;
	 
	 GPSTracker gps ;
	
	String countryCode,locality,addressString,cityString,countryString;
	
	ArrayList<String> places = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.submit_announcement3, container,
				false);
		init();

		return rootView;
	}

	private void init() {
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		next_button = (Button) rootView.findViewById(R.id.next_button);
		next_layout = (LinearLayout) rootView.findViewById(R.id.next_layout);
		
		gps = new GPSTracker(getActivity());
		
		geocoder = new Geocoder(getActivity(), Locale.getDefault());

		address = (MultiAutoCompleteTextView) rootView.findViewById(R.id.address);
		state = (MultiAutoCompleteTextView) rootView.findViewById(R.id.state);
		city = (MultiAutoCompleteTextView) rootView.findViewById(R.id.city);
		
		fm = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map));
		
		city.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
				isCity = true;
				isState = false;
				isCountry = false; 
				
				placesTask = new PlacesTask(s.toString());
				placesTask.execute();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		
		state.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
				isState= true;
				 isCountry= false;
				isCity = false; 
				
				placesTask = new PlacesTask(s.toString());
				placesTask.execute();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		
		
		
		
		mGoogleMap = fm.getMap();
		mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
		mGoogleMap.setMyLocationEnabled(true);
		
		
		
		
		double latitude = gps.getLatitude();
		double longitude = gps.getLongitude();
		
		utilis = new LatLng(latitude, longitude);
		mGoogleMap.moveCamera(CameraUpdateFactory
				.newLatLng(utilis));
		mGoogleMap.animateCamera(CameraUpdateFactory
				.zoomTo(9.0F));
		
		Marker TP = mGoogleMap.addMarker(new MarkerOptions()
				.position(utilis)
				.title("Announcement")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.app_logo)));
		
		
		
		
		mGoogleMap
		.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

			public void onMyLocationChange(
					Location paramAnonymousLocation) {
				
				double latitude , longitude;

				latitude = paramAnonymousLocation.getLatitude();
			
				longitude = paramAnonymousLocation.getLongitude();
				
			
				location = paramAnonymousLocation;
				
				

			
				/*utilis = new LatLng(latitude, longitude);
					mGoogleMap.moveCamera(CameraUpdateFactory
							.newLatLng(utilis));
					mGoogleMap.animateCamera(CameraUpdateFactory
							.zoomTo(9.0F));
					
					Marker TP = mGoogleMap.addMarker(new MarkerOptions()
							.position(utilis)
							.title("client")
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.app_logo)));*/
					

				
				try {

					
					addresses = geocoder.getFromLocation(latitude,
							longitude, 1);
					countryCode = ((Address) addresses.get(0))
							.getCountryCode();
					locality = ((Address) addresses.get(0))
							.getLocality();
					addressString = ((Address) addresses.get(0))
							.getAddressLine(0);
					cityString = ((Address) addresses.get(0))
							.getAddressLine(1);
					countryString = ((Address) addresses.get(0))
							.getAddressLine(2);
					
					Info_for_a_ann = (addressString + " " + cityString
							+ " " + countryString).replace("null", "");
					
					address.setHint(Info_for_a_ann);
					
				
					return;
				} catch (Exception localException) {
					Log.e("Exception ==", "" + localException);
					localException.printStackTrace();
					

				}
			}

		});
		
		address.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				  placeText = mapresult.get(position).get("description");
				  place_id = mapresult.get(position).get("place_id");
				 
				 address.setText(placeText);
					getLatLngObj = new getLatLngTask();
					getLatLngObj.execute();
				
			}
		});
		
		address.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				placesTask1 = new PlacesTask1();
				placesTask1.execute(s.toString());
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				
			}
		});

	
		next_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goNext();

			}
		});

		next_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goNext();

			}
		});

		progressBar.setProgress(80);
	}

	protected void goNext() {

		try {
			String address_name = address.getText().toString();
			
			
			if(address_name.equals("") || address_name==null){
				address_name = Info_for_a_ann;
			}

			Constants.ADDRESS_TO_SUBMIT = address_name;
			Constants.STATE_TO_SUBMIT = "";
			Constants.CITY_TO_SUBMIT = "";
			Constants.COUNTRY_TO_SUBMIT = "";

			if (address_name.trim().length() < 1) {
				address.setError("Please enter organization address.");

			}

			
			else {
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = new SubmitAnnouncement4();

				if (fragment != null) {
					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				ft.addToBackStack(null);
				ft.commit();
				// ft.commitAllowingStateLoss();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private class SpaceTokenizer implements Tokenizer {

		private final char delimiter = ' ';

		public int findTokenStart(CharSequence text, int cursor) {
			int i = cursor;

			while (i > 0 && text.charAt(i - 1) != delimiter) {
				i--;
			}
			while (i < cursor && text.charAt(i) == delimiter) {
				i++;
			}

			return i;
		}

		public int findTokenEnd(CharSequence text, int cursor) {
			int i = cursor;
			int len = text.length();

			while (i < len) {
				if (text.charAt(i) == delimiter) {
					return i;
				} else {
					i++;
				}
			}

			return len;
		}

		public CharSequence terminateToken(CharSequence text) {
			int i = text.length();
			while (i > 0 && text.charAt(i - 1) == delimiter) {
				i--;
			}

			return text;

		}

	}
	
	//*************** Places Task *************************//
	
	// Fetches all places from GooglePlaces AutoComplete Web Service
		private class PlacesTask1 extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... place) {
				// For storing data from web service
				String data = "";

				// Obtain browser key from https://code.google.com/apis/console

				String input = "";

				try {
					input = "input=" + URLEncoder.encode(place[0], "utf-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();

				}

				// place type to be searched
				String types = "types=geocode";

				// Sensor enabled
				String sensor = "sensor=false";
				// String locale =
				// getapp\.getResources().getConfiguration().locale.getCountry();
				// Building the parameters to the web service
				String parameters = input + "&" + types + "&" + sensor + "&key="
						+ API + "&components=country:" + countryCode;
				// String parameters = input+"&"+types+"&"+sensor+"&key="+API;
				// Output format
				String output = "json";

				// Building the url to the web service
				String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
						+ output + "?" + parameters;
				
				Log.e("places url==",""+url);

				try {
					// Fetching the data from we service
					data = downloadUrl(url);
				} catch (Exception e) {
					Log.d("Background Task", e.toString());
				}
				return data;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);

				// Creating ParserTask
				parserTask = new ParserTask();

				// Starting Parsing the JSON string returned by Web Service
				parserTask.execute(result);
				
			}
		}
		
		
		/** A method to download json data from url */
		private String downloadUrl(String strUrl) throws IOException {
			String data = "";
			InputStream iStream = null;
			HttpURLConnection urlConnection = null;
			try {
				URL url = new URL(strUrl);

				// Creating an http connection to communicate with url
				urlConnection = (HttpURLConnection) url.openConnection();

				// Connecting to url
				urlConnection.connect();

				// Reading data from url
				iStream = urlConnection.getInputStream();

				BufferedReader br = new BufferedReader(new InputStreamReader(
						iStream));

				StringBuffer sb = new StringBuffer();

				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

				data = sb.toString();

				br.close();

			} catch (Exception e) {
				Log.d("Exception while downloading url", e.toString());
			} finally {
				iStream.close();
				urlConnection.disconnect();
			}
			return data;
		}
		
		/** A class to parse the Google Places in JSON format */
		private class ParserTask extends
				AsyncTask<String, Integer, List<HashMap<String, String>>> {

			JSONObject jObject;

			@Override
			protected List<HashMap<String, String>> doInBackground(
					String... jsonData) {

				List<HashMap<String, String>> places = null;

				PlaceJSONParser placeJsonParser = new PlaceJSONParser();

				try {
					jObject = new JSONObject(jsonData[0]);

					// Getting the parsed data as a List construct
					places = placeJsonParser.parse(jObject);

				} catch (Exception e) {
					Log.d("Exception", e.toString());
				}
				return places;
			}

			@Override
			protected void onPostExecute(List<HashMap<String, String>> result) {
				mapresult = new ArrayList<HashMap<String, String>>();

				String[] from = new String[] { "description" };
				mapresult = result;

				int[] to = new int[] { android.R.id.text1 };

				// Creating a SimpleAdapter for the AutoCompleteTextView
				SimpleAdapter adapter = new SimpleAdapter(getActivity(), result,
						R.layout.places_listitem, from, to);

				// Setting the adapter
				
				address.setAdapter(adapter);
				address.setTokenizer(new SpaceTokenizer());
			}
		}
		
		
		/**
		 * 
		 * @author Desktop
		 * get lat long web service
		 */
		private class getLatLngTask extends AsyncTask<Void, Void, String> {
			@Override
			protected void onPreExecute() {
				db = new TransparentProgressDialog(getActivity(),
						R.drawable.loadingicon);
				db.show();
			}

			protected String doInBackground(Void... placesURL) {

				HttpClient httpclient = new DefaultHttpClient();

				String u = PLACES_URL + "key=" + API + "&" + "placeid=" + place_id;

				HttpGet httpget = new HttpGet(u);

				String result = null;
				try {

					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httpget);

					StatusLine statusLine = response.getStatusLine();

					if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						response.getEntity().writeTo(out);
						out.close();

						result = out.toString();

					} else {
						// close connection
						response.getEntity().getContent().close();
						throw new IOException(statusLine.getReasonPhrase());
					}

				} catch (Exception e) {
					Log.e("Error:", e.getMessage());
					// return result;
				}

				return result;
			}

			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				db.dismiss();
			
				try {
					JSONObject jsonObj = new JSONObject(result);

					JSONObject jsn = jsonObj.getJSONObject("result");

				
					JSONObject c1 = jsn.getJSONObject("geometry");
					JSONObject c2 = c1.getJSONObject("location");
					String lat = c2.getString("lat");
					String lng = c2.getString("lng");

					double latitude = Double.parseDouble(lat);
					double longitude = Double.parseDouble(lng);
					
					utilis = new LatLng(latitude, longitude);
					mGoogleMap.moveCamera(CameraUpdateFactory
							.newLatLng(utilis));
					mGoogleMap.animateCamera(CameraUpdateFactory
							.zoomTo(9.0F));
					
					Marker TP = mGoogleMap.addMarker(new MarkerOptions()
							.position(utilis)
							.title("Announcement")
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.app_logo)));
			
				}

				catch (Exception ae) {
					Log.e("Exception=", "=" + ae);

				}
			}
		}
		
		
		
		// ************** Places TAsk ******************************//
		
		
		public class PlacesTask extends AsyncTask<String, Void, String> {

			String placeToFind;

			public PlacesTask(String string) {
				this.placeToFind = string;
			}

			@Override
			protected String doInBackground(String... params) {
				HttpClient httpclient = new DefaultHttpClient();
				String result = null;

				try {
					/*
					 * https://maps.googleapis.com/maps/api/place/autocomplete/json?
					 * input=us&key=AIzaSyBHv_xhbxCdSUfK-g_fVG2cMlBmt7nAG5E
					 */

					HttpGet httpget = new HttpGet(
							"https://maps.googleapis.com/maps/api/place/autocomplete/json?"
									+ "input="
									+ this.placeToFind
									+ "&key=AIzaSyBHv_xhbxCdSUfK-g_fVG2cMlBmt7nAG5E");
					
					Log.e("url===",""+"https://maps.googleapis.com/maps/api/place/autocomplete/json?"
							+ "input="
							+ this.placeToFind
							+ "&key=AIzaSyBHv_xhbxCdSUfK-g_fVG2cMlBmt7nAG5E");

					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httpget);

					StatusLine statusLine = response.getStatusLine();

					if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						response.getEntity().writeTo(out);
						out.close();
						Log.i("status==", "STATUS OK");

						result = out.toString();
						//Log.i("result==", "==" + result);
					} else {
						// close connection
						response.getEntity().getContent().close();
						throw new IOException(statusLine.getReasonPhrase());
					}
				} catch (Exception e) {

					Log.i("error encountered", "==" + e);
				}

				return result;

			}

			@Override
			protected void onPostExecute(String result) {
				
				try {
					JSONObject jObj = new JSONObject(result);
		
					JSONArray PlacesArray = jObj.getJSONArray("predictions");
					
					places.clear();
					for(int i =0;i<PlacesArray.length();i++){
						places.add(PlacesArray.getJSONObject(i).getString("description"));
					}
					
					ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(
							getActivity(), R.layout.simple_spinner_item,
							R.id.text, places);
					
					  if(isState){
						state.setAdapter(dataAdapter1);
						state.setTokenizer(new SpaceTokenizer());
					} else if(isCity){
						city.setAdapter(dataAdapter1);
						city.setTokenizer(new SpaceTokenizer());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
}
