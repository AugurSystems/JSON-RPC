package com.augur.json;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Implements the JSON-RPC specification (version 2.0) for a RPC request object.
 *
 * @author Chris.Janicki@augur.com
 * @see http://www.jsonrpc.org/specification
 *
 * Copyright 2010-2013 Augur Systems, Inc.  All rights reserved.
 */

public class RPCResponse extends JSONObject implements Externalizable
{
	public static final long serialVersionUID = 8335194366852795449L;
	public static final String VERSION = "2.0";
	public static final String JSONRPC="jsonrpc", ID="id", RESULT="result", ERROR="error";


	/**
	 * Used to create a new RPCResponse to a JSON-RPC request; note that either 
	 * result or error <em>must</em> be null (and the other non-null),
	 * even though it is not enforced here.
	 *
	 * @param id
	 * @param result  Usually a String or JSONObject
	 * @param error  A String error message
	 * @throws JSONException
	 */
	private RPCResponse(String id, Object result, RPCError error) 
	{
		super();
    if (result!=null) put(RESULT, result);
    if (error!=null) put(ERROR, error);
    put(JSONRPC, VERSION);
    put(ID, id);
		//if (id==null) throw new JSONException("The request is a notification ('id' key is null); no response is necessary.");
	}


	private RPCResponse(String response) throws JSONException
	{
		super(response);
	}
	
	
	/**
	 * Constructs a new RPCResponse.
	 * @param rpcRequest  The originating RPCRequest
	 * @param result  The non-null JSONOBject which is the RPC result.
	 * @return a new RPCResponse
	 * @throws NullPointerException if either parameter is null.
	 */
	public static RPCResponse newResult(RPCRequest rpcRequest, JSONObject result)
	{
		if (rpcRequest==null || rpcRequest.id==null) throw new NullPointerException("The id parameter must not be null.");
		if (result==null) throw new NullPointerException("The result parameter must not be null.");
		return new RPCResponse(rpcRequest.id, result, null);
	}
	
	
	/**
	 * Constructs a new RPCResponse.
	 * @param rpcRequest  The originating RPCRequest
	 * @param result  The non-null JSONOBject which is the RPC result.
	 * @return a new RPCResponse
	 * @throws NullPointerException if either parameter is null.
	 */
	public static RPCResponse newResult(RPCRequest rpcRequest, JSONArray result)
	{
		if (rpcRequest==null || rpcRequest.id==null) throw new NullPointerException("The id parameter must not be null.");
		if (result==null) throw new NullPointerException("The result parameter must not be null.");
		return new RPCResponse(rpcRequest.id, result, null);
	}
	
	
	/**
	 * Constructs a new RPCResponse.
	 * @param rpcRequest  The originating RPCRequest
	 * @param result  The non-null String which is the RPC result.
	 * @return a new RPCResponse
	 * @throws NullPointerException if either parameter is null.
	 */
	public static RPCResponse newResult(RPCRequest rpcRequest, String result)
	{
		if (rpcRequest==null || rpcRequest.id==null) throw new NullPointerException("The id parameter must not be null.");
		if (result==null) throw new NullPointerException("The result parameter must not be null.");
		return new RPCResponse(rpcRequest.id, result, null);
	}
	

	/**
	 * Constructs a new RPCResponse.

	 *
	 * @param rpcRequest The originating RPCRequest, or null if couldn't be
	 * determined due to the error
	 * @param error  The non-null String describing the RPC error.
	 * @return a new RPCResponse
	 * @throws NullPointerException if either parameter is null.
	 */
	public static RPCResponse newError(RPCRequest rpcRequest, RPCError error)
	{
		if (error==null) throw new NullPointerException("The error parameter must not be null.");
		return new RPCResponse(rpcRequest==null?null:rpcRequest.id, null, error);
	}
	
	
	/**
	 * A convenience method that constructs the RPCError object in its typical
	 * form (without extra data). If extra data is needed, construct your own
	 * RPCError and pass it in a call to the other newError() method.
	 *
	 * @param rpcRequest The originating RPCRequest, or null if couldn't be
	 * determined due to the error
	 *
	 * @param errorCode The int error code; it may be one of the codes defined in
	 * the JSON-RPC 2.0 Specification (included as static fields of this class),
	 * an implementation-defined server error (-32000 to -32099) or a code outside
	 * the reserved range (-32768 to -32000).
	 *
	 * @param message A String providing a short description of the error. The
	 * message SHOULD be limited to a concise single sentence. Not null;
	 *
	 * @return The configured RPCReponse, ready to send.
	 */
	public static RPCResponse newError(RPCRequest rpcRequest, int errorCode, String message)
	{
		return newError(rpcRequest, new RPCError(errorCode, message));
	}
	
	
	/**
	 * Used to parse a response received from a JSON-RPC request.
	 *
	 * @param response The JSON-formatted response object.
	 * @throws JSONException
	 */
	public static RPCResponse newParsed(String response) throws JSONException
	{
		RPCResponse r = new RPCResponse(response);
		//
		String jsonrpc=r.getJSONRPC(), id=r.getID(), result=r.getResult(), error=r.getError();
		if (id==null) throw new JSONException("The 'id' key is required.");
		if (result==null && error==null) throw new JSONException("Either 'result' or 'error' must be defined");
		if (result!=null && error!=null) throw new JSONException("Either 'result' or 'error' must be undefined");
		if (jsonrpc==null) throw new JSONException("The 'jsonrpc' key is required.");
		if (!jsonrpc.equals(VERSION)) throw new JSONException("Bad version.  Expected "+VERSION+", received "+jsonrpc+".");
		
		return r;
	}
	

	/**
	 * Required during unmarshalling, only.
	 */
	public RPCResponse()
	{
		super();
	}


	public final String getID()
	{
		return getValue(ID);
	}


	/** Convenience method; same as getJSONRPC(). */
	public final String getVersion()
	{
		return getJSONRPC();
	}

	public final String getJSONRPC()
	{
		return getValue(JSONRPC);
	}


	public final String getResult()
	{
		return getValue(RESULT);
	}


	public final String getError()
	{
		return getValue(ERROR);
	}

	private String getValue(String key)
	{
		try { return getString(key); }
		catch (JSONException e) { return null; }
	}

	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeUTF(toString());
	}


	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		try { parse(in.readUTF()); }
		catch (JSONException je) { throw new IOException("Problem parsing JSON", je); }
	}

	
	/** Escapes any special characters, per JSON spec. */
	public static String encode(String s)
	{
		s = s.replace("\\", "\\\\");
		s = s.replace("\"", "\\\"");
		s = s.replaceAll("[\r\n]", " ");
		return s.trim();
	}
	
	
	
}
