package project.kais.init.client.except;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.jaxen.JaxenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;

import com.kwic.exception.DefinedException;
import com.kwic.util.ByteUtil;
import com.kwic.util.StringUtil;
import com.kwic.xml.parser.JXParser;

/**
 * 예외처리
 * @author ykkim
 *
 */
public abstract class Except {

	private static Logger log = LoggerFactory.getLogger(Except.class);
	
	public static final String VALUE_FILTER_SSN = "SSN";
	public static final String VALUE_FILTER_AMT = "AMT";
	public static final String VALUE_FILTER_DATE = "DATE";
	public static final String VALUE_FILTER_DATETIME = "DATETIME";

	/**
	 * 요청정보를 스크래핑 정보로 해석
	 * @param aibReqParam
	 * @param request
	 * @param sysParam
	 * @return
	 * @throws Exception
	 */
	public abstract Map<String, String> exceptRequest(Map<String, String> aibReqParam, Map<String, Object> request, Map<String, Object> sysParam) throws Exception;

	/**
	 * 응답정보를 스크래핑 정보로 해석
	 * @param aibResParam
	 * @param response
	 * @param sysParam
	 * @return
	 * @throws Exception
	 */
	public abstract Map<String, Object> exceptResponse(Map<String, Object> aibResParam, Map<String, Object> response, Map<String, Object> sysParam) throws Exception;

	/**
	 * 스크패핑 요청을 위한 AIB 전문 매핑 <br/>
	 * AIB 상풐코드 : 전문코드(4자리) + 업무코드(1~2자리) <br/>
	 *    0004-2 : 도로교통공단 운전면호증 진위여부 <br/>
	 *    0005-5 : 민원24 주민등록증 진위여부  <br/>
	 *    0008-4 : 민원24 인감증명서 발급사실 확인 <br/>
	 *    0009-50: 자동차등록원부(갑,을,소유자검증) 발급 <br/>
	 *    0019-6 : 민원24 주민등록 등초본 발급이력조회 	
	 * @param SPECIALCODE
	 * @param MODULE
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> mapAibRequest(String SPECIALCODE, String MODULE, Map<String, Object> request) throws Exception {
		Map<String, String> param = new HashMap<String, String>();
		String xmlNm = "scrap-" + SPECIALCODE + "-" + MODULE + "-req.xml";
		String structPath = StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext() .getRealPath("/WEB-INF/struct"), "\\", "/")	+ File.separator + xmlNm;
		JXParser jxp = new JXParser(new File(structPath));
		log.debug(String.format("[전문호출] Except.mapAibRequest xmlNm=%s, param=%s", xmlNm, param));
		
		Element[] fields = jxp.getElements("//element");
		String name = null;
		String defaultVal = null;
		String mappingElement = null;
		String val = null;
		for (int i = 0; i < fields.length; i++) {
			name           = jxp.getAttribute(fields[i], "name");
			defaultVal     = jxp.getAttribute(fields[i], "default");
			mappingElement = jxp.getAttribute(fields[i], "mappingElement");

			val = String.valueOf(request.get(mappingElement));
			if (mappingElement == null || "".equals(mappingElement)){
				val = defaultVal;
			}else if (defaultVal != null && (val == null || "".equals(val))) {
				val = defaultVal;
			}
			log.debug(String.format("AIB scrapping xml : name=%s, defaultVal=%s, mappingElement=%s, val=%s", name, defaultVal, mappingElement, val));
			
			if ("REGISELECT".equals(name)) {
				log.debug("name : "+name + ", value : " + val);
			}
			param.put(name, val);
		}
		
		return param;
	}

	/**
	 * NICE  요청 정보 매핑
	 * @param bzCd
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> mapNiceRequest(String bzCd, Map<String, Object> response) throws Exception {
		String xmlNm = "req-" + bzCd + ".xml";
		String structPath = StringUtil.replace(	ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"), "\\", "/") + File.separator + xmlNm;
		JXParser parser = new JXParser(new File(structPath));

		String aibResponse = String.valueOf(response.get("SCDL_ABRS")); //AIB 응답 XML
		
		if (aibResponse == null || "".equals(aibResponse.trim())){
			aibResponse = "<?xml version='1.0' encoding='UTF-8'?><OUTPUT></OUTPUT>";
		}
		JXParser aibResponsepParseer = new JXParser(aibResponse);
		String SCDL_G_BZCD = String.valueOf(response.get("SCDL_G_BZCD")); //NICE 상품코드
		
		Map<String, Object> reqMap	= null;
		reqMap = mapNiceRequest(parser.getRootElement(), aibResponsepParseer.getRootElement(), SCDL_G_BZCD, parser, aibResponsepParseer);
		
		log.debug(String.format("[전문호출] Except.mapNiceRequest bzCd=%s, xmlNm=%s, reqMap=%s", bzCd, xmlNm, reqMap));
		return reqMap;
	}

	/**
	 * 
	 * @param structParent
	 * @param aibParent
	 * @param SCDL_G_BZCD
	 * @param parser
	 * @param aibResponsepParseer
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> mapNiceRequest(Element structParent, Element aibParent, String SCDL_G_BZCD, JXParser parser, JXParser aibResponsepParseer) throws Exception {
		Map<String, Object> info = new HashMap<String, Object>();

		Element[] fields = parser.getElements(structParent, "field");
		String name = null;
		String type = null;
		String defaultVal = null;
		String xpath = null;
		String val = null;
		String filter = null;
		String pnode = null;
		Element element = null;
		String loopElement = null;
		List<Map<String, Object>> list = null;
		Element[] aibChildren = null;

		for (int i = 0; i < fields.length; i++) {
			name		= parser.getAttribute(fields[i], "name");
			type		= parser.getAttribute(fields[i], "type");
			defaultVal	= parser.getAttribute(fields[i], "default");
			xpath		= parser.getAttribute(fields[i], "xpath" + SCDL_G_BZCD);
			filter		= parser.getAttribute(fields[i], "filter");
			pnode		= parser.getAttribute(fields[i], "pnode");
			val = null;
			
			if ("LST".equals(type)) {
				list = new ArrayList<Map<String, Object>>();
				aibChildren = aibResponsepParseer.getElements(aibParent, xpath);
				loopElement = parser.getAttribute(fields[i], "loopElement");
				
				if (info.get(loopElement)==null || "".equals(String.valueOf(info.get(loopElement)))) {
					info.put(loopElement, String.valueOf(aibChildren.length));
				} else {
					info.put(loopElement, String.valueOf(Integer.parseInt(String.valueOf(info.get(loopElement))) + aibChildren.length));
				}
				
				for (int j = 0; aibChildren != null && j < aibChildren.length; j++) {
					list.add(mapNiceRequest(fields[i], aibChildren[j], SCDL_G_BZCD, parser, aibResponsepParseer));
				}

				info.put(name, list);
			} else {
				if (xpath != null && !"".equals(xpath)) {
					if (xpath.startsWith("//")) {
						element	= aibResponsepParseer.getElement(xpath);
					}else{
						element = aibResponsepParseer.getElement(aibParent, xpath);
					}
					if (element != null){
						val = aibResponsepParseer.getAttribute(element, "VALUE");
					}
				}
				if (pnode != null && "Y".equals(pnode)) {
					element	= aibParent.getParent();
					if (element != null) {
						val	= aibResponsepParseer.getAttribute(element, "VALUE");
					}
				}
				
				if (val == null || "".equals(val)){
					val = defaultVal == null ? "" : defaultVal;
				}
				val = valueFilter(val, filter);
				info.put(name, val);
			}
		}
		return info;
	}
	
	/**
	 * 
	 * @param structParent
	 * @param aibParent
	 * @param SCDL_G_BZCD
	 * @param parser
	 * @param aibResponsepParseer
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> mapNiceRequest2(Element structParent, Element aibParent, String SCDL_G_BZCD, JXParser parser, JXParser aibResponsepParseer) throws Exception {
		Map<String, Object> info = new HashMap<String, Object>();

		Element[] fields = parser.getElements(structParent, "field");
		String name = null;
		String type = null;
		String defaultVal = null;
		String xpath = null;
		String val = null;
		String filter = null;
		String pnode = null;
		Element element = null;
		String loopElement = null;
		List<Map<String, Object>> list = null;
		Element[] aibChildren = null;

		for (int i = 0; i < fields.length; i++) {
			name		= parser.getAttribute(fields[i], "name");
			type		= parser.getAttribute(fields[i], "type");
			defaultVal	= parser.getAttribute(fields[i], "default");
			xpath		= parser.getAttribute(fields[i], "xpath" + SCDL_G_BZCD);
			filter		= parser.getAttribute(fields[i], "filter");
			pnode		= parser.getAttribute(fields[i], "pnode");
			val = null;
			
			if ("LST".equals(type)) {
				list = new ArrayList<Map<String, Object>>();
				aibChildren = aibResponsepParseer.getElements(aibParent, xpath);
				loopElement = parser.getAttribute(fields[i], "loopElement");
				
				if (info.get(loopElement)==null || "".equals(String.valueOf(info.get(loopElement)))) {
					info.put(loopElement, String.valueOf(aibChildren.length));
				} else {
					info.put(loopElement, String.valueOf(Integer.parseInt(String.valueOf(info.get(loopElement))) + aibChildren.length));
				}
				
				for (int j = 0; aibChildren != null && j < aibChildren.length; j++) {
					list.add(mapNiceRequest2(fields[i], aibChildren[j], SCDL_G_BZCD, parser, aibResponsepParseer));
				}

				info.put(name, list);
			} else {
				if (xpath != null && !"".equals(xpath)) {
					element = aibResponsepParseer.getElement(aibParent, xpath);
					if (element != null){
						val = aibResponsepParseer.getAttribute(element, "VALUE");
					}
				}
				//차량전손횟수 예외 케이스 -> 상위노드의 피해구분 값을 적용하기 위한 로직
				if (pnode != null && "Y".equals(pnode)) {
					element	= aibParent.getParent();
					if (element != null) {
						val	= aibResponsepParseer.getAttribute(element, "VALUE");
					}
				}
				
				if (val == null || "".equals(val)){
					val = defaultVal == null ? "" : defaultVal;
				}
				val = valueFilter(val, filter);
				info.put(name, val);
			}
		}
		return info;
	}

	/**
	 * 응답전문 해석하여 바이트 배열로 반환
	 * @param bzCd
	 * @param info
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public byte[] makeNiceRequestBytes(String bzCd, Map<String, Object> info, String encoding) throws Exception {
		String xmlNm = "req-" + bzCd;
		String structPath = StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"), "\\", "/") + File.separator + xmlNm + ".xml";
		log.debug(String.format("[전문호출] Except.makeNiceRequestBytes xmlNm=%s.xml", xmlNm));
		
		JXParser jxp = new JXParser(new File(structPath));
		List<byte[]> byteList = new ArrayList<byte[]>();
		makeNiceRequestBytes(bzCd, jxp.getRootElement(), info, encoding, jxp, byteList);

		int totSz = 0;
		for (int i = 0; i < byteList.size(); i++){
			totSz += byteList.get(i).length;
		}

		ByteBuffer bf = ByteBuffer.allocate(totSz);
		for (int i = 0; i < byteList.size(); i++){
			bf.put(byteList.get(i));
		}
		
		return bf.array();
	}

	/**
	 * 요청전문을 템플릿으로 해석하여 4번째 파라미타인 바이트 배열에 저장한다.
	 * @param bzCd
	 * @param structParent
	 * @param info
	 * @param encoding
	 * @param jxp
	 * @param byteList
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void makeNiceRequestBytes(String bzCd, Element structParent, Map<String, Object> info, String encoding, JXParser jxp, List<byte[]> byteList) throws Exception {
		Element[] fields = jxp.getElements(structParent, "field");

		String type = null;
		String name = null;
		int length = 0;
		byte[] val = null;
		List<Map<String, Object>> list = null;

		for (int i = 0; i < fields.length; i++) {
			name = jxp.getAttribute(fields[i], "name");
			type = jxp.getAttribute(fields[i], "type");

			if ("LST".equals(type)) {
				list = (List<Map<String, Object>>) info.get(name);
				if (list == null){
					continue;
				}
				for (int j = 0; j < list.size(); j++){
					makeNiceRequestBytes(bzCd, fields[i], list.get(j), encoding, jxp, byteList);
				}
			} else {
				length = Integer.parseInt(jxp.getAttribute(fields[i], "length"));
				if ("AN".equals(type)) {
					log.debug(String.format("[전문매핑 확인] makeNiceRequestBytes type=%s, name=%s, info.get(name)=%s", type, name, (info.get(name) == null ? "없다" : info.get(name)) ));
					val = ByteUtil.addByte((info.get(name) == null ? "" : String.valueOf(info.get(name))).getBytes(encoding),	ByteUtil.APPEND_CHARACTER_SPACE, length, false, encoding);
				}
				else if ("binary".equals(type)){
					val = ByteUtil.addByte((info.get(name) == null ? new byte[0] : (byte[]) info.get(name)),					ByteUtil.APPEND_CHARACTER_SPACE, length, true, encoding);
				}
				else if ("N".equals(type)) {
					val = ByteUtil.addByte((info.get(name) == null ? "" : String.valueOf(info.get(name))).getBytes(encoding),	ByteUtil.APPEND_CHARACTER_ZERO, length, true, encoding);
				}
				else{
					val = ByteUtil.addByte((info.get(name) == null ? "" : String.valueOf(info.get(name))).getBytes(encoding),	ByteUtil.APPEND_CHARACTER_SPACE, length, false, encoding);
				}
				byteList.add(val);
			}
		}
	}

	int tmpIndex = 0;
	byte[] tmpBytes = null;

	/**
	 * 
	 * @param bzCd
	 * @param bytes
	 * @param encoding
	 * @param reqOrRes
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> mapNiceResponse(String bzCd, byte[] bytes, String encoding, String reqOrRes) throws Exception {
		tmpIndex = 0;
		tmpBytes = bytes;
		String xmlNm = reqOrRes + "-" + bzCd + ".xml";
		String structPath = StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"), "\\", "/") + File.separator  + xmlNm;
		JXParser jxp = new JXParser(new File(structPath));

		Map<String, Object> map = mapNiceResponse(jxp, jxp.getRootElement(), encoding);
		log.debug(String.format("[전문호출] Except.mapNiceResponse bzCd=%s, xmlNm=%s", bzCd, xmlNm));
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
	private Map<String, Object> mapNiceResponse(JXParser jxp, Element parent, String encoding) throws Exception {
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
					list.add(mapNiceResponse(jxp, fields[i], encoding));
				}
				cMap.put(name, list);
				
			} else {
				// 조건에따라 길이가 유동적일때
				if ("conditional".equals(jxp.getAttribute(fields[i], "length"))) {
					String[] lengthCondtion = jxp.getAttribute(fields[i], "lengthCondtion").split(",");
					for (int j = 0; j < lengthCondtion.length; j++) {
						if (lengthCondtion[j] == null || "".equals(lengthCondtion[j])) {
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
				} catch (JaxenException e) {
					log.error("Invalid default value.", e);
				}
				
				//공통부만 왔을 경우 처리 로직
				if (tmpIndex >= new String(tmpBytes, encoding).length())
					break;
				
				byte[] bytes = new byte[length];
				System.arraycopy(tmpBytes, tmpIndex, bytes, 0, length);
				if ("AN".equals(type)){
					cMap.put(name, new String(bytes, encoding).trim());
				}
				else if ("binary".equals(type)){
					cMap.put(name, bytes);
				}
				else if ("N".equals(type)) {
					String value = new String(bytes, encoding).trim();
					try {
						log.debug("value >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+value);
						cMap.put(name, Long.parseLong(value));
					} catch (Exception e) {
						log.error(String.format("Invalid numeric value. name=%s, value=%s", name, value), e);
						throw new DefinedException("Invalid numeric data[" + name + "=" + value + "]");
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
	 * 상품코드 매핑
	 * 거래구분코드(5자리) + 상품코드(2자리)로 NICE와 AIB 상품코드를 매핑한다.
	 */
	public String getResponseBzCd(String SCDL_G_BZCD) throws Exception {
		String structPath = StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"), "\\", "/") + File.separator + "bzcd-mapper.xml";
		JXParser jxp = new JXParser(new File(structPath));

		Element element = jxp.getElement("//mapper[@request='" + SCDL_G_BZCD + "']");
		if (element == null) {
			return null;
		}

		String rscd = jxp.getAttribute(element, "response");
		if (rscd == null || "".equals(rscd)) {
			return null;
		}

		log.debug(String.format("BzCd Mapping result : %s = %s in %s", SCDL_G_BZCD, rscd, structPath));
		return rscd;
	}

	/**
	 * 주민등록번호, 금액, 날짜를 필터에 맞게 가공하여 반환한다. <br/>
	 * 주민등록번호 : 공백, '-' 제거, 뒤 7자리 '*' 표시  <br/>
	 * 금액 : ',' 제거  <br/>
	 * 날자 : '-' 등의 구분자 제거
	 * @param value
	 * @param filterType
	 * @return
	 */
	public static String valueFilter(String value, String filterType) {
		String filterValue = value;
		if (filterType == null || "".equals(filterType) || value == null || "".equals(value)) {
			return value;
		} else if (VALUE_FILTER_SSN.equals(filterType)) {
			filterValue = StringUtil.replace(filterValue, "-", "").trim();
			if (filterValue.length() == 13) {
				//filterValue = filterValue.substring(0, 6) + "*******";
			}
		} else if (VALUE_FILTER_AMT.equals(filterType)) {
			filterValue = StringUtil.replace(filterValue, ",", "").trim();
		} else if (VALUE_FILTER_DATE.equals(filterType)) {
			filterValue = StringUtil.replace(filterValue, "-", "").trim();
			filterValue = StringUtil.replace(filterValue, ".", "").trim();
			filterValue = StringUtil.replace(filterValue, "/", "").trim();
		} else if (VALUE_FILTER_DATE.equals(filterType)) {
			filterValue = StringUtil.replace(filterValue, "-", "").trim();
			filterValue = StringUtil.replace(filterValue, ".", "").trim();
			filterValue = StringUtil.replace(filterValue, "/", "").trim();
			filterValue = StringUtil.replace(filterValue, ":", "").trim();
			filterValue = StringUtil.replace(filterValue, " ", "").trim();
		}
		return filterValue;
	}
}
