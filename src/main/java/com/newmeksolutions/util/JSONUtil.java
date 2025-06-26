package com.newmeksolutions.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil 
{
	private static final ObjectMapper mapper = new ObjectMapper();

	    public static String toJSON(Object obj) throws Exception 
	    {
	        return mapper.writeValueAsString(obj);
	    }
}