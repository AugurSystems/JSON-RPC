package com.augur.json;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility to test validity of a JSON file. 
 * @author Augur Systems, Inc.
 */
public class Main
{
  public static void main(String[] args)
  {
    if (args.length<1) usage("java -jar json.jar");
    String fname = args[0];
    test(Path.of(fname), args.length>1 && args[1].equals("-p"));
  }
  
  public static void test(Path p, boolean print)
  {
    try 
    {
      String json = Files.readString(p);
      switch (firstChar(json)) 
      {
        case '{' ->           
        {
          JSONObject jo = new JSONObject(json);
          if (print) System.out.println(jo.toString(2));
          else System.out.println("JSON object is OK");
        }
        case '[' -> 
        {
          JSONArray jo = new JSONArray(json);
          if (print) System.out.println(jo.toString(2));
          else System.out.println("JSON array is OK");
        }
        default -> throw new JSONException("Expected JSON to start with '{' or '['");
      }
      System.exit(0); // success
    }
    catch (MalformedInputException mie) { System.out.println("Unable to read the file as a text string"); }
    catch (IOException ioe) { System.out.println(ioe); }
    catch (JSONException je) { System.out.println(je.getMessage()); }
    System.exit(1); // some error
  }
  
  public static void usage(String exe)
  {
    System.out.println("Verify syntax of a JSON file, else display the parsing error.");
    System.out.println("Usage: "+exe+" <file> [-p]");
    System.out.println();
    System.out.println("  <file>  The *.json file to be parsed");
    System.out.println("  -p      Print as standard JSON (\"pretty\" format, no comments)");
    System.out.println();
    System.exit(1);
  }
  
  
  public static char firstChar(String json)
  {
    for (int i=0; i<json.length(); i++)
    {
      char c = json.charAt(i);
      if (Character.isWhitespace(c)) continue;
      else if (c=='#') { for (i++; i<json.length(); i++) if (json.charAt(i)=='\n') break; }
      else return c;
    }
    return 0;
  }
  
}