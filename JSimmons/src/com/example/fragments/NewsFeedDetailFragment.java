package com.example.fragments;

import com.example.functions.Constants;
import com.example.jsimmons.R;
import com.macrew.imageloader.ImageLoader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class NewsFeedDetailFragment extends Fragment {
	
	private View rootView;
	
	public ImageLoader imageLoader;
	ImageView image;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.newsfedd_detail, container, false);
		
		imageLoader = new ImageLoader(getActivity());
		init();

		return rootView;
	}

	private void init() {
		image = (ImageView) rootView.findViewById(R.id.image);
		imageLoader.DisplayImage(Constants.IMAGE_LINK, R.drawable.main_bg, image);
	}

}
