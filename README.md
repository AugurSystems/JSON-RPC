# JSON (+RPC)
This is a fork of the reference implementation by [Douglas Crockford](https://en.wikipedia.org/wiki/Douglas_Crockford), provided at [JSON.org](http://JSON.org), (which is based on [ECMA-404](http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf), "The JSON Data Interchange Standard" and also documented as [RFC 4627](https://www.ietf.org/rfc/rfc4627.txt)).  When Mr. Crockford was asked by us to include RPC, he succinctly replied, "I don't believe in RPC."  However, he welcomed any forks.  This is this result.  

There are three additions to the base code:

1. Added three classes to support [JSON-RPC v2.0](http://www.jsonrpc.org): <code>RPCError</code>, <code>RPCRequest</code>, and <code>RPCResponse</code></li>
2. Added support for comments in JSON syntax, prefixed by a '#' hash mark (octothorpe)</li>
3. Added utility methods, <code>JSONObject.keySet()</code> and <code>JSONObject.putAll()</code></li>

## Compatibility
This library should be backward compatible with any projects that used the JSON.org reference implementation.</p>

## Comment Syntax
Comments (prefixed with '#') are supported, and useful for documenting hand-edited JSON files.  However, comments are ignored (not preserved) while parsing JSON, so if you later call <code>JSONObject.toString()</code>, there will be no comments.  Since comments are not part of the official JSON specification, you will just have to ignore any corresponding syntax errors flagged by your editor.

## Build
The ANT build script offers two build sub-targets.  The "build-core" target creates "json.jar" comprised of just the JSON and JSON-RPC classes.  That's all you need for most JSON work. 

The "build-plus" target creates "json+.jar" which includes the same core classes, plus extras that were included in the original JSON.org library, specifically:  JSON translations for XML, CDL (comma-delimited list), and HTTP headers.  

Just run 'ant' inside the 'build' directory to build both.  The core json.jar is 40% smaller than the json+.jar file.  (Obviously you only need one in your classpath.)

## Copyright
The original work (most Java source files) are Copyright 2002 JSON.org.  
Additional work is Copyright 2010-2025 Augur Systems, Inc.  
See individual files for specifics.
