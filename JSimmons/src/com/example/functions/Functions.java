package com.example.functions;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.Html;
import android.util.Log;

public class Functions {

	JSONParser json = new JSONParser();
	public static String url = "http://phphosting.osvin.net/divineDistrict/api/";

	/*
	 * login
	 */

	public HashMap register(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "signup.php?", "POST",
							localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");
				localHashMap.put("Message",
						localJSONObject.getString("Message"));
				localHashMap.put("user_id",
						localJSONObject.getString("user_id"));

				localHashMap.put("auth_key",
						localJSONObject.getString("auth_key"));
				localHashMap.put("username",
						localJSONObject.getString("username"));

				localHashMap.put("image", localJSONObject.getString("image"));

			} else {
				localHashMap.put("Status", "false");
				localHashMap.put("Message",
						localJSONObject.getString("Message"));
			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	/**
	 * Register
	 */

	public HashMap login(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "login.php?", "POST",
							localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");
				localHashMap.put("Message",
						localJSONObject.getString("Message"));
				localHashMap.put("user_id",
						localJSONObject.getString("user_id"));
				localHashMap.put("auth_key",
						localJSONObject.getString("auth_key"));
				localHashMap.put("username",
						localJSONObject.getString("username"));

				localHashMap.put("image", localJSONObject.getString("image"));

			} else {
				localHashMap.put("Status", "false");
				localHashMap.put("Message",
						localJSONObject.getString("Message"));
			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	/**
	 * NewsFeed List
	 */

	public ArrayList<HashMap<String, String>> newsFeedlist(
			ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> localArrayList1 = new ArrayList<HashMap<String, String>>();

		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "newsfeed.php?", "POST",
							localArrayList)).toString());

			String resopnse = localJSONObject.getString("Status");
			if (resopnse.equalsIgnoreCase("true")) {

				JSONArray Data = localJSONObject.getJSONArray("Data");
				for (int i = 0; i < Data.length(); i++) {
					HashMap<String, String> localhashMap = new HashMap<String, String>();
					localhashMap.put("id", Data.getJSONObject(i)
							.getString("id"));
					localhashMap.put("user_id", Data.getJSONObject(i)
							.getString("user_id"));
					localhashMap.put("title",
							Data.getJSONObject(i).getString("title"));
					localhashMap.put("description", Data.getJSONObject(i)
							.getString("description"));
					localhashMap.put("image",
							Data.getJSONObject(i).getString("image"));
					localhashMap.put("status",
							Data.getJSONObject(i).getString("status"));
					localhashMap.put("user_name", Data.getJSONObject(i)
							.getString("user_name"));
					localhashMap.put("user_image", Data.getJSONObject(i)
							.getString("user_image"));
					localhashMap.put("email",
							Data.getJSONObject(i).getString("email"));

					localhashMap.put("comment_count", Data.getJSONObject(i)
							.getString("comment_count"));
					localhashMap.put("favourite_count", Data.getJSONObject(i)
							.getString("favourite_count"));
					localhashMap.put("isLike",
							Data.getJSONObject(i).getString("isLike"));
					localhashMap.put("image",
							Data.getJSONObject(i).getString("image"));

					String org_names = "";
					JSONArray user_org = Data.getJSONObject(i).getJSONArray(
							"user_organisations");

					Constants.OrgList.clear();

					// if(user_org.length()>0){
					for (int j = 0; j < user_org.length(); j++) {

						/*
						 * org_names =
						 * org_names+","+user_org.getJSONObject(i).getString
						 * ("Organisation");
						 * Log.e("orgName====>>","if if "+"..."+org_names);
						 */

						Constants.OrgList.add(user_org.getJSONObject(j)
								.getString("Organisation"));

					}

					String OrgNames = Constants.OrgList.toString()
							.replace("[", "").replace("]", "")
							.replace(", ", ",");

					localhashMap.put("org_name", OrgNames);

					Log.e("org names===>>>", "" + OrgNames);
					Log.e("i===>>>", "" + i);
					// }

					localArrayList1.add(localhashMap);

				}

			}
			return localArrayList1;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localArrayList1;

		}

	}

	/**
	 * Category List
	 * 
	 * @param localArrayList
	 * @return
	 */

	public ArrayList<HashMap<String, String>> Categorylist(
			ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> localArrayList1 = new ArrayList<HashMap<String, String>>();

		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "getCategories.php?",
							"POST", localArrayList)).toString());

			String resopnse = localJSONObject.getString("Status");
			if (resopnse.equalsIgnoreCase("true")) {

				JSONArray Data = localJSONObject.getJSONArray("Data");
				for (int i = 0; i < Data.length(); i++) {
					HashMap<String, String> localhashMap = new HashMap<String, String>();
					localhashMap.put("id", Data.getJSONObject(i)
							.getString("id"));
					localhashMap.put("name",
							Data.getJSONObject(i).getString("name"));
					localhashMap.put("date_created", Data.getJSONObject(i)
							.getString("date_created"));
					localhashMap.put("date_modified", Data.getJSONObject(i)
							.getString("date_modified"));

					localArrayList1.add(localhashMap);

				}

			}
			return localArrayList1;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localArrayList1;

		}

	}

	/**
	 * sub category list
	 * 
	 * @param localArrayList
	 * @return
	 */

	public ArrayList<HashMap<String, String>> SubCategorylist(
			ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> localArrayList1 = new ArrayList<HashMap<String, String>>();

		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "getSubcategories.php?",
							"POST", localArrayList)).toString());

			String resopnse = localJSONObject.getString("Status");
			if (resopnse.equalsIgnoreCase("true")) {

				JSONArray Data = localJSONObject.getJSONArray("Data");
				for (int i = 0; i < Data.length(); i++) {
					HashMap<String, String> localhashMap = new HashMap<String, String>();
					localhashMap.put("id", Data.getJSONObject(i)
							.getString("id"));
					localhashMap.put("cat_id",
							Data.getJSONObject(i).getString("cat_id"));
					localhashMap.put("name",
							Data.getJSONObject(i).getString("name"));
					localhashMap.put("date_created", Data.getJSONObject(i)
							.getString("date_created"));
					localhashMap.put("date_modified", Data.getJSONObject(i)
							.getString("date_modified"));

					localArrayList1.add(localhashMap);

				}

			}
			return localArrayList1;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localArrayList1;

		}

	}

	/*
	 * Announcemnt List
	 */

	public ArrayList<HashMap<String, String>> getAnnouncemntList(
			ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> localArrayList1 = new ArrayList<HashMap<String, String>>();

		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url
							+ "getAnnouncementsBySubcategory.php?", "POST",
							localArrayList)).toString());

			String resopnse = localJSONObject.getString("Status");
			if (resopnse.equalsIgnoreCase("true")) {

				JSONArray Data = localJSONObject.getJSONArray("Data");
				for (int i = 0; i < Data.length(); i++) {
					HashMap<String, String> localhashMap = new HashMap<String, String>();
					localhashMap.put("id", Data.getJSONObject(i)
							.getString("id"));

					localhashMap.put("cat_id",
							Data.getJSONObject(i).getString("cat_id"));
					localhashMap.put("subcat_id", Data.getJSONObject(i)
							.getString("subcat_id"));
					localhashMap.put("title",
							Data.getJSONObject(i).getString("title"));
					localhashMap.put("description", Data.getJSONObject(i)
							.getString("description"));
					localhashMap.put("image",
							Data.getJSONObject(i).getString("image"));
					localhashMap.put("address", Data.getJSONObject(i)
							.getString("address"));

					localhashMap.put("city",
							Data.getJSONObject(i).getString("city"));
					localhashMap.put("state",
							Data.getJSONObject(i).getString("state"));
					localhashMap.put("country", Data.getJSONObject(i)
							.getString("country"));
					localhashMap.put("latitude", Data.getJSONObject(i)
							.getString("latitude"));
					localhashMap.put("longitute", Data.getJSONObject(i)
							.getString("longitute"));
					localhashMap.put("start_date", Data.getJSONObject(i)
							.getString("start_date"));
					localhashMap.put("end_date", Data.getJSONObject(i)
							.getString("end_date"));
					localhashMap.put("price",
							Data.getJSONObject(i).getString("price"));

					localhashMap.put("category", Data.getJSONObject(i)
							.getString("category"));
					localhashMap.put("sub_category", Data.getJSONObject(i)
							.getString("sub_category"));
					localhashMap.put("organisation", Data.getJSONObject(i)
							.getString("organisation"));

					localhashMap.put("distance", Data.getJSONObject(i)
							.getString("distance"));

					localArrayList1.add(localhashMap);

				}

			}
			return localArrayList1;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localArrayList1;

		}

	}

	/**
	 * Announcment Detail
	 * 
	 * @param localArrayList
	 * @return
	 */

	public HashMap getAnnouncemntDetail(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "announcementDetail.php?",
							"POST", localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				JSONObject data = localJSONObject.getJSONObject("data");
				localHashMap.put("Status", "true");
				localHashMap.put("id", data.getString("id"));
				// localHashMap.put("user_id", data.getString("user_id"));
				localHashMap.put("org_id", data.getString("org_id"));
				localHashMap.put("cat_id", data.getString("cat_id"));
				localHashMap.put("subcat_id", data.getString("subcat_id"));
				localHashMap.put("title", data.getString("title"));
				localHashMap.put("description", data.getString("description"));
				localHashMap.put("image", data.getString("image"));
				localHashMap.put("address", data.getString("address"));
				localHashMap.put("city", data.getString("city"));
				localHashMap.put("state", data.getString("state"));
				localHashMap.put("country", data.getString("country"));
				localHashMap.put("latitude", data.getString("latitude"));
				localHashMap.put("longitute", data.getString("longitute"));
				localHashMap.put("start_date", data.getString("start_date"));
				localHashMap.put("end_date", data.getString("end_date"));
				localHashMap.put("website", data.getString("website"));
				localHashMap.put("price", data.getString("price"));
				localHashMap.put("category", data.getString("category"));
				localHashMap
						.put("sub_category", data.getString("sub_category"));
				localHashMap
						.put("organisation", data.getString("organisation"));
				// localHashMap.put("username", data.getString("username"));

			} else {
				localHashMap.put("Status", "false");
				localHashMap.put("Message",
						localJSONObject.getString("Message"));
			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	/**
	 * Set reminder APi
	 * 
	 * @param localArrayList
	 * @return
	 */

	public HashMap setReminder(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "attendance.php?", "POST",
							localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");
				localHashMap.put("Message",
						localJSONObject.getString("Message"));

			} else {
				localHashMap.put("Status", "false");
				localHashMap.put("Message",
						localJSONObject.getString("Message"));

			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	/**
	 * Submit announcemnt APi
	 * 
	 * @param localArrayList
	 * @return
	 */

	public HashMap SubmitAnnouncement(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "joinAnnouncement.php?",
							"POST", localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");

			} else {
				localHashMap.put("Status", "false");

			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	/**
	 * Like APi
	 * 
	 * @param localArrayList
	 * @return
	 */

	public HashMap Like(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "favourite.php?", "POST",
							localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");
				localHashMap.put("Message",
						localJSONObject.getString("Message"));

			} else {
				localHashMap.put("Status", "false");
				localHashMap.put("Message",
						localJSONObject.getString("Message"));

			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	/**
	 * Comment APi
	 */
	public ArrayList<HashMap<String, String>> GetComments(
			ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> localArrayList1 = new ArrayList<HashMap<String, String>>();

		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "newsfeedDetail.php?",
							"POST", localArrayList)).toString());

			String resopnse = localJSONObject.getString("Status");
			if (resopnse.equalsIgnoreCase("true")) {

				JSONObject Data = localJSONObject.getJSONObject("data");
				JSONArray comments = Data.getJSONArray("comments");
				for (int i = 0; i < comments.length(); i++) {
					HashMap<String, String> localhashMap = new HashMap<String, String>();
					localhashMap.put("comment", comments.getJSONObject(i)
							.getString("comment"));
					localhashMap.put("user_id", comments.getJSONObject(i)
							.getString("user_id"));
					localhashMap.put("username", comments.getJSONObject(i)
							.getString("username"));
					localhashMap.put("user_image", comments.getJSONObject(i)
							.getString("user_image"));

					localArrayList1.add(localhashMap);

				}

			}
			return localArrayList1;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localArrayList1;

		}

	}

	/**
	 * Send Comment
	 * 
	 * @param localArrayList
	 * @return
	 */

	public HashMap SendComment(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "comment.php?", "POST",
							localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");
				localHashMap.put("Message",
						localJSONObject.getString("Message"));

			} else {
				localHashMap.put("Status", "false");
				localHashMap.put("Message",
						localJSONObject.getString("Message"));

			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	/**
	 * Post news APi
	 */

	public HashMap postNews(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "addNews.php?", "POST",
							localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");
				localHashMap.put("Message",
						localJSONObject.getString("Message"));

			} else {
				localHashMap.put("Status", "false");
				localHashMap.put("Message",
						localJSONObject.getString("Message"));

			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	public ArrayList<HashMap<String, String>> SpiritualGrowth(
			ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> localArrayList1 = new ArrayList<HashMap<String, String>>();

		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "addSpiritualGrowth.php?",
							"POST", localArrayList)).toString());

			String resopnse = localJSONObject.getString("Status");
			if (resopnse.equalsIgnoreCase("true")) {

				JSONArray Data = localJSONObject.getJSONArray("data");
				for (int i = 0; i < Data.length(); i++) {
					HashMap<String, String> localhashMap = new HashMap<String, String>();
					localhashMap.put("id", Data.getJSONObject(i)
							.getString("id"));
					localhashMap.put("name",
							Data.getJSONObject(i).getString("name"));
					localhashMap.put("count",
							Data.getJSONObject(i).getString("count"));

					localArrayList1.add(localhashMap);

				}

			}
			return localArrayList1;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localArrayList1;

		}

	}

	/**
	 * All announcemnt List
	 * 
	 * @param localArrayList
	 * @return
	 */

	public ArrayList<HashMap<String, String>> announcmenetList(
			ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> localArrayList1 = new ArrayList<HashMap<String, String>>();

		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "getAnnouncement.php?",
							"POST", localArrayList)).toString());

			String resopnse = localJSONObject.getString("Status");
			if (resopnse.equalsIgnoreCase("true")) {

				JSONArray Data = localJSONObject.getJSONArray("Data");
				for (int i = 0; i < Data.length(); i++) {
					HashMap<String, String> localhashMap = new HashMap<String, String>();
					localhashMap.put("id", Data.getJSONObject(i)
							.getString("id"));

					localhashMap.put("org_id",
							Data.getJSONObject(i).getString("org_id"));
					localhashMap.put("title",
							Data.getJSONObject(i).getString("title"));
					localhashMap.put("description", Data.getJSONObject(i)
							.getString("description"));
					localhashMap.put("latitude", Data.getJSONObject(i)
							.getString("latitude"));
					localhashMap.put("longitute", Data.getJSONObject(i)
							.getString("longitute"));

					localArrayList1.add(localhashMap);

				}

			}
			return localArrayList1;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localArrayList1;

		}

	}

	/**
	 * Set remider APi
	 * 
	 * @param localArrayList
	 * @return
	 */

	public HashMap SetReminder(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "reminderOptions.php?",
							"POST", localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");
				localHashMap.put("Message",
						localJSONObject.getString("Message"));

			} else {
				localHashMap.put("Status", "false");
				localHashMap.put("Message",
						localJSONObject.getString("Message"));
			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	/**
	 * calendar APi
	 * 
	 * @param localArrayList
	 * @return
	 */

	public ArrayList<HashMap<String, String>> calendarapi(
			ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> localArrayList1 = new ArrayList<HashMap<String, String>>();

		try {
			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "myCalendar.php?", "POST",
							localArrayList)).toString());

			String resopnse = localJSONObject.getString("Status");
			if (resopnse.equalsIgnoreCase("true")) {

				JSONArray Data = localJSONObject.getJSONArray("data");
				for (int i = 0; i < Data.length(); i++) {
					HashMap<String, String> localhashMap = new HashMap<String, String>();
					localhashMap.put("id", Data.getJSONObject(i)
							.getString("id"));
					localhashMap.put("title",
							Data.getJSONObject(i).getString("title"));
					localhashMap.put("start_date", Data.getJSONObject(i)
							.getString("start_date"));
					localhashMap.put("end_date", Data.getJSONObject(i)
							.getString("end_date"));
					localhashMap.put("category", Data.getJSONObject(i)
							.getString("category"));
					localhashMap.put("sub_category", Data.getJSONObject(i)
							.getString("sub_category"));
					localhashMap.put("attendance_status", Data.getJSONObject(i)
							.getString("attendance_status"));

					localArrayList1.add(localhashMap);

				}

			}
			return localArrayList1;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localArrayList1;
		}

	}

	/**
	 * organization list
	 * 
	 * @param localArrayList
	 * @return
	 */

	public ArrayList<HashMap<String, String>> OrganizationListAPI(
			ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> localArrayList1 = new ArrayList<HashMap<String, String>>();

		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "getOrganisations.php?",
							"POST", localArrayList)).toString());

			String resopnse = localJSONObject.getString("Status");
			if (resopnse.equalsIgnoreCase("true")) {

				JSONArray Data = localJSONObject.getJSONArray("Data");
				for (int i = 0; i < Data.length(); i++) {
					HashMap<String, String> localhashMap = new HashMap<String, String>();
					localhashMap.put("id", Data.getJSONObject(i)
							.getString("id"));
					localhashMap.put("username", Data.getJSONObject(i)
							.getString("name"));
					localhashMap.put("isLike",
							Data.getJSONObject(i).getString("isLike"));

					localArrayList1.add(localhashMap);

				}

			}
			return localArrayList1;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localArrayList1;

		}

	}

	/**
	 * all subcateogries listing
	 */

	public ArrayList<HashMap<String, String>> SubCategory(
			ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> localArrayList1 = new ArrayList<HashMap<String, String>>();

		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "getSubcategories.php?",
							"POST", localArrayList)).toString());

			String resopnse = localJSONObject.getString("Status");
			if (resopnse.equalsIgnoreCase("true")) {

				JSONArray Data = localJSONObject.getJSONArray("Data");
				for (int i = 0; i < Data.length(); i++) {
					HashMap<String, String> localhashMap = new HashMap<String, String>();
					localhashMap.put("id", Data.getJSONObject(i)
							.getString("id"));
					localhashMap.put("cat_id",
							Data.getJSONObject(i).getString("cat_id"));
					localhashMap.put("name",
							Data.getJSONObject(i).getString("name"));
					localhashMap.put("date_created", Data.getJSONObject(i)
							.getString("date_created"));
					localhashMap.put("date_modified", Data.getJSONObject(i)
							.getString("date_modified"));

					localArrayList1.add(localhashMap);

				}

			}
			return localArrayList1;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localArrayList1;

		}

	}

	/**
	 * to submit location (radius in miles)
	 */

	public HashMap SubmitLocation(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "settings.php?", "POST",
							localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");

			} else {
				localHashMap.put("Status", "false");

			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	/**
	 * My organzation list
	 * 
	 * @param localArrayList
	 * @return
	 */

	public ArrayList<HashMap<String, String>> getOrgList(
			ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> localArrayList1 = new ArrayList<HashMap<String, String>>();

		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "myOrganisation.php?",
							"POST", localArrayList)).toString());

			String resopnse = localJSONObject.getString("Status");
			if (resopnse.equalsIgnoreCase("true")) {

				JSONArray Data = localJSONObject.getJSONArray("data");
				for (int i = 0; i < Data.length(); i++) {
					HashMap<String, String> localhashMap = new HashMap<String, String>();
					localhashMap.put("org_id",
							Data.getJSONObject(i).getString("org_id"));
					localhashMap.put("Organisation", Data.getJSONObject(i)
							.getString("Organisation"));

					localArrayList1.add(localhashMap);

				}

			}
			return localArrayList1;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localArrayList1;

		}

	}

	/**
	 * add organization
	 * 
	 * @param localArrayList
	 * @return
	 */

	public HashMap addOrg(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "addOrganisation.php?",
							"POST", localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");
				localHashMap.put("Message", "Message");

			} else {
				localHashMap.put("Status", "false");
				localHashMap.put("Message", "Message");

			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	/**
	 * Add alert APi
	 * 
	 * @param localArrayList
	 * @return
	 */

	public HashMap addAlert(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "userAlert.php?", "POST",
							localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");
				localHashMap.put("Message", "Message");

			} else {
				localHashMap.put("Status", "false");
				localHashMap.put("Message", "Message");

			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	/**
	 * 
	 * @param localArrayList
	 * @return
	 */

	public HashMap getAlert(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "getUserAlerts.php?",
							"POST", localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");
				localHashMap.put("Message", "Message");

				JSONObject data = localJSONObject.getJSONObject("data");

				localHashMap.put("keywords", data.getString("keywords"));
				localHashMap.put("id", data.getString("id"));

			} else {
				localHashMap.put("Status", "false");
				localHashMap.put("Message", "Message");

			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	/**
	 * Delete org
	 */

	public HashMap deleteOrg(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "deleteOrg.php?", "POST",
							localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");
				localHashMap.put("Message", "Message");

			} else {
				localHashMap.put("Status", "false");
				localHashMap.put("Message", "Message");

			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	/**
	 * My announcemnts list
	 */

	public ArrayList<HashMap<String, String>> getAnnList(
			ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> localArrayList1 = new ArrayList<HashMap<String, String>>();

		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "myAnnouncements.php?",
							"POST", localArrayList)).toString());

			String resopnse = localJSONObject.getString("Status");
			if (resopnse.equalsIgnoreCase("true")) {

				JSONArray Data = localJSONObject.getJSONArray("data");
				for (int i = 0; i < Data.length(); i++) {
					HashMap<String, String> localhashMap = new HashMap<String, String>();
					localhashMap.put("id", Data.getJSONObject(i)
							.getString("id"));
					localhashMap.put("title",
							Data.getJSONObject(i).getString("title"));
					localhashMap.put("description", Data.getJSONObject(i)
							.getString("description"));
					localhashMap.put("image",
							Data.getJSONObject(i).getString("image"));
					localhashMap.put("city",
							Data.getJSONObject(i).getString("city"));
					localhashMap.put("address", Data.getJSONObject(i)
							.getString("address"));

					localhashMap.put("state",
							Data.getJSONObject(i).getString("state"));
					localhashMap.put("country", Data.getJSONObject(i)
							.getString("country"));
					localhashMap.put("latitude", Data.getJSONObject(i)
							.getString("latitude"));
					localhashMap.put("longitute", Data.getJSONObject(i)
							.getString("longitute"));

					localhashMap.put("start_date", Data.getJSONObject(i)
							.getString("start_date"));
					localhashMap.put("end_date", Data.getJSONObject(i)
							.getString("end_date"));
					localhashMap.put("price",
							Data.getJSONObject(i).getString("price"));

					localArrayList1.add(localhashMap);

				}

			}
			return localArrayList1;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localArrayList1;

		}

	}

	/**
	 * Join announcement
	 * 
	 * @param localArrayList
	 * @return
	 */

	public HashMap JoinAnnouncement(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "joinAnnouncement.php?",
							"POST", localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");

			} else {
				localHashMap.put("Status", "false");

			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	public HashMap Share(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "orgShares.php?", "POST",
							localArrayList)).toString());

			String status = localJSONObject.getString("status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");

			} else {
				localHashMap.put("Status", "false");

			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	public HashMap ViewAPI(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "orgViews.php?", "POST",
							localArrayList)).toString());

			String status = localJSONObject.getString("status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");

			} else {
				localHashMap.put("Status", "false");

			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}

	/**
	 * get announcemnt list on the basis of organizations id
	 * 
	 * @param localArrayList
	 * @return
	 */

	public ArrayList getAnnouncmentOrganization(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url
							+ "getAnnouncementsByOrganistion.php?", "POST",
							localArrayList)).toString());

			String status = localJSONObject.getString("Status");
			if (status.equalsIgnoreCase("true")) {
				localHashMap.put("Status", "true");
				localHashMap.put("Message", "Message");

				JSONArray data = localJSONObject.getJSONArray("Data");

				for (int i = 0; i < data.length(); i++) {
					HashMap<String, String> localhashMap = new HashMap<String, String>();

					localHashMap.put("title",
							data.getJSONObject(i).getString("title"));
					localHashMap.put("id", data.getJSONObject(i)
							.getString("id"));
					localHashMap.put("description", data.getJSONObject(i)
							.getString("description"));
					localHashMap.put("image",
							data.getJSONObject(i).getString("image"));
				}

				locallist.add(localHashMap);

			} else {

			}
			return locallist;

		} catch (Exception ae) {
			ae.printStackTrace();
			return locallist;

		}

	}

	public ArrayList<HashMap<String, String>> getNotification(
			ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> localArrayList1 = new ArrayList<HashMap<String, String>>();

		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "getNotification.php?",
							"POST", localArrayList)).toString());

			String resopnse = localJSONObject.getString("Status");
			if (resopnse.equalsIgnoreCase("true")) {

				JSONArray Data = localJSONObject.getJSONArray("Data");
				for (int i = 0; i < Data.length(); i++) {
					HashMap<String, String> localhashMap = new HashMap<String, String>();

					localhashMap.put("subject", Data.getJSONObject(i)
							.getString("subject"));
					localhashMap.put("message", Data.getJSONObject(i)
							.getString("message"));

					localArrayList1.add(localhashMap);

				}

			}
			return localArrayList1;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localArrayList1;

		}

	}

}
