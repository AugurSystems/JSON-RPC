# JSON (+RPC)
This is a fork of the very nice reference implementation by [Douglas Crockford](https://en.wikipedia.org/wiki/Douglas_Crockford), provided at [JSON.org](http://JSON.org), (which is based on [ECMA-404](http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf), "The JSON Data Interchange Standard" and also documented as [RFC 4627](https://www.ietf.org/rfc/rfc4627.txt)).  Unfortunately, when I asked Mr Crockford via email to consider extending his work to include RPC, he succinctly replied, "I don't believe in RPC."  However, he had previously welcomed me to publish any extensions myself.  So this is my result.  

There are three additions to the base code:

1. Added three classes to support [JSON-RPC v2.0](http://www.jsonrpc.org): <code>RPCError</code>, <code>RPCRequest</code>, and <code>RPCResponse</code></li>
2. Added support for comments in JSON syntax, when a line <strong>begins</strong> (<em>No leading white space!</em>) with a hash mark ('#')</li>
3. Added a utility method, <code>JSONObject.keySet()</code></li>

## Compatibility
This library should be backward compatible with any projects that used the JSON.org reference implementation.</p>

## Comment Syntax
My implementation of comments is limited to "a '#' at column zero" in order to minimally change the reference implementation's parsing code.  Note that comments are ignored (not preserved) while parsing JSON, so if you later call <code>JSONObject.toString()</code>, there will be no comments.

## Build
The ANT build script offers two sub-build targets.  The "build-core" target creates "json.jar" comprised of just the JSON and JSON-RPC classes.  That's all you need for most JSON work. The "build-plus" target creates "json+.jar" which includes the same core classes, plus JSON translations for XML, CDL (comma-delimited list), and HTTP headers.  Just run 'ant' inside the 'build' directory to build both.  The core json.jar is 40% smaller than the json+.jar file.  (Obviously you only need to use one in your project.)

# Copyright
The original work (most Java source files) are Copyright 2002 JSON.org.  
Additional work is Copyright 2010-2016 Augur Systems, Inc.  
See individual files for specifics.
