package project.kais.service;

import com.kwic.xml.parser.JXParser;

public interface PublicService {
	String getMENU_XML() throws Exception;
	
	String getURI_XML() throws Exception;
	JXParser getURI_XML_JXP() throws Exception;

	String getDVD_XML() throws Exception;
	JXParser getDVD_XML_JXP() throws Exception;
}
