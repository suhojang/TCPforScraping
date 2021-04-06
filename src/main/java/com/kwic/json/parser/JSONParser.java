package com.kwic.json.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * <pre>
 * Title		: JJSONParser
 * Description	: JSON Parser
 * Date			: 
 * Copyright	: Copyright	(c)	2012
 * Company		: KWIC.
 * 
 *    수정일                  수정자                      수정내용
 * -------------------------------------------
 * 
 * </pre>
 *
 * @author 장정훈
 * @version	
 * @since 
 */
public class JSONParser{
	
	public static List<?> readListValues(String content) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper	= new ObjectMapper();
		return mapper.readValue(content, ArrayList.class);
	}
	
	public static Map<?,?> readMapValues(String content) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper	= new ObjectMapper();
		return mapper.readValue(content, HashMap.class);
	}
	
	public static String toJsonString(Collection<?> obj) throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper	= new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
	
	
}
