package com.example.fragments;

import com.example.functions.Constants;
import com.example.jsimmons.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.macrew.imageloader.ImageLoader;
import com.macrew.imageloader.ImageLoaderPic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyAnnoncemnetDetail extends Fragment {

	private View rootView;

	TextView title;
	ImageView image;
	TextView description;
	TextView address, date, price;

	public ImageLoaderPic imageLoader;

	SupportMapFragment fm;
	GoogleMap mGoogleMap;
	LatLng utilis;
	Double lat, lng;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.myannouncement_detail, container,
				false);
		init();

		return rootView;
	}

	private void init() {

		imageLoader = new ImageLoaderPic(getActivity());

		title = (TextView) rootView.findViewById(R.id.title);
		image = (ImageView) rootView.findViewById(R.id.image);
		description = (TextView) rootView.findViewById(R.id.description);
		address = (TextView) rootView.findViewById(R.id.address);
		date = (TextView) rootView.findViewById(R.id.date);
		price = (TextView) rootView.findViewById(R.id.price);

		title.setText(Constants.MY_ANN_TITLE);
		description.setText(Constants.MY_ANN_DES);
		address.setText(Constants.MY_ANN_ADDRESS);
		date.setText(Constants.MY_ANN_DATE);
		price.setText(Constants.MY_ANN_PRICE);
		
		Log.e("image====",""+Constants.MY_ANN_IMAGE);

		imageLoader.DisplayImage("http://"+Constants.MY_ANN_IMAGE,
				R.drawable.sample_post_image, image);

		fm = ((SupportMapFragment) getChildFragmentManager().findFragmentById(
				R.id.map));
	}

}
