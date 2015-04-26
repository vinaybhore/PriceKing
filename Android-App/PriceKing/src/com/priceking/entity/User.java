package com.priceking.entity;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.priceking.utils.Constants;

/**
 * User Entity
 * 
 * @author DEVEN
 * 
 */
public class User implements Entitiy, Parcelable {
	private String firstname;
	private String lastname;
	private String username;
	private String email;
	private String phone;
	private String password;

	public User() {

	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public JSONObject serializeJSON() throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(Constants.FIRST_NAME, firstname);
		jsonObject.put(Constants.LAST_NAME, lastname);
		jsonObject.put(Constants.USER_NAME, username);
		jsonObject.put(Constants.EMAIL, email);
		jsonObject.put(Constants.PHONE, phone);
		jsonObject.put(Constants.PASSWORD, password);
		return jsonObject;
	}

	public JSONObject serializeSignIn() throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(Constants.USER_NAME, username);
		jsonObject.put(Constants.PASSWORD, password);
		return jsonObject;
	}

	/**
	 * Method used to deserialize json for User object
	 */
	@Override
	public void deserializeJSON(JSONObject user) throws Exception {

		this.setFirstname(user.has("firstname") ? user.getString("firstname")
				: "");
		this.setLastname(user.has("lastname") ? user.getString("lastname") : "");
		this.setUsername(user.has("username") ? user.getString("username") : "");
		this.setEmail(user.has("email") ? user.getString("email") : "");
		this.setPhone(user.has("phone") ? user.getString("phone") : "");
		this.setPassword(user.has("password") ? user.getString("password") : "");

	}

	/**
	 * 
	 * @return creator
	 */
	public static Parcelable.Creator<User> getCreator() {
		return CREATOR;
	}

	private User(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * write User Object to parcel
	 */
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(firstname);
		out.writeString(lastname);
		out.writeString(username);
		out.writeString(email);
		out.writeString(password);
		out.writeString(phone);
	}

	/**
	 * read User Object from Parcel
	 * 
	 * @param in
	 */
	public void readFromParcel(Parcel in) {
		firstname = in.readString();
		lastname = in.readString();
		username = in.readString();
		email = in.readString();
		password = in.readString();
		phone = in.readString();
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		public User[] newArray(int size) {
			return new User[size];
		}
	};

}
