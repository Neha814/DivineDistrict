package com.example.fragments;

import com.example.functions.Constants;
import com.example.jsimmons.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MapFragment extends Fragment {
	
	private View rootView;
	String title;
	TextView titleTV;
	SupportMapFragment fm;
	GoogleMap mGoogleMap;
	LatLng utilis;
	Double lat , lng;
	ImageView navigation;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.map_fragment, container, false);
		
		titleTV = (TextView) rootView.findViewById(R.id.title);
		fm = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map));
		
		navigation = (ImageView) rootView.findViewById(R.id.navigation);
		
		title = Constants.ANNOUNCEMENT_TITLE;
		lat = Constants.LATITIUDE;
		lng = Constants.LONGITUDE;
		
		utilis = new LatLng(lat, lng);
		
		
		fm.getMap();
		this.mGoogleMap = this.fm.getMap();
		mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
		mGoogleMap.setMyLocationEnabled(true);
		
		Marker TP = mGoogleMap.addMarker(new MarkerOptions()
		.position(utilis)
		.title("Announcement"));
		
		
		mGoogleMap.moveCamera(CameraUpdateFactory
				.newLatLng(utilis));
		mGoogleMap.animateCamera(CameraUpdateFactory
				.zoomTo(9.0F));
		
		navigation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?saddr="
								+ Constants.MY_LATITIUDE + ","
								+ Constants.MY_LONGITUDE + "&daddr=" +Constants.LATITIUDE
								+ "," +Constants.LONGITUDE));
				startActivity(intent);
			}
		});
		
		mGoogleMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
			
			@Override
			public void onMyLocationChange(Location arg0) {
				double latitude = arg0.getLatitude();
				double longitude = arg0.getLongitude();
			/*	utilis = new LatLng(latitude, longitude);*/
			/*	mGoogleMap.moveCamera(CameraUpdateFactory
						.newLatLng(utilis));
				mGoogleMap.animateCamera(CameraUpdateFactory
						.zoomTo(9.0F));*/

				/*Marker TP = mGoogleMap.addMarker(new MarkerOptions()
						.position(utilis)
						.title("my Position"));*/
				
			}
		});
		
		titleTV.setText(title);
		return rootView;
	}

}
