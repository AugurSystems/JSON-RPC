package com.augur.json;

/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

/**
 * A JSONObject is an unordered collection of name/value pairs. Its
 * external form is a string wrapped in curly braces with colons between the
 * names and values, and commas between the values and names. The internal form
 * is an object having <code>get</code> and <code>opt</code> methods for
 * accessing the values by name, and <code>put</code> methods for adding or
 * replacing values by name. The values can be any of these types:
 * <code>Boolean</code>, <code>JSONArray</code>, <code>JSONObject</code>,
 * <code>Number</code>, <code>String</code>, or the <code>JSONObject.NULL</code>
 * object. A JSONObject constructor can be used to convert an external form
 * JSON text into an internal form whose values can be retrieved with the
 * <code>get</code> and <code>opt</code> methods, or to convert values into a
 * JSON text using the <code>put</code> and <code>toString</code> methods.
 * A <code>get</code> method returns a value if one can be found, and throws an
 * exception if one cannot be found. An <code>opt</code> method returns a
 * default value instead of throwing an exception, and so is useful for
 * obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an
 * object, which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking and type
 * coercion for you. The opt methods differ from the get methods in that they
 * do not throw. Instead, they return a specified value, such as null.
 * <p>
 * The <code>put</code> methods add or replace values in an object. For example, 
 * <pre>myString = new JSONObject().put("JSON", "Hello, World!").toString();</pre>
 * produces the string <code>{"JSON": "Hello, World"}</code>.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to
 * the JSON syntax rules.
 * The constructors are more forgiving in the texts they will accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just
 *     before the closing brace.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single
 *     quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a quote
 *     or single quote, and if they do not contain leading or trailing spaces,
 *     and if they do not contain any of these characters:
 *     <code>{ } [ ] / \ : , = ; #</code> and if they do not look like numbers
 *     and if they are not the reserved words <code>true</code>,
 *     <code>false</code>, or <code>null</code>.</li>
 * <li>Keys can be followed by <code>=</code> or <code>=></code> as well as
 *     by <code>:</code>.</li>
 * <li>Values can be followed by <code>;</code> <small>(semicolon)</small> as
 *     well as by <code>,</code> <small>(comma)</small>.</li>
 * <li>Numbers may have the <code>0x-</code> <small>(hex)</small> prefix.</li>
 * </ul>
 * @author JSON.org
 * @version 2010-12-28
 * @version 2025-04-18 Simplify '#' code, now supporting comment starting anywhere in line
 */
public class JSONObject implements Serializable {


    /**
     * The map where the JSONObject's properties are kept.
     */
    private Map<String,Object> map;


    /**
     * Construct an empty JSONObject.
     */
    public JSONObject() {
			this.map = new HashMap<>();
    }

		/** 
		 * Makes a completely independent deep copy of the given JSONObject with no references to the source object or its contents.
		 * @param src The JSONException to be copied
		 * @throws JSONException very unlikely or impossible
		 */
		public JSONObject(JSONObject src) throws JSONException
		{
			this(src.toString());
		}
		

//    /**
//     * Construct a JSONObject from a subset of another JSONObject.
//     * An array of strings is used to identify the keys that should be copied.
//     * Missing keys are ignored.
//     * @param jo A JSONObject.
//     * @param names An array of strings.
//     */
//    public JSONObject(JSONObject jo, String[] names) {
//        this();
//        for (int i = 0; i < names.length; i += 1) {
//            try {
//                putOnce(names[i], jo.opt(names[i]));
//            } catch (Exception ignore) {
//            }
//        }
//    }

		
    /**
     * Construct a JSONObject from a Map.
     *
     * @param map A map object that can be used to initialize the contents of
     *  the JSONObject.
     * @throws JSONException 
		 * @author Updated 24-05-2013 to throw exc on null key, and implement typed map
     */
    public JSONObject(Map map) throws JSONException
		{
        this.map = new HashMap<>();
        if (map != null) {
            Iterator i = map.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry e = (Map.Entry)i.next();
                Object value = e.getValue();
                if (value != null) 
								{
									Object key = e.getKey();
					        if (key == null) { throw new JSONException("Null key."); }
                  this.map.put(key.toString(), wrap(value));
                }
            }
        }
    }

		
	/**
	 * Constructs the JSONObject by reading the given file (in UTF-8 encoding).
	 * @param file The File to load the JSON text
	 * @throws com.augur.json.JSONException
	 * @throws java.io.IOException
	 * @deprecated Use the constructor that accepts a File and a specific Charset instead;
	 * for example StandardCharsets.ISO_8859_1 (for HTTP POST) or StandardCharsets.UTF_8.
	 */
	public JSONObject(File file) throws JSONException, IOException
	{
		this(file, StandardCharsets.UTF_8);
	}
	
	/**
	 * Constructs the JSONObject by reading the given file.
	 * @param file The File to load the JSON text
	 * @param charset The Charset to decode text from the binary stream
	 * for example StandardCharsets.ISO_8859_1 (for HTTP POST) or StandardCharsets.UTF_8.
	 * @throws com.augur.json.JSONException
	 * @throws java.io.IOException
	 */
	public JSONObject(File file, Charset charset) throws JSONException, IOException
	{
		this(new InputStreamReader(new FileInputStream(file), charset));
	}
	
		
	/**
	 * Constructs a JSONObject from the Reader, and closes the stream.
	 * @param reader The Reader stream to read the JSON text
	 * @throws com.augur.json.JSONException
	 * @throws java.io.IOException
	 */
	public JSONObject(Reader reader) throws JSONException, IOException
	{
		this();
		JSONTokener x = new JSONTokener(reader);
		parse(x);
		reader.close();
	}
	
		
	/**
	 * Constructs a JSONObject from the InputStream using the system's default decoder (not wise!), 
	 * and closes the stream.
	 * @param in The InputStream to read the JSON text
	 * @throws com.augur.json.JSONException
	 * @throws java.io.IOException
	 * @deprecated Use the method that takes a Charset instead; 
	 * for example StandardCharsets.ISO_8859_1 (for HTTP POST) or StandardCharsets.UTF_8.
	 */
	public JSONObject(InputStream in) throws JSONException, IOException
	{
		this();
		JSONTokener x = new JSONTokener(in);
		parse(x);
		in.close();
	}
	
		
	/**
	 * Constructs a JSONObject from the InputStream, and closes the stream.
	 * @param in An InputStream to read the JSON text
	 * @param charset The Charset to decode text from the binary stream
	 * for example StandardCharsets.ISO_8859_1 (for HTTP POST) or StandardCharsets.UTF_8.
	 * @throws com.augur.json.JSONException
	 * @throws java.io.IOException
	 */
	public JSONObject(InputStream in, Charset charset) throws JSONException, IOException
	{
		this();
		JSONTokener x = new JSONTokener(in, charset);
		parse(x);
		in.close();
	}
	
	

	/** System-dependent line separator (usually '\n' or '\r\n') */
	public static final String LS = System.lineSeparator();


    /**
     * Construct a JSONObject from an Object using bean getters.
     * It reflects on all of the public methods of the object.
     * For each of the methods with no parameters and a name starting
     * with <code>"get"</code> or <code>"is"</code> followed by an uppercase letter,
     * the method is invoked, and a key and the value returned from the getter method
     * are put into the new JSONObject.
     *
     * The key is formed by removing the <code>"get"</code> or <code>"is"</code> prefix.
     * If the second remaining character is not upper case, then the first
     * character is converted to lower case.
     *
     * For example, if an object has a method named <code>"getName"</code>, and
     * if the result of calling <code>object.getName()</code> is <code>"Larry Fine"</code>,
     * then the JSONObject will contain <code>"name": "Larry Fine"</code>.
     *
     * @param bean An object that has getter methods that should be used
     * to make a JSONObject.
     */
    public JSONObject(Object bean) {
        this();
        populateMap(bean);
    }


    /**
     * Construct a JSONObject from an Object, using reflection to find the
     * public members. The resulting JSONObject's keys will be the strings
     * from the names array, and the values will be the field values associated
     * with those keys in the object. If a key is not found or not visible,
     * then it will not be copied into the new JSONObject.
     * @param object An object that has fields that should be used to make a
     * JSONObject.
     * @param names An array of strings, the names of the fields to be obtained
     * from the object.
     */
    public JSONObject(Object object, String names[]) {
        this();
        Class c = object.getClass();
        for (int i = 0; i < names.length; i += 1) {
            String name = names[i];
            try {
                put(name, c.getField(name).get(object));
            } catch (Exception ignore) {
            }
        }
    }


    /**
     * Construct a JSONObject from a source JSON text string.
     * This is the most commonly used JSONObject constructor.
     * @param source    A string beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     * @exception JSONException If there is a syntax error in the source
     *  string or a duplicated key.
     */
    public JSONObject(String source) throws JSONException {
        this(new JSONTokener(source));
    }


    /**
     * Construct a JSONObject from a ResourceBundle.
     * @param baseName The ResourceBundle base name.
     * @param locale The Locale to load the ResourceBundle for.
     * @throws JSONException If any JSONExceptions are detected.
     */
    public JSONObject(String baseName, Locale locale) throws JSONException {
        this();
        ResourceBundle r = ResourceBundle.getBundle(baseName, locale, 
                Thread.currentThread().getContextClassLoader());

// Iterate through the keys in the bundle.
        
        Enumeration keys = r.getKeys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (key instanceof String) {
    
// Go through the path, ensuring that there is a nested JSONObject for each 
// segment except the last. Add the value using the last segment's name into
// the deepest nested JSONObject.
                
                String[] path = ((String)key).split("\\.");
                int last = path.length - 1;
                JSONObject target = this;
                for (int i = 0; i < last; i += 1) {
                    String segment = path[i];
                    JSONObject nextTarget = target.optJSONObject(segment);
                    if (nextTarget == null) {
                        nextTarget = new JSONObject();
                        target.put(segment, nextTarget);
                    }
                    target = nextTarget;
                }
                target.put(path[last], r.getString((String)key));
            }
        }
    }


    /**
     * Construct a JSONObject from a JSONTokener.
     * @param x A JSONTokener object containing the source string.
     * @throws JSONException If there is a syntax error in the source string
     *  or a duplicated key.
     */
    public JSONObject(JSONTokener x) throws JSONException {
        this();
				parse(x);
    }

		public final void parse(String s) throws JSONException
		{
			parse(new JSONTokener(s));
		}
		
		public final void parse(JSONTokener x) throws JSONException
		{
        char c;
        String key;
        if ((c=x.nextClean()) != '{') { throw x.syntaxError("A JSONObject must begin with '{' but found '"+JSONTokener.toString(c)+"'"); }
        for (;;) {
            c = x.nextClean();
            switch (c) {
            case 0:
                throw x.syntaxError("A JSONObject text must end with '}', but reached EOF");
            case '}':
                return;
            default:
                x.back();
                key = x.nextValue().toString();
            }

// The key is followed by ':'. We will also tolerate '=' or '=>'.

            c = x.nextClean();
            if (c == '=') {
                if (x.next() != '>') {
                    x.back();
                }
            } else if (c != ':') {
                throw x.syntaxError("Expected a ':' after a key but found '"+JSONTokener.toString(c)+"'"); 
            }
            put(key, x.nextValue());

// Pairs are separated by ','. We will also tolerate ';'.

            switch (x.nextClean()) {
            case ';':
            case ',':
                if (x.nextClean() == '}') {
                    return;
                }
                x.back();
                break;
            case '}':
                return;
            default:
                throw x.syntaxError("Expected a ',' or '}' but found '"+JSONTokener.toString(c)+"'"); 
            }
        }
		}

    
    /**
     * Accumulate values under a key. It is similar to the put method except
     * that if there is already an object stored under the key then a
     * JSONArray is stored under the key to hold all of the accumulated values.
     * If there is already a JSONArray, then the new value is appended to it.
     * In contrast, the put method replaces the previous value.
     * @param key   A key string.
     * @param value An object to be accumulated under the key.
     * @return this.
     * @throws JSONException If the value is an invalid number
     *  or if the key is null.
     */
    public JSONObject accumulate(String key, Object value)
            throws JSONException {
        testValidity(value);
        Object object = opt(key);
        if (object == null) {
            put(key, value instanceof JSONArray ?
                    new JSONArray().put(value) : value);
        } else if (object instanceof JSONArray) {
            ((JSONArray)object).put(value);
        } else {
            put(key, new JSONArray().put(object).put(value));
        }
        return this;
    }


    /**
     * Append values to the array under a key. If the key does not exist in the
     * JSONObject, then the key is put in the JSONObject with its value being a
     * JSONArray containing the value parameter. If the key was already
     * associated with a JSONArray, then the value parameter is appended to it.
     * @param key   A key string.
     * @param value An object to be accumulated under the key.
     * @return this.
     * @throws JSONException If the key is null or if the current value
     *  associated with the key is not a JSONArray.
     */
    public JSONObject append(String key, Object value) throws JSONException {
        testValidity(value);
        Object object = opt(key);
        if (object == null) {
            put(key, new JSONArray().put(value));
        } else if (object instanceof JSONArray) {
            put(key, ((JSONArray)object).put(value));
        } else {
            throw new JSONException("Value in ["+quote(key)+"] is not a JSONArray: "+object.getClass());
        }
        return this;
    }


    /**
     * Produce a string from a double. The string "null" will be returned if
     * the number is not finite.
     * @param  d A double.
     * @return A String.
     */
    public static String doubleToString(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return "null";
        }

// Shave off trailing zeros and decimal point, if possible.

        String string = Double.toString(d);
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && 
        		string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }


    /**
     * Get the value object associated with a key.
     *
     * @param key   A key string.
     * @return      The object associated with the key.
     * @throws      JSONException if the key is not found.
     */
    public Object get(String key) throws JSONException {
        if (key == null) {
           throw new JSONException("Null key.");
        }
        Object object = opt(key);
        if (object == null) {
					if (has(key)) return null;
					else throw new JSONException("Value at ["+quote(key)+"] not found.");
        }
        return object;
    }


    /**
     * Get the boolean value associated with a key.
     *
     * @param key   A key string.
     * @return      The truth.
     * @throws      JSONException
     *  if the value is not a Boolean or the String "true" or "false".
     */
    public boolean getBoolean(String key) throws JSONException {
        Object object = get(key);
        if (object.equals(Boolean.FALSE) ||
                (object instanceof String &&
                ((String)object).equalsIgnoreCase("false"))) {
            return false;
        } else if (object.equals(Boolean.TRUE) ||
                (object instanceof String &&
                ((String)object).equalsIgnoreCase("true"))) {
            return true;
        }
        throw new JSONException("Value in ["+quote(key)+"] is not a Boolean: "+object.getClass());
    }


    /**
     * Get the double value associated with a key.
     * @param key   A key string.
     * @return      The numeric value.
     * @throws JSONException if the key is not found or
     *  if the value is not a Number object and cannot be converted to a number.
     */
    public double getDouble(String key) throws JSONException {
        Object object = get(key);
        try {
            return object instanceof Number ?
                ((Number)object).doubleValue() :
                Double.parseDouble((String)object);
        } catch (Exception e) {
            throw new JSONException("Value in ["+quote(key)+"] is not a number: "+object.getClass());
        }
    }


    /**
     * Get the int value associated with a key. 
     *
     * @param key   A key string.
     * @return      The integer value.
     * @throws   JSONException if the key is not found or if the value cannot
     *  be converted to an integer.
     */
    public int getInt(String key) throws JSONException {
        Object object = get(key);
        try {
            return object instanceof Number ?
                ((Number)object).intValue() :
                Integer.parseInt((String)object);
        } catch (Exception e) {
            throw new JSONException("Value in ["+quote(key)+"] is not an int.");
        }
    }


    /**
     * Get the JSONArray value associated with a key.
     *
     * @param key   A key string.
     * @return      A JSONArray which is the value.
     * @throws      JSONException if the key is not found or
     *  if the value is not a JSONArray.
     */
    public JSONArray getJSONArray(String key) throws JSONException {
        Object object = get(key);
        if (object == null) {
					if (has(key)) return null;
					else throw new JSONException("Value at ["+quote(key)+"] not found.");
        }
        if (object instanceof JSONArray) { return (JSONArray)object; }
				else throw new JSONException("Value in ["+quote(key)+"] is not a JSONArray: "+object.getClass());
    }


    /**
     * Get the JSONObject value associated with a key.
     *
     * @param key   A key string.
     * @return      A JSONObject which is the value.
     * @throws      JSONException if the key is not found or
     *  if the value is not a JSONObject.
     */
    public JSONObject getJSONObject(String key) throws JSONException {
        Object object = get(key);
        if (object == null) {
					if (has(key)) return null;
					else throw new JSONException("Value at ["+quote(key)+"] not found.");
        }
        if (object instanceof JSONObject) { return (JSONObject)object; }
				else throw new JSONException("Value in ["+quote(key)+"] is not a JSONObject: "+object.getClass());
    }


    /**
     * Get the long value associated with a key. 
     *
     * @param key   A key string.
     * @return      The long value.
     * @throws   JSONException if the key is not found or if the value cannot
     *  be converted to a long.
     */
    public long getLong(String key) throws JSONException {
        Object object = get(key);
        try {
            return object instanceof Number ?
                ((Number)object).longValue() :
                Long.parseLong((String)object);
        } catch (Exception e) {
            throw new JSONException("Value in ["+quote(key)+"] is not a long.");
        }
    }


    /**
     * Get an array of field names from a JSONObject.
     *
     * @return An array of field names, or null if there are no names.
     */
    public static String[] getNames(JSONObject jo) {
        int length = jo.length();
        if (length == 0) {
            return null;
        }
        Iterator iterator = jo.keys();
        String[] names = new String[length];
        int i = 0;
        while (iterator.hasNext()) {
            names[i] = (String)iterator.next();
            i += 1;
        }
        return names;
    }


    /**
     * Get an array of field names from an Object.
     *
     * @return An array of field names, or null if there are no names.
     */
    public static String[] getNames(Object object) {
        if (object == null) {
            return null;
        }
        Class klass = object.getClass();
        Field[] fields = klass.getFields();
        int length = fields.length;
        if (length == 0) {
            return null;
        }
        String[] names = new String[length];
        for (int i = 0; i < length; i += 1) {
            names[i] = fields[i].getName();
        }
        return names;
    }


    /**
     * Get the string associated with a key.
     *
     * @param key   A key string.
     * @return      A string which is the value.
     * @throws   JSONException if the key is not found.
     */
    public String getString(String key) throws JSONException {
        Object object = get(key);
        if (object == null) {
					if (has(key)) return null;
					else throw new JSONException("Value at ["+quote(key)+"] not found.");
        }
				return object.toString();
//        if (object instanceof String) { return (String)object; }
//				else return object.toString();
//				else throw new JSONException("Value in ["+quote(key)+"] is not a String: "+object.getClass());
    }


    /**
     * Determine if the JSONObject contains a specific key.
     * @param key   A key string.
     * @return      true if the key exists in the JSONObject.
     */
    public boolean has(String key) {
        return this.map.containsKey(key);
    }
    
    
    /**
     * Increment a property of a JSONObject. If there is no such property,
     * create one with a value of 1. If there is such a property, and if
     * it is an Integer, Long, Double, or Float, then add one to it.
     * @param key  A key string.
     * @return this.
     * @throws JSONException If there is already a property with this name
     * that is not an Integer, Long, Double, or Float.
     */
    public JSONObject increment(String key) throws JSONException {
        Object value = opt(key);
        if (value == null) {
            put(key, 1);
        } else if (value instanceof Integer) {
            put(key, ((Integer)value) + 1);
        } else if (value instanceof Long) {
            put(key, ((Long)value) + 1);                
        } else if (value instanceof Double) {
            put(key, ((Double)value) + 1);                
        } else if (value instanceof Float) {
            put(key, ((Float)value) + 1);                
        } else {
            throw new JSONException("Unable to increment [" + quote(key) + "].");
        }
        return this;
    }


    /**
     * Get an enumeration of the keys of the JSONObject.
     *
     * @return An iterator of the keys.
     */
    public Iterator keys() {
        return this.map.keySet().iterator();
    }

    /**
     * Get an enumeration of the keys of the JSONObject.
     *
     * @return An iterator of the keys.
		 * @author Added 24-05-2013 by Janicki
     */
    public Set<String> keySet() {
        return this.map.keySet();
    }


    /**
     * Get the number of keys stored in the JSONObject.
     *
     * @return The number of keys in the JSONObject.
     */
    public int length() {
        return this.map.size();
    }


    /**
     * Produce a JSONArray containing the names of the elements of this
     * JSONObject.
     * @return A JSONArray containing the key strings, or null if the JSONObject
     * is empty.
     */
    public JSONArray names() {
        JSONArray ja = new JSONArray();
        Iterator  keys = keys();
        while (keys.hasNext()) {
            ja.put(keys.next());
        }
        return ja.length() == 0 ? null : ja;
    }

    /**
     * Produce a string from a Number.
     * @param  number A Number
     * @return A String.
     * @throws JSONException If n is a non-finite number.
     */
    public static String numberToString(Number number)
            throws JSONException {
        if (number == null) {
            throw new JSONException("Null pointer");
        }
        testValidity(number);

// Shave off trailing zeros and decimal point, if possible.

        String string = number.toString();
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && 
        		string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }


    /**
     * Get an optional value associated with a key.
     * @param key   A key string.
     * @return      An object which is the value, or null if there is no value.
     */
    public Object opt(String key) {
        return key == null ? null : this.map.get(key);
    }


    /**
     * Get an optional boolean associated with a key.
     * It returns false if there is no such key, or if the value is not
     * Boolean.TRUE or the String "true".
     *
     * @param key   A key string.
     * @return      The truth.
     */
    public boolean optBoolean(String key) {
        return optBoolean(key, false);
    }


    /**
     * Get an optional boolean associated with a key.
     * It returns the defaultValue if there is no such key, or if it is not
     * a Boolean or the String "true" or "false" (case insensitive).
     *
     * @param key              A key string.
     * @param defaultValue     The default.
     * @return      The truth.
     */
    public boolean optBoolean(String key, boolean defaultValue) {
        try {
            return getBoolean(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * Get an optional double associated with a key,
     * or NaN if there is no such key or if its value is not a number.
     * If the value is a string, an attempt will be made to evaluate it as
     * a number.
     *
     * @param key   A string which is the key.
     * @return      An object which is the value.
     */
    public double optDouble(String key) {
        return optDouble(key, Double.NaN);
    }


    /**
     * Get an optional double associated with a key, or the
     * defaultValue if there is no such key or if its value is not a number.
     * If the value is a string, an attempt will be made to evaluate it as
     * a number.
     *
     * @param key   A key string.
     * @param defaultValue     The default.
     * @return      An object which is the value.
     */
    public double optDouble(String key, double defaultValue) {
        try {
            return getDouble(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * Get an optional int value associated with a key,
     * or zero if there is no such key or if the value is not a number.
     * If the value is a string, an attempt will be made to evaluate it as
     * a number.
     *
     * @param key   A key string.
     * @return      An object which is the value.
     */
    public int optInt(String key) {
        return optInt(key, 0);
    }


    /**
     * Get an optional int value associated with a key,
     * or the default if there is no such key or if the value is not a number.
     * If the value is a string, an attempt will be made to evaluate it as
     * a number.
     *
     * @param key   A key string.
     * @param defaultValue     The default.
     * @return      An object which is the value.
     */
    public int optInt(String key, int defaultValue) {
        try {
            return getInt(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * Get an optional JSONArray associated with a key.
     * It returns null if there is no such key, or if its value is not a JSONArray.
     *
     * @param key   A key string.
     * @return      A JSONArray which is the value.
     */
    public JSONArray optJSONArray(String key) {
        return optJSONArray(key, null);
    }


    /**
     * Get an optional JSONArray associated with a key.
     * It returns the defaultValue if there is no such key, or if its value is not a JSONArray.
     *
     * @param key   A key string.
		 * @param defaultValue The JSONArray to return if the given key has no value, or its value is not a JSONArray
     * @return      A JSONArray which is the value.
     */
    public JSONArray optJSONArray(String key, JSONArray defaultValue) {
			Object object;
			try { object = get(key); } 
			catch (JSONException e) { return defaultValue; } // key not found?
			if (object==null) return null;
			return object instanceof JSONArray ? (JSONArray)object : defaultValue;
    }


    /**
     * Get an optional JSONObject associated with a key.
     * It returns null if there is no such key, or if its value is not a
     * JSONObject.
     *
     * @param key   A key string.
     * @return      A JSONObject which is the value.
     */
    public JSONObject optJSONObject(String key) {
        return optJSONObject(key, null);
    }

    /**
     * Get an optional JSONObject associated with a key.
     * It returns the defaultValue if there is no such key, or if its value is not a JSONObject.
     *
     * @param key   A key string.
		 * @param defaultValue The JSONObject to return if the given key has no value, or its value is not a JSONObject
     * @return      A JSONObject which is the value.
     */
    public JSONObject optJSONObject(String key, JSONObject defaultValue) {
        Object object;
				try { object = get(key); } 
				catch (JSONException e) { return defaultValue; } // key not found?
				if (object==null) return null;
        return object instanceof JSONObject ? (JSONObject)object : defaultValue;
    }


    /**
     * Get an optional long value associated with a key,
     * or zero if there is no such key or if the value is not a number.
     * If the value is a string, an attempt will be made to evaluate it as
     * a number.
     *
     * @param key   A key string.
     * @return      An object which is the value.
     */
    public long optLong(String key) {
        return optLong(key, 0);
    }


    /**
     * Get an optional long value associated with a key,
     * or the default if there is no such key or if the value is not a number.
     * If the value is a string, an attempt will be made to evaluate it as
     * a number.
     *
     * @param key          A key string.
     * @param defaultValue The default.
     * @return             An object which is the value.
     */
    public long optLong(String key, long defaultValue) {
        try {
            return getLong(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
		 * DELETED: Just causes too many mistaken assumptions that null will be returned if key not found; use the definitive version to specify your default.
     * Get an optional string associated with a key.
     * It returns an empty string if there is no such key. If the value is not
     * a string and is not null, then it is converted to a string.
     *
     * @param key   A key string.
     * @return      A string which is the value.
    public String optString(String key) {
        return optString(key, "");
    }
     */


    /**
     * Get an optional string associated with a key.
     * It returns the defaultValue if there is no such key, or the value is null.
     *
     * @param key   A key string.
     * @param defaultValue     The default.
     * @return      A string which is the value.
     */
    public String optString(String key, String defaultValue) {
        Object object = opt(key);
        return object == null ? defaultValue : object.toString();        
    }


    private void populateMap(Object bean) {
        Class klass = bean.getClass();

// If klass is a System class then set includeSuperClass to false. 

        boolean includeSuperClass = klass.getClassLoader() != null;

        Method[] methods = (includeSuperClass) ?
                klass.getMethods() : klass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i += 1) {
            try {
                Method method = methods[i];
                if (Modifier.isPublic(method.getModifiers())) {
                    String name = method.getName();
                    String key = "";
                    if (name.startsWith("get")) {
                        if (name.equals("getClass") || 
                                name.equals("getDeclaringClass")) {
                            key = "";
                        } else {
                            key = name.substring(3);
                        }
                    } else if (name.startsWith("is")) {
                        key = name.substring(2);
                    }
                    if (key.length() > 0 &&
                            Character.isUpperCase(key.charAt(0)) &&
                            method.getParameterTypes().length == 0) {
                        if (key.length() == 1) {
                            key = key.toLowerCase();
                        } else if (!Character.isUpperCase(key.charAt(1))) {
                            key = key.substring(0, 1).toLowerCase() +
                                key.substring(1);
                        }

                        Object result = method.invoke(bean, (Object[])null);
                        if (result != null) {
                            map.put(key, wrap(result));
                        }
                    }
                }
            } catch (Exception ignore) {
            }
        }
    }


    /**
     * Put a key/boolean pair in the JSONObject.
     *
     * @param key   A key string.
     * @param value A boolean which is the value.
     * @return this.
     * @throws JSONException If the key is null.
     */
    public JSONObject put(String key, boolean value) throws JSONException {
        put(key, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }


    /**
     * Put a key/value pair in the JSONObject, where the value will be a
     * JSONArray which is produced from a Collection.
     * @param key   A key string.
     * @param value A Collection value.
     * @return      this.
     * @throws JSONException
     */
    public JSONObject put(String key, Collection value) throws JSONException {
        put(key, new JSONArray(value));
        return this;
    }


    /**
     * Put a key/double pair in the JSONObject.
     *
     * @param key   A key string.
     * @param value A double which is the value.
     * @return this.
     * @throws JSONException If the key is null or if the number is invalid.
     */
    public JSONObject put(String key, double value) throws JSONException {
        put(key, Double.valueOf(value));
        return this;
    }


    /**
     * Put a key/int pair in the JSONObject.
     *
     * @param key   A key string.
     * @param value An int which is the value.
     * @return this.
     * @throws JSONException If the key is null.
     */
    public JSONObject put(String key, int value) throws JSONException 
		{
			if (key == null) throw new JSONException("Null key.");
			this.map.put(key, value);
			return this;
    }


    /**
     * Put a key/long pair in the JSONObject.
     *
     * @param key   A key string.
     * @param value A long which is the value.
     * @return this.
     * @throws JSONException If the key is null.
     */
    public JSONObject put(String key, long value) throws JSONException 
		{
			if (key == null) throw new JSONException("Null key.");
			this.map.put(key, value);
			return this;
    }


    /**
     * Put a key/value pair in the JSONObject, where the value will be a
     * JSONObject which is produced from a Map.
     * @param key   A key string.
     * @param value A Map value.
     * @return      this.
     * @throws JSONException
     */
    public JSONObject put(String key, Map value) throws JSONException {
        put(key, new JSONObject(value));
        return this;
    }


    /**
     * Put a key/value pair in the JSONObject. 
     * @param key   A key string.
     * @param value An object which is the value. It should be of one of these
     *  types: Boolean, Double, Integer, JSONArray, JSONObject, Long, String,
     *  or the JSONObject.NULL object.
     * @return this.
     * @throws JSONException If the value is non-finite number
     *  or if the key is null.
     */
    public JSONObject put(String key, Object value) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
            testValidity(value);
            this.map.put(key, value);
        return this;
    }

		
    /**
     * Put a key=null in the JSONObject. 
     * @param key   A key string.
     * @return this.
     * @throws JSONException if the key is null.
     */
    public JSONObject putNull(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
            this.map.put(key, null);
        return this;
    }

		
		
		/**
		 * Copies all the key/value pairs from the given JSONObject into this one, 
		 * replacing existing values if there are key collisions, similar to the 
		 * action of java.util.Map.putAll().
		 * @param more The JSONObject whose contents should be copied into this JSONObject.
		 * @author Added by Janicki
		 */
		public void putAll(JSONObject more) {
			if (more!=null) this.map.putAll(more.map);
		}

		
    /**
     * Produce a string in double quotes with backslash sequences in all the
     * right places. A backslash will be inserted within </, producing <\/,
     * allowing JSON text to be delivered in HTML. In JSON text, a string 
     * cannot contain a control character or an unescaped quote or backslash.
     * @param string A String
     * @return  A String correctly formatted for insertion in a JSON text.
     */
    public static String quote(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }
        StringBuilder sb = new StringBuilder(string.length() + 4);
        return quote(string, sb).toString();
    }

		
		/**
		 * New method from refactored JSONObject.quote(String) so that the StringBuilder could be reused.
		 * 
		 * Produce a string in double quotes with backslash sequences in all the
		 * right places. A backslash will be inserted within </, producing <\/,
		 * allowing JSON text to be delivered in HTML. In JSON text, a string 
		 * cannot contain a control character or an unescaped quote or backslash.
		 * @param string A String
		 * @return  A String correctly formatted for insertion in a JSON text.
		 */
		public static StringBuilder quote(String string, StringBuilder sb)
		{
			if (string == null || string.length() == 0)
			{
				sb.append("\"\"");
				return sb;
			}

			char b;
			char c = 0;
			String hhhh;
			int i;
			int len = string.length();

			sb.append('"');
			for (i = 0; i < len; i += 1)
			{
				b = c;
				c = string.charAt(i);
				switch (c)
				{
					case '\\':
					case '"':
						sb.append('\\');
						sb.append(c);
						break;
					case '/':
						if (b == '<') { sb.append('\\'); }
						sb.append(c);
						break;
					case '\b':
						sb.append("\\b");
						break;
					case '\t':
						sb.append("\\t");
						break;
					case '\n':
						sb.append("\\n");
						break;
					case '\f':
						sb.append("\\f");
						break;
					case '\r':
						sb.append("\\r");
						break;
					default:
						if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100'))
						{
							hhhh = "000" + Integer.toHexString(c);
							sb.append("\\u").append(hhhh.substring(hhhh.length() - 4));
						}
						else
						{
							sb.append(c);
						}
				}
			}
			sb.append('"');
			return sb;
		}


		/**
     * Remove a name and its value, if present.
     * @param key The name to be removed.
     * @return The value that was associated with the name,
     * or null if there was no value.
     */
    public Object remove(String key) {
        return this.map.remove(key);
    }
		
		
		/**
		 * Needed in com.augur.trapstation.LogReaderEvents, since the JSONObject is 
		 * to be reused within a de-serialization/filter loop.
		 */
		public void clear()
		{
			this.map.clear();
		}

    /**
     * Get an enumeration of the keys of the JSONObject.
     * The keys will be sorted alphabetically.
     *
     * @return An NavigableSet of the keys.
     */
    public NavigableSet<String> sortedKeys() {
      return new TreeSet<>(this.map.keySet());
    }

    /**
     * Try to convert a string into a number, boolean, or null. If the string
     * can't be converted, return the string.
     * @param string A String.
     * @return A simple JSON value.
     */
    public static Object stringToValue(String string) {
        if (string.equals("")) {
            return string;
        }
        if (string.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (string.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("null")) {
            return null;
        }

        /*
         * If it might be a number, try converting it. 
         * We support the non-standard 0x- convention. 
         * If a number cannot be produced, then the value will just
         * be a string. Note that the 0x-, plus, and implied string
         * conventions are non-standard. A JSON parser may accept
         * non-JSON forms as long as it accepts all correct JSON forms.
         */

        char b = string.charAt(0);
        if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
            if (b == '0' && string.length() > 2 &&
                        (string.charAt(1) == 'x' || string.charAt(1) == 'X')) {
                try {
                    return Integer.parseInt(string.substring(2), 16);
                } catch (Exception ignore) {
                }
            }
            try {
                if (string.indexOf('.') > -1 || 
                        string.indexOf('e') > -1 || string.indexOf('E') > -1) {
                    return Double.valueOf(string);
                } else {
                    Long myLong = Long.valueOf(string);
                    if (myLong.longValue() == myLong.intValue()) {
                        return myLong.intValue();
                    } else {
                        return myLong;
                    }
                }
            }  catch (Exception ignore) {
            }
        }
        return string;
    }


    /**
     * Throw an exception if the object is a NaN or infinite number.
     * @param o The object to test.
     * @throws JSONException If o is a non-finite number.
     */
    public static void testValidity(Object o) throws JSONException {
        if (o != null) {
            if (o instanceof Double) {
                if (((Double)o).isInfinite() || ((Double)o).isNaN()) {
                    throw new JSONException(
                        "JSON does not allow non-finite numbers.");
                }
            } else if (o instanceof Float) {
                if (((Float)o).isInfinite() || ((Float)o).isNaN()) {
                    throw new JSONException(
                        "JSON does not allow non-finite numbers.");
                }
            }
        }
    }


    /**
     * Produce a JSONArray containing the values of the members of this
     * JSONObject.
     * @param names A JSONArray containing a list of key strings. This
     * determines the sequence of the values in the result.
     * @return A JSONArray of values.
     * @throws JSONException If any of the values are non-finite numbers.
     */
    public JSONArray toJSONArray(JSONArray names) throws JSONException {
        if (names == null || names.length() == 0) {
            return null;
        }
        JSONArray ja = new JSONArray();
        for (int i = 0; i < names.length(); i += 1) {
            ja.put(this.opt(names.getString(i)));
        }
        return ja;
    }

    /**
     * Make a JSON text of this JSONObject. For compactness, no whitespace
     * is added. If this would not result in a syntactically correct JSON text,
     * then null will be returned instead.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, portable, transmittable
     *  representation of the object, beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     */
		 @Override
    public String toString() {
        try {
            Iterator<String>     keys = keys();
            StringBuilder sb = new StringBuilder("{");

            while (keys.hasNext()) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                String key = keys.next();
                sb.append(quote(key));
                sb.append(':');
                sb.append(valueToString(this.map.get(key)));
            }
            sb.append('}');
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Make a prettyprinted JSON text of this JSONObject.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     * @param indentFactor The number of spaces to add to each level of
     *  indentation.
     * @return a printable, displayable, portable, transmittable
     *  representation of the object, beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     * @throws JSONException If the object contains an invalid number.
     */
    public String toString(int indentFactor) throws JSONException {
        return toString(indentFactor, 0);
    }


    /**
     * Make a prettyprinted JSON text of this JSONObject.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     * @param indentFactor The number of spaces to add to each level of
     *  indentation.
     * @param indent The indentation of the top level.
     * @return a printable, displayable, transmittable
     *  representation of the object, beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     * @throws JSONException If the object contains an invalid number.
     */
    String toString(int indentFactor, int indent) throws JSONException {
        int i;
        int length = this.length();
        if (length == 0) {
            return "{}";
        }
        Iterator<String> keys = sortedKeys().iterator();
        int          newindent = indent + indentFactor;
        String       key;
        StringBuilder sb = new StringBuilder("{");
        if (length == 1) {
            key = keys.next();
            sb.append(quote(key));
            sb.append(": ");
            sb.append(valueToString(this.map.get(key), indentFactor,
                    indent));
        } else {
            while (keys.hasNext()) {
                key = keys.next();
                if (sb.length() > 1) {
                    sb.append(",").append(LS);
                } else {
                    sb.append(LS);
                }
                for (i = 0; i < newindent; i += 1) {
                    sb.append(' ');
                }
                sb.append(quote(key));
                sb.append(": ");
                sb.append(valueToString(this.map.get(key), indentFactor,
                        newindent));
            }
            if (sb.length() > 1) {
                sb.append(LS);
                for (i = 0; i < indent; i += 1) {
                    sb.append(' ');
                }
            }
        }
        sb.append('}');
        return sb.toString();
    }


    /**
     * Make a JSON text of an Object value. If the object has an
     * value.toJSONString() method, then that method will be used to produce
     * the JSON text. The method is required to produce a strictly
     * conforming text. If the object does not contain a toJSONString
     * method (which is the most common case), then a text will be
     * produced by other means. If the value is an array or Collection,
     * then a JSONArray will be made from it and its toJSONString method
     * will be called. If the value is a MAP, then a JSONObject will be made
     * from it and its toJSONString method will be called. Otherwise, the
     * value's toString method will be called, and the result will be quoted.
     *
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     * @param value The value to be serialized.
     * @return a printable, displayable, transmittable
     *  representation of the object, beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     * @throws JSONException If the value is or contains an invalid number.
     */
    public static String valueToString(Object value) throws JSONException {
        if (value == null) {
            return "null";
        }
        if (value instanceof JSONString) {
            Object object;
            try {
                object = ((JSONString)value).toJSONString();
            } catch (Exception e) {
                throw new JSONException(e);
            }
            if (object instanceof String) {
                return (String)object;
            }
            throw new JSONException("Bad value from toJSONString: " + object);
        }
        if (value instanceof Number) {
            return numberToString((Number) value);
        }
        if (value instanceof Boolean || value instanceof JSONObject ||
                value instanceof JSONArray) {
            return value.toString();
        }
        if (value instanceof Map) {
            return new JSONObject((Map)value).toString();
        }
        if (value instanceof Collection) {
            return new JSONArray((Collection)value).toString();
        }
        if (value.getClass().isArray()) {
            return new JSONArray(value).toString();
        }
        return quote(value.toString());
    }


    /**
     * Make a prettyprinted JSON text of an object value.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     * @param value The value to be serialized.
     * @param indentFactor The number of spaces to add to each level of
     *  indentation.
     * @param indent The indentation of the top level.
     * @return a printable, displayable, transmittable
     *  representation of the object, beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     * @throws JSONException If the object contains an invalid number.
     */
     static String valueToString(Object value, int indentFactor, int indent)
            throws JSONException {
        if (value == null) {
            return "null";
        }
        try {
            if (value instanceof JSONString) {
                Object o = ((JSONString)value).toJSONString();
                if (o instanceof String) {
                    return (String)o;
                }
            }
        } catch (Exception ignore) {
        }
        if (value instanceof Number) {
            return numberToString((Number) value);
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof JSONObject) {
            return ((JSONObject)value).toString(indentFactor, indent);
        }
        if (value instanceof JSONArray) {
            return ((JSONArray)value).toString(indentFactor, indent);
        }
        if (value instanceof Map) {
            return new JSONObject((Map)value).toString(indentFactor, indent);
        }
        if (value instanceof Collection) {
            return new JSONArray((Collection)value).toString(indentFactor, indent);
        }
        if (value.getClass().isArray()) {
            return new JSONArray(value).toString(indentFactor, indent);
        }
        return quote(value.toString());
    }


     /**
      * Wrap an object, if necessary. If the object is null, return the NULL 
      * object. If it is an array or collection, wrap it in a JSONArray. If 
      * it is a map, wrap it in a JSONObject. If it is a standard property 
      * (Double, String, et al) then it is already wrapped. Otherwise, if it 
      * comes from one of the java packages, turn it into a string. And if 
      * it doesn't, try to wrap it in a JSONObject. If the wrapping fails,
      * then null is returned.
      *
      * @param object The object to wrap
      * @return The wrapped value
      */
     public static Object wrap(Object object) {
         try {
             if (object == null) {
                 return null;
             }
             if (object instanceof JSONObject || object instanceof JSONArray  || 
                     object instanceof JSONString || 
                     object instanceof Byte   || object instanceof Character  ||
                     object instanceof Short  || object instanceof Integer    ||
                     object instanceof Long   || object instanceof Boolean    || 
                     object instanceof Float  || object instanceof Double     ||
                     object instanceof String) {
                 return object;
             }
             
             if (object instanceof Collection) {
                 return new JSONArray((Collection)object);
             }
             if (object.getClass().isArray()) {
                 return new JSONArray(object);
             }
             if (object instanceof Map) {
                 return new JSONObject((Map)object);
             }
             Package objectPackage = object.getClass().getPackage();
             String objectPackageName = ( objectPackage != null ? objectPackage.getName() : "" );
             if (objectPackageName.startsWith("java.") ||
                     objectPackageName.startsWith("javax.") ||
                     object.getClass().getClassLoader() == null) {
                 return object.toString();
             }
             return new JSONObject(object);
         } catch(Exception exception) {
             return null;
         }
     }

     
     /**
      * Write the contents of the JSONObject as JSON text to a writer.
      * For compactness, no whitespace is added.
      * <p>
      * Warning: This method assumes that the data structure is acyclical.
      *
      * @return The writer.
      * @throws JSONException
      */
     public Writer write(Writer writer) throws JSONException {
        try {
            boolean  commanate = false;
            Iterator<String> keys = keys();
            writer.write('{');

            while (keys.hasNext()) {
                if (commanate) {
                    writer.write(',');
                }
                String key = keys.next();
                writer.write(quote(key));
                writer.write(':');
                Object value = this.map.get(key);
                if (value instanceof JSONObject) {
                    ((JSONObject)value).write(writer);
                } else if (value instanceof JSONArray) {
                    ((JSONArray)value).write(writer);
                } else {
                    writer.write(valueToString(value));
                }
                commanate = true;
            }
            writer.write('}');
            return writer;
        } catch (IOException exception) {
            throw new JSONException(exception);
        }
     }
}