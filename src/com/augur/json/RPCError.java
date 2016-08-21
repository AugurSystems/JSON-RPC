package com.augur.json;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Implements the JSON-RPC specification (version 2.0) for a RPC error object.
 *
 * @see http://www.jsonrpc.org/specification 
* @author Chris.Janicki@augur.com
 * Copyright 2013 Augur Systems, Inc.  All rights reserved.
 */
public class RPCError extends JSONObject implements Externalizable
{
	public static final long serialVersionUID = 2056194300852796134L;
	public static final String CODE="code", MESSAGE="message", DATA="data";
	public static final int PARSE_ERROR = -32700;
	public static final int INVALID_REQUEST = -32600;
	public static final int METHOD_NOT_FOUND = -32601;
	public static final int INVALID_PARAMS = -32602;
	public static final int INTERNAL_ERROR = -32603;
	// Custom codes [-32000:-32099]
	/** A problem connecting to an external service, e.g. LDAP. */
	public static final int EXTERNAL_SVC_CONNECT_ERROR = -32000;
		

	/**
	 * Creates an RPCError.
	 *
	 * @param errorCode The int error code; it may be one of the codes defined in
	 * the JSON-RPC 2.0 Specification (included as static fields of this class),
	 * an implementation-defined server error (-32000 to -32099) or a code outside
	 * the reserved range (-32768 to -32000).
	 * 
	 * @param message A String providing a short description of the error. The
	 * message SHOULD be limited to a concise single sentence. Not null;
	 *
	 * @throws NullPointerException if the message is null.
	 */
	public RPCError(int errorCode, String message) throws NullPointerException
	{
		this(errorCode, message, (String)null);
	}
	

	/**
	 * Creates an RPCError.
	 *
	 * @param errorCode The int error code; it may be one of the codes defined in
	 * the JSON-RPC 2.0 Specification (included as static fields of this class),
	 * an implementation-defined server error (-32000 to -32099) or a code outside
	 * the reserved range (-32768 to -32000).
	 * 
	 * @param message A String providing a short description of the error. The
	 * message SHOULD be limited to a concise single sentence. Not null;
	 *
	 * @param data A Primitive or Structured value that contains additional
	 * information about the error. This may be null, in which case you should
	 * preferably use the 2-parameter constructor without a data parameter. The
	 * value of this member is defined by the Server (e.g. detailed error
	 * information, nested errors etc.).
	 * 
	 * @throws NullPointerException if the message is null.
	 */
	public RPCError(int errorCode, String message, String data) throws NullPointerException
	{
		super();
		if (message==null) throw new NullPointerException("The message parameter must not be null.");
		try
		{
			put(CODE, errorCode);
			put(MESSAGE, message);
			if (data != null) put(DATA, data);
		}
		catch (JSONException e) { e.printStackTrace(); } // shouldn't happen in this usage
	}


	/**
	 * Creates an RPCError.
	 *
	 * @param errorCode The int error code; it may be one of the codes defined in
	 * the JSON-RPC 2.0 Specification (included as static fields of this class),
	 * an implementation-defined server error (-32000 to -32099) or a code outside
	 * the reserved range (-32768 to -32000).
	 * 
	 * @param message A String providing a short description of the error. The
	 * message SHOULD be limited to a concise single sentence. Not null;
	 *
	 * @param data A Primitive or Structured value that contains additional
	 * information about the error. This may be null, in which case you should
	 * preferably use the 2-parameter constructor without a data parameter. The
	 * value of this member is defined by the Server (e.g. detailed error
	 * information, nested errors etc.).
	 * 
	 * @throws NullPointerException if the message is null.
	 */
	public RPCError(int errorCode, String message, JSONObject data) throws NullPointerException
	{
		super();
		if (message==null) throw new NullPointerException("The message parameter must not be null.");
		try
		{
			put(CODE, errorCode);
			put(MESSAGE, message);
			if (data != null) put(DATA, data);
		}
		catch (JSONException e) { e.printStackTrace(); } // shouldn't happen in this usage
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


}
