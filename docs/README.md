# JSON (+RPC)
		This is a fork of the very nice reference implementation by 
		[Douglas Crockford](https://en.wikipedia.org/wiki/Douglas_Crockford), 
		provided at [JSON.org](http://JSON.org), 
		(which is based on [ECMA-404](http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf), 
		"The JSON Data Interchange Standard"
		and also documented as [RFC 4627](https://www.ietf.org/rfc/rfc4627.txt)).  
		Unfortunately, when I asked Mr Crockford via email to consider extending his work to include RPC, he succinctly replied, "I don't believe in RPC."  
		However, he had previously welcomed me to publish any extensions myself.
		So this is my result.  

		There are three additions to the base code:
		<ol>
			<li>Added three classes to support [JSON-RPC v2.0](http://www.jsonrpc.org): <code>RPCError</code>, <code>RPCRequest</code>, and <code>RPCResponse</code></li>
			<li>Added support for comments in JSON syntax, when a line <strong>begins</strong> (<em>No leading white space!</em>) with a hash mark ('#')</li>
			<li>Added a utility method, <code>JSONObject.keySet()</code></li>
		</ol>

		## Compatibility
		This library should be backward compatible with any projects that used the JSON.org reference implementation.</p>

		## Comment Syntax
		My implementation of comments is limited to "a '#' at column zero" in order to minimally change the reference implementation's parsing code.  
		Note that comments are ignored (not preserved) while parsing JSON, so if you later call <code>JSONObject.toString()</code>, there will be no comments.
		<em>If you can add greater support for comments, in an elegant manner, 
			I'd be glad to merge your suggested code.</em>

# Copyright
The original work (most Java source files) are Copyright 2002 JSON.org.  
Additional work is Copyright 2010-2016 Augur Systems, Inc.  
See individual files for specifics.
