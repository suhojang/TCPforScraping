package project.kais.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import project.kais.service.PublicService;

import com.kwic.xml.parser.JXParser;

/**
 * 서버 1대일때는 각 XML들의 변경 시 바로 반영이 되지만
 * 서버가 여러대일 경우에는 작업이 일어난 서버만 변경되고 나머지 서버는 리스타트 후 반영된다.
 * 이에 대한 보안을 숙고해 보자.
 * 
 * */
@Service("PublicService")
public class PublicServiceImpl implements PublicService{
	@Resource(name = "PublicDao")
	private PublicDao dao;
	
	public synchronized String getMENU_XML(){
		Map<String,Object> info	= dao.Public001();
		return (String)info.get("MNLST_XML");
	}
	public synchronized String getURI_XML() throws Exception{
		Map<String,Object> info	= dao.Public002();
		return (String)info.get("URLST_XML");
	}
	public synchronized JXParser getURI_XML_JXP() throws Exception{
		Map<String,Object> info	= dao.Public002();
		return new JXParser((String)info.get("URLST_XML"));
	}
	public synchronized String getDVD_XML() throws Exception{
		Map<String,Object> info	= dao.Public003();
		return (String)info.get("CNSDVD_XML");
	}
	public synchronized JXParser getDVD_XML_JXP() throws Exception{
		Map<String,Object> info	= dao.Public003();
		return new JXParser((String)info.get("CNSDVD_XML"));
	}
}
