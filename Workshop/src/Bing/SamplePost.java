package Bing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class SamplePost {

   public static void post(){
      HashMap testObj = new HashMap();
      testObj.put("name", "bogus");
      testObj.put("field_type", "text_en");
      testObj.put("indexed", "true");
      testObj.put("stored", "true");
      testObj.put("search_by_default", "true");
      testObj.put("multi_valued", "true");

      JSONObject jsonObj = new JSONObject(testObj);
      System.out.println(jsonObj.toString());
      try {
         URL                 url;
         URLConnection   urlConn;
         DataOutputStream    printout;
         DataInputStream     input;
		       
         //Make the actual connection
         url = new URL ("http://example.com/restAPI");
         urlConn = url.openConnection();
         urlConn.setDoInput (true);
         urlConn.setDoOutput (true);
         urlConn.setUseCaches (false);
         urlConn.setRequestProperty("Content-Type", "application/json");
			
         //Send the JSON data
         printout = new DataOutputStream (urlConn.getOutputStream ());
         String content = jsonObj.toString();
         printout.writeBytes (content);
         printout.flush ();
         printout.close ();

         // Get response 
         input = new DataInputStream (urlConn.getInputStream ());
         String str;
         while (null != ((str = input.readLine())))
         {
            try {
               //Create the JSON object from the returned text
               JSONObject jsonObjOutput = new JSONObject(str);

               //Output the complete object
               System.out.println(jsonObjOutput.toString());

               //Output the "search_by_default" value in the returned JSON object
               System.out.println(jsonObjOutput.get("search_by_default").toString());
            } catch (JSONException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
         input.close ();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
}

