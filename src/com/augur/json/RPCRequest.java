package com.augur.json;

/**
 * Implements the JSON-RPC specification (version 2.0) for a RPC request object.
 *
 * @author Chris.Janicki@AugurSystems.com
 * @see http://www.jsonrpc.org/specification
 * 
 * Copyright 2010 Augur Systems, Inc.  All rights reserved.
 */

public class RPCRequest extends JSONObject //implements Externalizable
{
	public static final long serialVersionUID = 8335194366852795448L;
	public static final String VERSION = "2.0";
	public static final String JSONRPC="jsonrpc", ID="id", METHOD="method", PARAMS="params";
	public static final JSONObject NULL_PARAMS = new JSONObject();
	
	JSONObject params;
	String jsonrpc, id, method;


	public RPCRequest(String id, String method, JSONObject params) throws JSONException
	{
		super();
		this.jsonrpc = VERSION;
		put(JSONRPC, jsonrpc);

		this.method = method;
		if (method!=null) put(METHOD, method);
		else throw new JSONException("The 'method' key is required.");

		this.id = id;
		if (id!=null) put(ID, id);

		this.params = params;
		if (params != null) put(PARAMS, params);
	}
	

	/**
	 * Required during unmarshalling, only.
	 */
	public RPCRequest()
	{
		super();
	}
	
	/** Used when RPC params come in a GET request query.
	 * @param params The JSONObject containing the key/value params parsed from a GET request query
	 * @throws com.augur.json.JSONException */
	public RPCRequest(JSONObject params) throws JSONException
	{
		this(params.toString());
	}
	
	/**
	 * Used to parse a JSON-RPC request.
	 *
	 * @param request
	 * @throws JSONException
	 */
	public RPCRequest(String request) throws JSONException
	{
		super(request);
		// read all KEYS now so JSONExceptions are squeezed out during construction...
		params = optJSONObject(PARAMS,null);
		if (params==null) params = new JSONObject(); // for GET request, params are at top level; need to copy into params object
		jsonrpc=VERSION; // just assume this version, if missing
		id = null; // default, a "notification"
		
		for (String k : keySet())
		{
			switch(k)
			{
				case PARAMS: break;
				case JSONRPC: 
					jsonrpc = getString(JSONRPC); 
					if (!jsonrpc.equals(VERSION)) throw new JSONException("Bad version.  Expected "+VERSION+", received "+jsonrpc+".");
					break;
				case ID: 
					id=getString(ID); 
					break;
				case METHOD: 
					method = getString(METHOD); 
					break;
				default: 
					params.put(k, remove(k)); // move extras to params {}
					break; 
			}
		}

		if (method==null || method.trim().isEmpty()) throw new JSONException("The 'method' key is required.");
	}


	public String getRpcID()
	{
		return id;
	}
	
	public void setRpcID(String id) { this.id = id; }
	public void setRpcID(int id) { setRpcID(Integer.toString(id)); }
	


	/** Convenience method; same as getJSONRPC(). */
	public String getRpcVersion()
	{
		return getJSONRPC();
	}

	public String getJSONRPC()
	{
		return jsonrpc;
	}


	public String getRpcMethod()
	{
		return method;
	}


	/** 
	 * @return The JSONObject containing the RPC parameters; an empty JSONObject
	 * will be returned if the params are actually null; do not use this JSONObject
	 * to 'put' new data, since you may be writing to a shared "null params" object.
	 */
	public JSONObject getRpcParams()
	{
		return params == null ? NULL_PARAMS : params;
	}


//	public void writeExternal(ObjectOutput out) throws IOException
//	{
//		out.writeBoolean(jsonrpc!=null); if(jsonrpc!=null) out.writeUTF(jsonrpc);
//		out.writeBoolean(id!=null); if(id!=null) out.writeUTF(id);
//		out.writeBoolean(method!=null); if(method!=null) out.writeUTF(id);
//		out.writeBoolean(params!=null); if(params!=null) out.writeUTF(params.toString());
//
//	}
//
//
//	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
//	{
//		if (in.readBoolean()) jsonrpc = in.readUTF();
//		if (in.readBoolean()) id = in.readUTF();
//		if (in.readBoolean()) method = in.readUTF();
//		try { if (in.readBoolean()) params = new JSONObject(in.readUTF()); }
//		catch (JSONException je) { throw new IOException("Problem parsing the serialized params (a JSONObject).", je); }
//	}


}
