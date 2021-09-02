package com.augur.json;

/**
 * The JSONException is thrown by the JSON.org classes when things are amiss.
 * This is a basic extension of java.lang.Exception.
 * 
 * @author augur.com (simplified original from json.org)
 * @version 2021-07-14
 */
public class JSONException extends Exception {
	private static final long serialVersionUID = 0;

    public JSONException(String message) {
        super(message);
    }

    public JSONException(Throwable cause) {
        super(cause);
    }

		public JSONException(String message, Throwable cause) {
        super(message, cause);
    }

}
