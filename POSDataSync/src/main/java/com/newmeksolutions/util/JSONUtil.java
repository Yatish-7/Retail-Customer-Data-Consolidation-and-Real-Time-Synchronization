package com.newmeksolutions.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil 
{

	// This code Converts the Any class object into JSON and Returns the JSON Value of the Object //
	private static final ObjectMapper mapper = new ObjectMapper();

	    public static String toJSON(Object obj) throws Exception 
	    {
	        return mapper.writeValueAsString(obj);
	    }
}