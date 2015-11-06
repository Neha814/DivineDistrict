package com.example.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.functions.Constants;
import com.example.jsimmons.Home;
import com.example.jsimmons.R;

public class SubmitAnnouncement2 extends Fragment {
	
	private View rootView;
	
	Button next_button;
	LinearLayout next_layout;
	ProgressBar progressBar;
	
	ImageView upload_img;
	ImageView camera_img;
	EditText write_post;
	
	boolean isCamera = false ,  isGallery = false;
	Bitmap takenImage;
	
	public String photoFileName;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.submit_announcement2, container, false);
		init();

		return rootView;
	}

	private void init() {
		
		Constants.imgFileGallery  = new File("");
		
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		next_button = (Button) rootView.findViewById(R.id.next_button);
		next_layout = (LinearLayout) rootView.findViewById(R.id.next_layout);
		upload_img =(ImageView) rootView.findViewById(R.id.upload_img);
		camera_img = (ImageView) rootView.findViewById(R.id.camera_img);
		write_post = (EditText) rootView.findViewById(R.id.write_post);
		
		
		
		camera_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isGallery){
					photoFileName = System.currentTimeMillis() + ".png";
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT,getPhotoFileUri(photoFileName)); // set the image file name
								isCamera = true;								

					startActivityForResult(intent, 0);
					} else {
						Toast.makeText(getActivity(), "Photo is already uploaded.", Toast.LENGTH_SHORT).show();
					}
				
			}
		});
		
		upload_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isCamera){
					Intent GaleryIntent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					isGallery = true;
					startActivityForResult(GaleryIntent, 1);
					} else {
						Toast.makeText(getActivity(), "Photo is already captured.", Toast.LENGTH_SHORT).show();
					}
			}
		});
		
		next_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GoNext();
			}
		});
		
		next_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GoNext();
			}
		});
		
		progressBar.setProgress(60);
		
	
	}
	
	public Uri getPhotoFileUri(String fileName) {
		// Get safe storage directory for photos
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
			Log.d("", "failed to create directory");
		}

		// Return the file target for the photo based on filename
		return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator
				+ fileName));
	}

	protected void GoNext() {
		
		String description = write_post.getText().toString();
		if(description.trim().length()<1){
			write_post.setError("Please enter description.");
		} else {
			
			Constants.DESCRIPTION_TO_SUBMIT = description;
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new SubmitAnnouncement3();
	
	    if(fragment != null) {
        	ft.replace(R.id.frame_layout, fragment);
       }
         else{
             ft.add(R.id.frame_layout, fragment);
            }
        ft.addToBackStack(null);		 
        ft.commit();
        //ft.commitAllowingStateLoss();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		try {
		if (requestCode == 0) {

			Uri takenPhotoUri = getPhotoFileUri(photoFileName);
			// by this point we have the camera photo on disk
			takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
			// Load the taken image into a preview
			// takenImage = Bitmap.createScaledBitmap(takenImage, 120, 120,
			// false);
			camera_img.setImageBitmap(takenImage);

			Log.e("takenPhotoUri.getPath()==", "" + takenPhotoUri.getPath());

			Constants.imgFileGallery = new File(takenPhotoUri.getPath());
			
			Constants.picturePath = takenPhotoUri.getPath();
			Log.e("imgFileGallery==", "" + Constants.imgFileGallery);
		}

		else if (requestCode == 1) {
			Uri selectedImage = data.getData();
			InputStream imageStream = null;
			try {
				imageStream = Home.appContext.openInputStream(selectedImage);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("Exception==", "" + e);
			}
			takenImage = BitmapFactory.decodeStream(imageStream);

			upload_img.setImageBitmap(takenImage);

			/**
			 * saving to file
			 */

			Uri SelectedImage = data.getData();
			String[] FilePathColumn = { MediaStore.Images.Media.DATA };

			Cursor SelectedCursor = Home.appContext.query(SelectedImage,
					FilePathColumn, null, null, null);
			SelectedCursor.moveToFirst();

			int columnIndex = SelectedCursor.getColumnIndex(FilePathColumn[0]);
			String picturePath = SelectedCursor.getString(columnIndex);
			SelectedCursor.close();

			Log.e("picturePath==", "" + picturePath);
			
			Constants.picturePath = picturePath;
			Constants.imgFileGallery = new File(picturePath);

		}
	}catch(Exception e){
		e.printStackTrace();
	}
	}

}
