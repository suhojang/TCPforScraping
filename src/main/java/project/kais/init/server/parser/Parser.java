package project.kais.init.server.parser;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;


import com.kwic.exception.DefinedException;
import com.kwic.util.ByteUtil;
import com.kwic.util.StringUtil;
import com.kwic.web.init.Handler;
import com.kwic.xml.parser.JXParser;

public abstract class Parser {

	private static Logger log = LoggerFactory.getLogger(Parser.class); 

	/**
	 * parser의 식별자로서 NICE 스크래핑 요청KEY (NCRQ_SEQ)를 사용한다.
	 */
	private String serial;
		
	private String encoding;
	
	/**
	 * 거래구분코드
	 */
	private String bzcd;
	
	private byte[] requestBytes;
	
	private Map<String, Object> reqMap = new HashMap<String, Object>();
	
	private Map<String, Object> sysParam;

	private Handler handler;

	private int tokenIndex = 0;
	
	/**
	 * 
	 */
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("serial=").append(serial);
		sb.append("encoding=").append(encoding);
		sb.append("bzcd=").append(bzcd);	
		try {
			sb.append("requestBytes=").append(new String(requestBytes, encoding));
		} catch (UnsupportedEncodingException e) {
			sb.append("requestBytes=").append("Unknown");
		}	
		return sb.toString();
	}

	/**
	 * 원문 해석 및 DB 저장
	 * 
	 * @throws Exception
	 */
	public abstract void execute() throws Exception;

	/**
	 * 스크래핑 응답 전문 생성
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract byte[] makeResponse() throws Exception;

	/**
	 * 에러 메시지를 포함한  스크래핑 응답 전문 생성
	 * @param e
	 * @return
	 * @throws Exception
	 */
	public abstract byte[] makeResponse(Exception e) throws Exception;

	/**
	 * 사용자 요청 해석
	 * 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> parseRequest() throws Exception {
		String xmlNm = "req-" + bzcd + ".xml";
		String structPath = StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"),	"\\", "/") + File.separator + xmlNm;
		File reqBzcdXml = new File(structPath);
		JXParser jxp = new JXParser(reqBzcdXml);

		log.debug(String.format("[전문호출] Parser.parseRequest xmlNm=%s", xmlNm));
		reqMap = parseRequest(jxp, jxp.getRootElement());
		return reqMap;
	}

	/**
	 * 사용자 요청 해석하여 map으로 반환
	 * @param jxp
	 * @param parent 최상위 엘리먼트
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> parseRequest(JXParser jxp, Element parent) throws Exception {
		Element[] fields = jxp.getElements(parent, "field");

		String type = "";
		String defaultVal = "";
		int length = 0;
		String name = "";
		int loopCount = 0;
		boolean conditionMatch = false;
		
		/*
		 * xml 을 해석하여 얻은 정보를 반환할 map
		 */
		Map<String, Object> parsingMap = new HashMap<String, Object>();
		
		for (int i = 0; i < fields.length; i++) {
			name = jxp.getAttribute(fields[i], "name");
			type = jxp.getAttribute(fields[i], "type");
			
			log.debug(String.format("%d of %d : name=%s, type=%s", i, fields.length, name, type));

			if ("LST".equals(type)) {
				//리스트 형태인 경우
				loopCount = Integer.parseInt(String.valueOf(parsingMap.get(jxp.getAttribute(fields[i], "loopElement"))));

				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for (int j = 0; j < loopCount; j++) {
					list.add(parseRequest(jxp, fields[i]));
				}
				parsingMap.put(name, list);
				
			} else {
				// 조건에따라 길이가 유동적일때
				if ("conditional".equals(jxp.getAttribute(fields[i], "length"))) {
					conditionMatch = false;
					String[] lengthCondtion = jxp.getAttribute(fields[i], "lengthCondtion").split(",");
					for (int j = 0; j < lengthCondtion.length; j++) {

						if (lengthCondtion[j] == null || "".equals(lengthCondtion[j])) {
							handler.setResultCode("E841");//올바르지 않은 길이 조건
							throw new DefinedException("Invalid length condition[" + jxp.getAttribute(fields[i], "lengthCondtion") + "]");
						}

						if (lengthCondtion[j].substring(0, lengthCondtion[j].indexOf("=")).equals(parsingMap.get(jxp.getAttribute(fields[i], "target")))) {
							length = Integer.parseInt(lengthCondtion[j].substring(lengthCondtion[j].indexOf("=") + 1));
							conditionMatch = true;
							break;
						}
					}
					if (!conditionMatch) {
						handler.setResultCode("E840");//정의되지 않은 상품코드
						throw new DefinedException("Undefined length condition[" + parsingMap.get(jxp.getAttribute(fields[i], "target")) + "]");
					}
				} else {
					length = Integer.parseInt(jxp.getAttribute(fields[i], "length"));
				}
				
				defaultVal = null;
				try {
					defaultVal = jxp.getAttribute(fields[i], "default");
				} catch (Exception e) {
				}
				byte[] bytes = new byte[length];
				
				System.arraycopy(requestBytes, tokenIndex, bytes, 0, length);
				
				if ("AN".equals(type)){
					parsingMap.put(name, new String(bytes, encoding).trim());
				}
				else if ("binary".equals(type)){
					parsingMap.put(name, bytes);
				}
				else if ("N".equals(type)) {
					try {
						parsingMap.put(name, Long.parseLong(new String(bytes, encoding).trim()));
					} catch (Exception e) {
						handler.setResultCode("E842");//올바르지 않은 숫자 데이터
						throw new DefinedException("Invalid numeric data[" + name + "=" + new String(bytes, encoding).trim() + "]");
					}
				}
				if (defaultVal != null && !"".equals(defaultVal) && (parsingMap.get(name) == null || "".equals(String.valueOf(parsingMap.get(name))))) {
					parsingMap.put(name, defaultVal);
				}
				tokenIndex += length;
			}
		}
		return parsingMap;
	}

	int tmpIndex = 0;
	byte[] tmpBytes = null;
	public static final String REQUEST  = "req";
	public static final String RESPONSE = "res";

	/**
	 * 요청 또는 응답전문을 해석하여 map으로 반환한다.
	 * @param bzcd 거래구분코드 (거래구분코드 + NICE상품코드)
	 * @param bytes 해석해야 할 원문의 바이트 배열
	 * @param encoding 인코딩
	 * @param reqOrRes 요청응답구분 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> parse(String bzCd, byte[] bytes, String encoding, String reqOrRes) throws Exception {
		tmpIndex = 0;
		tmpBytes = bytes;
		String xmlNm = reqOrRes + "-" + bzCd + ".xml";
		String structPath = StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"), "\\", "/")	+ File.separator + xmlNm;
		
		File resBzcdXml = new File(structPath);
		JXParser jxp = new JXParser(resBzcdXml);

		Map<String, Object> map = parse(jxp, jxp.getRootElement(), encoding);
		log.debug(String.format("[전문호출] Parser.parse xmlNm=%s", xmlNm));	
		return map;
	}
	
	/**
	 * 로깅전문등록에서 처리응답코드를 확인하기 위한 파서
	 * @param bzcd
	 * @param requestBytes
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> parseResponseForLogging(String bzcd, byte[] requestBytes, String encoding) throws Exception {		
		tmpIndex = 0;
		tmpBytes = requestBytes;
		String xmlNm = "res-" + bzcd + ".xml";
		String structPath = StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"),	"\\", "/") + File.separator + xmlNm;
		
		File resBzcdXml = new File(structPath);
		JXParser jxp = new JXParser(resBzcdXml);

		Map<String, Object> map = parse(jxp, jxp.getRootElement(), encoding);
		log.debug(String.format("[전문호출] Parser.parseResponseForLogging xmlNm=%s, map%s", xmlNm, map));
		return map;
	}

	/**
	 * 
	 * @param jxp
	 * @param parent
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> parse(JXParser jxp, Element parent, String encoding) throws Exception {		
		Element[] fields = jxp.getElements(parent, "field");

		String type = "";
		String name = "";
		String defaultVal = "";
		int length = 0;
		int loopCount = 0;

		Map<String, Object> cMap = new HashMap<String, Object>();
		for (int i = 0; i < fields.length; i++) {
			name = jxp.getAttribute(fields[i], "name");
			type = jxp.getAttribute(fields[i], "type");

			if ("LST".equals(type)) {
				loopCount = Integer.parseInt(String.valueOf(cMap.get(jxp.getAttribute(fields[i], "loopElement"))));

				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for (int j = 0; j < loopCount; j++){
					list.add(parse(jxp, fields[i], encoding));
				}
				cMap.put(name, list);
			} else {
				// 조건에따라 길이가 유동적일때
				if ("conditional".equals(jxp.getAttribute(fields[i], "length"))) {
					String[] lengthCondtion = jxp.getAttribute(fields[i], "lengthCondtion").split(",");
					for (int j = 0; j < lengthCondtion.length; j++) {
						if (lengthCondtion[j] == null || "".equals(lengthCondtion[j])){
							continue;
						}
						if (lengthCondtion[j].substring(0, lengthCondtion[j].indexOf("=")).equals(cMap.get(jxp.getAttribute(fields[i], "target")))) {
							length = Integer.parseInt(lengthCondtion[j].substring(lengthCondtion[j].indexOf("=") + 1));
							break;
						}
					}
				} else {
					length = Integer.parseInt(jxp.getAttribute(fields[i], "length"));
				}
				
				defaultVal = null;
				try {
					defaultVal = jxp.getAttribute(fields[i], "default");
				} catch (Exception e) {
				}
				
				byte[] bytes = new byte[length];
				System.arraycopy(tmpBytes, tmpIndex, bytes, 0, length);
				
				if ("AN".equals(type)){
					cMap.put(name, new String(bytes, encoding).trim());
				}
				else if ("binary".equals(type)) {
					cMap.put(name, bytes);
				}
				else if ("N".equals(type)) {
					try {
						cMap.put(name, Long.parseLong(new String(bytes, encoding).trim()));
					} catch (Exception e) {
						handler.setResultCode("E842"); //올바르지 않은 숫자 데이터
						throw new DefinedException("Invalid numeric data[" + name + "=" + new String(bytes, encoding).trim() + "]");
					}
				}
				if (defaultVal != null && !"".equals(defaultVal) && (cMap.get(name) == null || "".equals(String.valueOf(cMap.get(name))))) {
					cMap.put(name, defaultVal);
				}

				tmpIndex += length;
			}
		}
		return cMap;
	}

	/**
	 * 응답 템플릿을 이용해 응답객체를 생성하여 바이트 배열로 반환한다.
	 * @param bzcd 거래구분코드
	 * @param info
	 * @param encoding 인코딩
	 * @return
	 * @throws Exception
	 */
	public byte[] make(String bzcd, Map<String, Object> info, String encoding) throws Exception {
		String xmlNm = "res-" + bzcd + ".xml";
		String structPath = StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"),	"\\", "/") + File.separator + xmlNm;
		JXParser jxp = new JXParser(new File(structPath));
		log.debug(String.format("[전문호출] Parser.make xmlNm=%s", xmlNm));
		byte[] bytes = make(bzcd, jxp, jxp.getRootElement(), info, encoding);
		return bytes;
	}

	/**
	 * 응답 템플릿을 이용해 응답객체를 생성하여 바이트 배열로 반환한다.
	 * @param bzCd 거래구분코드
	 * @param jxp 파서
	 * @param parent 파서의 최상위 엘리먼트
	 * @param info
	 * @param encoding 인코딩
	 * @return
	 * @throws Exception
	 */
	private byte[] make(String bzCd, JXParser jxp, Element parent, Map<String, Object> info, String encoding) throws Exception {
		Element[] fields = jxp.getElements(parent, "field");

		String name = "";
		String target = "";
		String defaultVal = "";
		byte[] val = null;
		String type = "";
		int length = 0;

		List<byte[]> byteList = new ArrayList<byte[]>();
		for (int i = 0; i < fields.length; i++) {
			name = jxp.getAttribute(fields[i], "name");
			type = jxp.getAttribute(fields[i], "type");

			if ("LST".equals(type)) {
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> list = (List<Map<String, Object>>) info.get(name);
				for (int j = 0; list != null && j < list.size(); j++) {
					byteList.add(make(bzCd, jxp, fields[i], list.get(j), encoding));
				}

			} else {
				// 조건에따라 길이가 유동적일때
				if ("conditional".equals(jxp.getAttribute(fields[i], "length"))) {
					target = jxp.getAttribute(fields[i], "target");
					
					byteList.add(make(bzCd + "-" + String.valueOf(info.get(target)), info, encoding));
				} else {
					defaultVal = null;
					try {
						defaultVal = jxp.getAttribute(fields[i], "default");
					} catch (Exception e) {
					}
					
					length = Integer.parseInt(jxp.getAttribute(fields[i], "length"));
					
					if ("AN".equals(type)) {
						val = ByteUtil.addByte((info.get(name) == null ? "" : String.valueOf(info.get(name))).getBytes(encoding), ByteUtil.APPEND_CHARACTER_SPACE, length, false, encoding);
					} else if ("binary".equals(type)) {
						val = ByteUtil.addByte((info.get(name) == null ? new byte[0] : (byte[]) info.get(name)),                  ByteUtil.APPEND_CHARACTER_SPACE, length, true, encoding);
					} else if ("N".equals(type)) {
						val = ByteUtil.addByte((info.get(name) == null ? "" : String.valueOf(info.get(name))).getBytes(encoding), ByteUtil.APPEND_CHARACTER_ZERO,  length, true, encoding);
					} else {
						val = ByteUtil.addByte((info.get(name) == null ? "" : String.valueOf(info.get(name))).getBytes(encoding), ByteUtil.APPEND_CHARACTER_SPACE, length, false, encoding);
					}
					if (defaultVal != null && !"".equals(defaultVal) && (info.get(name) == null || "".equals(String.valueOf(info.get(name))))) {
						val = defaultVal.getBytes(encoding);
					}

					byteList.add(val);
				}
			}
		}

		int tSize = 0;
		for (int i = 0; i < byteList.size(); i++) {
			tSize += byteList.get(i).length;
		}

		ByteBuffer bf = ByteBuffer.allocate(tSize);

		for (int i = 0; i < byteList.size(); i++) {
			bf.put(byteList.get(i));
		}

		return bf.array();
	}

	public static Object getBean(String name) {
		return ContextLoader.getCurrentWebApplicationContext().getBean(name);
	}

	/**
	 * 상품코드 매핑
	 * 
	 * @param info
	 */
	public String[] getKwBzCd(String SCDL_G_BZCD, Map<String, Object> info) throws Exception {
		String structPath = StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"),	"\\", "/") + File.separator + "bzcd-mapper.xml";
		JXParser jxp = new JXParser(new File(structPath));

		Element element = jxp.getElement("//mapper[@request='" + SCDL_G_BZCD + "']");
		if (element == null){
			return null;
		}

		/**
		 * 상품코드가 한개인데 이 안에서 구분값으로 AIB 업무코드를 분기하여 처리하기 위한 로직. branchParamName은
		 * 구분값의 파라미터명이며 예를들어 [req-1S101-22.xml]안에 [ISSUE_TYPE]이 해당한다.
		 * branchCondition은 구분값의 ISSUE_TYPE의 구분값에 따라서 aib , aib2 중 어떤걸 호출할지 정의하는
		 * 것이다.
		 */
		String branchParamName = jxp.getAttribute(element, "branchParamName");
		String branchCondition = jxp.getAttribute(element, "branchCondition");
		if (branchParamName != null && !"".equals(branchParamName) && branchCondition != null && !"".equals(branchCondition)) {

			String[] arr = null;
			String[] arrbrc = StringUtil.split(branchCondition, ",");
			String branchParamValue = String.valueOf(info.get(branchParamName));
			String targetAib = "";

			for (int j = 0; j < arrbrc.length; j++) {
				if (arrbrc[j] == null || "".equals(arrbrc[j])) {
					return null;
				}
				if (arrbrc[j].substring(0, arrbrc[j].indexOf("=")).equals(branchParamValue)) {
					targetAib = arrbrc[j].substring(arrbrc[j].indexOf("=") + 1);
				}
			}

			String kwCd = jxp.getAttribute(element, targetAib);
			if (kwCd == null || "".equals(kwCd)) {
				return null;
			}

			arr = StringUtil.split(kwCd, "-");
			if (arr.length != 2){
				return null;
			}

			return arr;
		}

		String kwCd = jxp.getAttribute(element, "aib");
		if (kwCd == null || "".equals(kwCd)){
			return null;
		}

		String[] arr = StringUtil.split(kwCd, "-");
		if (arr.length != 2){
			return null;
		}

		return arr;
	}
	


	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void setSysParam(Map<String, Object> sysParam) {
		this.sysParam = sysParam;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getSerial() {
		return serial;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setBzcd(String bzcd) {
		this.bzcd = bzcd;
	}

	public void setRequest(byte[] bytes) {
		this.requestBytes = bytes;
	}

	public byte[] getRequest() {
		return requestBytes;
	}

	public Map<String, Object> getReqMap() {
		return reqMap;
	}

	public String getBzcd() {
		return bzcd;
	}

	public Handler getHandler() {
		return handler;
	}

	public Map<String, Object> getSysParam() {
		return sysParam;
	}
}
