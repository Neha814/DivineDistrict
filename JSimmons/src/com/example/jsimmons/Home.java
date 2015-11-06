package com.example.jsimmons;

import java.io.InputStream;
import java.net.URL;

import com.example.fragments.*;
import com.example.functions.Constants;
import com.example.jsimmons.R;
import com.macrew.imageloader.ImageLoader;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends FragmentActivity {

	ActionBarDrawerToggle actionBarDrawerToggle;
	SharedPreferences sp;
	DrawerLayout drawerLayout;
	public static ContentResolver appContext;
	Button menu_button;
	ListView left_drawer;
	String[] lvMenuItems1 = new String[10];
	MyListAdapter mAdapter;
	Integer[] images = { R.drawable.menu_profile_pic, R.drawable.sponsored,
			R.drawable.find_anouncments, R.drawable.submit_anouncments,
			R.drawable.find_anouncments, R.drawable.spritual_growth,
			R.drawable.calendar, R.drawable.organizations, R.drawable.reminder,
			R.drawable.logout };

	public String[] lvMenuItems2;

	public Integer[] images1;

	Boolean ifNotLogout = false;

	boolean inHome;

	public ImageLoader imageLoader;

	ImageButton post_header, location_header, notification_header,
			search_header;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		imageLoader = new ImageLoader(getApplicationContext());

		Editor e = sp.edit();
		e.putBoolean("inHome", true);
		e.commit();

		IntentFilter intentFilter = new IntentFilter(
				"android.intent.action.MAIN");
		registerReceiver(mHandleMessageReceiver, intentFilter);

		Constants.AUTH_KEY = sp.getString("auth_key", "");
		Constants.USER_ID = sp.getString("user_id", "");
		Constants.USERNAME = sp.getString("username", "");

		Log.e("profile pic=================", "" + sp.getString("image", ""));
		if (sp.getString("image", "").equals(null)
				|| sp.getString("image", "").equalsIgnoreCase("")) {
			Constants.PROFILE_PIC_URL = Constants.CLIENT_IMAGE;

		} else {
			Constants.PROFILE_PIC_URL = sp.getString("image", "");
		}

		inIT();

	}

	private BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("<>BROADCAST<>", "<>RECEIVER<>");
			String newMessage = intent.getExtras().getString("message");

			Log.i("BROADCAST MESSAGE", "===============");
			Log.i("BROADCAST NEW MESSAGE", "==" + newMessage);

			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message depending upon your app
			 * requirement For now i am just displaying it on the screen
			 * */

			// Releasing wake lock
			WakeLocker.release();
		}
	};

	private void inIT() {
		menu_button = (Button) findViewById(R.id.menu_button);
		drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
		left_drawer = (ListView) findViewById(R.id.left_drawer);

		appContext = getContentResolver();

		actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.string.app_name, R.string.app_name) {
			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {

			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {

			}
		};

		lvMenuItems1[0] = "News";
		lvMenuItems1[1] = "Sponsered";
		lvMenuItems1[2] = "Find Announcements";
		lvMenuItems1[3] = "Submit Announcements";
		lvMenuItems1[4] = "My Announcements";

		lvMenuItems1[5] = "My Spiritual Growth";
		lvMenuItems1[6] = "My Calendar";
		lvMenuItems1[7] = "My Organizations";
		lvMenuItems1[8] = "My Reminder Settings";
		lvMenuItems1[9] = "Logout";

		LayoutInflater inflater = getLayoutInflater();
		ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header,
				left_drawer, false);

		left_drawer.addHeaderView(header, null, false);

		post_header = (ImageButton) header.findViewById(R.id.post_header);
		location_header = (ImageButton) header
				.findViewById(R.id.location_header);
		notification_header = (ImageButton) header
				.findViewById(R.id.notification_header);
		search_header = (ImageButton) header.findViewById(R.id.search_header);

		post_header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawer(left_drawer);
				FragmentManager fm = Home.this.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = new PostFragment();

				if (fragment != null) {
					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				// /ft.addToBackStack(null);
				// ft.commit();
				ft.commitAllowingStateLoss();
			}
		});

		location_header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawer(left_drawer);
				FragmentManager fm = Home.this.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = new LocationSettingFragment();

				if (fragment != null) {
					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				// /ft.addToBackStack(null);
				// ft.commit();
				ft.commitAllowingStateLoss();
			}
		});
		
		search_header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawer(left_drawer);
				FragmentManager fm = Home.this.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = new MyOrganization();

				if (fragment != null) {
					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				// /ft.addToBackStack(null);
				// ft.commit();
				ft.commitAllowingStateLoss();
			}
		});

		notification_header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawer(left_drawer);
				FragmentManager fm = Home.this.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				//Fragment fragment = new MyReminders();
				Fragment fragment = new Notification();

				if (fragment != null) {
					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				// /ft.addToBackStack(null);
				// ft.commit();
				ft.commitAllowingStateLoss();
			}
		});

		mAdapter = new MyListAdapter(lvMenuItems1, images, Home.this);
		left_drawer.setAdapter(mAdapter);
		drawerLayout.setDrawerListener(actionBarDrawerToggle);

		left_drawer
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						onMenuItemClick(parent, view, position, id);
						view.setSelected(true);
					}

					private void onMenuItemClick(AdapterView<?> parent,
							View view, int position, long id) {
						Log.e("listivew item position==", "" + position);
						// String selectedItem = lvMenuItems1[position];
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								view.getApplicationWindowToken(), 0);
						FragmentManager fm = Home.this
								.getSupportFragmentManager();
						FragmentTransaction ft = fm.beginTransaction();
						Fragment fragment = null;
						if (position == 1) {
							fragment = new NewsFeedFragment();
							ifNotLogout = false;
						}

						else if (position == 3) {
							fragment = new FindAnnouncementCategoryFragment();
							ifNotLogout = false;
						} else if (position == 4) {
							fragment = new SubmitAnnouncement1();
							ifNotLogout = false;
						} else if (position == 5) {
							fragment = new MyAnnouncements();
							ifNotLogout = false;
						} else if (position == 6) {
							fragment = new MySpiritualGrowth();
							ifNotLogout = false;
						} else if (position == 7) {
							fragment = new MyCalendar();
							ifNotLogout = false;
						} else if (position == 8) {
							fragment = new MyOrganization();
							ifNotLogout = false;
						} else if (position == 9) {
							fragment = new MyReminders();
							ifNotLogout = false;
						} else if (position == 10) {
							ifNotLogout = true;
							showLogoutDialog();
							// fragment = new NewsFeedFragment();
						}
						try {
							if (!ifNotLogout) {
								try {
									fm.popBackStack(
											null,
											FragmentManager.POP_BACK_STACK_INCLUSIVE);
								} catch (Exception e) {
									Toast.makeText(getApplicationContext(),
											"stack problem", Toast.LENGTH_SHORT)
											.show();
								}
								drawerLayout.closeDrawer(left_drawer);

								// addInitialFragmnet();
								if (fragment != null) {
									ft.replace(R.id.frame_layout, fragment);
								} else {
									ft.add(R.id.frame_layout, fragment);
								}
								ft.addToBackStack(null);
								ft.commit();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				});

		menu_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(left_drawer);
			}
		});

		try {
			if (Constants.TYPE_NOTIFICATION.equalsIgnoreCase("organisation")) {
				FragmentManager fm = Home.this.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = null;

				fragment = new MyOrganization();

				if (fragment != null) {

					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				// ft.addToBackStack(null);
				ft.commit();
			} else if (Constants.TYPE_NOTIFICATION
					.equalsIgnoreCase("announcement")) {
				FragmentManager fm = Home.this.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = null;

				fragment = new MyAnnouncements();

				if (fragment != null) {

					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				// ft.addToBackStack(null);
				ft.commit();
			} else {
				// Add FragmentMain as the initial fragment
				FragmentManager fm = Home.this.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = null;

				fragment = new NewsFeedFragment();

				if (fragment != null) {

					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				// ft.addToBackStack(null);
				ft.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();

			FragmentManager fm = Home.this.getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			Fragment fragment = null;

			fragment = new NewsFeedFragment();

			if (fragment != null) {

				ft.replace(R.id.frame_layout, fragment);
			} else {
				ft.add(R.id.frame_layout, fragment);
			}
			 ft.addToBackStack(null);
			ft.commit();
		}
	}

	protected void addInitialFragmnet() {
		FragmentManager fm = Home.this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = null;

		fragment = new NewsFeedFragment();

		if (fragment != null) {

			ft.replace(R.id.frame_layout, fragment);
		} else {
			ft.add(R.id.frame_layout, fragment);
		}
		 ft.addToBackStack(null);
		ft.commit();
	}

	protected void showLogoutDialog() {
		final Dialog dialog;
		dialog = new Dialog(Home.this);
		dialog.setCancelable(false);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);

		Drawable d = new ColorDrawable(Color.BLACK);
		d.setAlpha(0);
		dialog.getWindow().setBackgroundDrawable(d);

		Button yes, no;

		dialog.setContentView(R.layout.logout);
		yes = (Button) dialog.findViewById(R.id.yes);
		no = (Button) dialog.findViewById(R.id.no);

		yes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Editor e = sp.edit();
				e.putBoolean("inHome", false);
				e.commit();
				Intent i = new Intent(Home.this, Login.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();

				dialog.dismiss();
			}
		});

		no.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		dialog.show();
	}

	class MyListAdapter extends BaseAdapter {
		Resources res = getResources();

		LayoutInflater mInflater = null;

		public MyListAdapter(String[] lvMenuItems1, Integer[] images,
				Home context) {

			lvMenuItems2 = lvMenuItems1;
			images1 = images;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {

			return images1.length;
		}

		@Override
		public Object getItem(int position) {

			return images1[position];
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.menu_list_item, null);
				holder.textview = (TextView) convertView
						.findViewById(R.id.text);
				holder.text2 = (TextView) convertView.findViewById(R.id.text2);
				holder.image = (ImageView) convertView.findViewById(R.id.image);
				holder.rr1 = (RelativeLayout) convertView
						.findViewById(R.id.rr1);
				holder.view = (View) convertView.findViewById(R.id.view);

			} else {
				holder = (ViewHolder) convertView.getTag();

			}
			try {
				holder.textview.setText(lvMenuItems2[position]);
				holder.image.setImageResource(images1[position]);
				if (position == 0) {

					Log.e("Constants", "");

					holder.text2.setText(Constants.USERNAME);
					holder.textview.setTypeface(null, Typeface.BOLD);
					holder.rr1.setBackgroundColor(Color.parseColor("#213e9a"));
					
					Log.e("URL=======",""+Constants.PROFILE_PIC_URL);
					if (!Constants.PROFILE_PIC_URL.equals("")) {
						imageLoader.DisplayImage("http://"+Constants.PROFILE_PIC_URL,
								R.drawable.profile_pic, holder.image);
					} else {
						holder.image.setImageResource(R.drawable.profile_pic);
					}
					holder.view.setVisibility(View.VISIBLE);
				} else if (position == 1) {
					holder.text2.setText("loreum ipsum");
					holder.textview.setTypeface(null, Typeface.BOLD);
					holder.rr1.setBackgroundColor(Color.parseColor("#213e9a"));
					holder.view.setVisibility(View.GONE);
				} else if (position > 1) {
					holder.text2.setVisibility(View.GONE);
					holder.textview.setTypeface(null, Typeface.NORMAL);
					holder.rr1.setBackgroundColor(Color.parseColor("#3853a4"));
					holder.view.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}

	}

	class ViewHolder {
		TextView textview, text2;
		RelativeLayout rr1;
		ImageView image;
		View view;

	}

	@Override
	public void onBackPressed() {

		try {

			int count = getFragmentManager().getBackStackEntryCount();

			if (count == 0) {
				super.onBackPressed();
				// additional code
			} else {
				getFragmentManager().popBackStack();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
