package com.fada.sellsteward.utils;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseParser<T> {
	
	 public abstract T parseJSON(String jsonStr) throws JSONException;
	 
	 /**
	  * 
	  * @param res
	 * @throws JSONException 
	  */
	 public String checkResponse(String paramString) throws JSONException{
		if(paramString==null || "".equals(paramString.trim())){ 
			return null;
		}else{
			JSONObject jsonObject = new JSONObject(paramString); 
			String result = jsonObject.getString("response");
			if(result!=null && !result.equals("error")){
				return result;
			}else{
				return null;
			}
			
		}
	 }
}
