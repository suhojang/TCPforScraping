package project.kais.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import project.kais.service.ADM_PCD_Service;
import project.kais.service.ADM_PRS_Service;

import com.kwic.security.aes.AESCipher;
import com.kwic.util.StringUtil;
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;

@Controller
public class ADM_PRS_Controller extends Controllers {

	private static Logger log = LoggerFactory.getLogger(ADM_PRS_Controller.class);

	@Resource(name = "propertiesService")
	private EgovPropertyService properties;

	/** service */
	@Resource(name = "ADM_PRS_Service")
	private ADM_PRS_Service service;

	/** service */
	@Resource(name = "ADM_PCD_Service")
	private ADM_PCD_Service pcdService;

	/**
	 * 처리현황 화면 호출
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/ADM_PRS_010000/")
	public String ADM_PRS_010000(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		List<Map<String, Object>> cd0002 = pcdService.ADM_PCD_S1200A("0002");
		List<Map<String, Object>> cd0003 = pcdService.ADM_PCD_S1200A("0003");
		model.addAttribute("CD0002", cd0002);
		model.addAttribute("CD0003", cd0003);
		return "/ADM/PRS/ADM_PRS_010000";
	}

	/**
	 * 처리현황 목록 조회
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value = "/ADM_PRS_S1000A/")
	public void ADM_PRS_S1000A(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		try {
			String SCDL_G_BZCD  = getParam(request, "SCDL_G_BZCD", 2);
			String SCDL_BZCD    = getParam(request, "SCDL_BZCD", 4);
			String SCDL_JBCD    = getParam(request, "SCDL_JBCD", 3);
			String SCDL_SCAF_ID = getParam(request, "SCDL_SCAF_ID", 12);
			String SCDL_STS     = getParam(request, "SCDL_STS", 12);
			String SCDL_RTCD    = getParam(request, "SCDL_RTCD", 4);
			String SCDL_RDT_FR  = getParam(request, "SCDL_RDT_FR", 10);
			String SCDL_RDT_TO  = getParam(request, "SCDL_RDT_TO", 10);
			String PAGE_NO      = getParam(request, "PAGE_NO", 5);
			String ROWPERPAGE   = getParam(request, "ROWPERPAGE", 3);

			SCDL_RDT_FR = StringUtil.replace(StringUtil.replace(SCDL_RDT_FR, "-", ""), ".", "");
			SCDL_RDT_TO = StringUtil.replace(StringUtil.replace(SCDL_RDT_TO, "-", ""), ".", "");
			
			String BF_YYMM	= SCDL_RDT_FR.substring(2, 6);
			String YYMM		= SCDL_RDT_TO.substring(2, 6);

			if (PAGE_NO == null || "".equals(PAGE_NO)) {
				PAGE_NO = "1";
			}

			if (ROWPERPAGE == null || "".equals(ROWPERPAGE)) {
				ROWPERPAGE = properties.getString("pageSize");
			}

			String STNO = String.valueOf((Integer.parseInt(PAGE_NO) - 1) * Integer.parseInt(ROWPERPAGE) + 1);
			String EDNO = String.valueOf(Integer.parseInt(PAGE_NO) * Integer.parseInt(ROWPERPAGE));

			Map<String, String> param = new HashMap<String, String>();
			param.put("BF_YYMM", BF_YYMM);
			param.put("YYMM", YYMM);
			param.put("SCDL_G_BZCD", SCDL_G_BZCD);
			param.put("SCDL_BZCD", SCDL_BZCD);
			param.put("SCDL_JBCD", SCDL_JBCD);
			param.put("SCDL_SCAF_ID", SCDL_SCAF_ID);
			param.put("SCDL_STS", SCDL_STS);
			param.put("SCDL_RTCD", SCDL_RTCD);
			param.put("SCDL_RDT_FR", SCDL_RDT_FR);
			param.put("SCDL_RDT_TO", SCDL_RDT_TO);
			param.put("STNO", STNO);
			param.put("EDNO", EDNO);

			List<Map<String, Object>> list = null;
			String TCNT = "0";
			Map<String, Object> ajax = new HashMap<String, Object>();
			if (!service.CHECK_YYMM_NCRQ(param)) {
				list = new ArrayList<Map<String, Object>>();
				TCNT = "0";
			} else {
				if (BF_YYMM.equals(YYMM)) {
					list = service.ADM_PRS_S1000A(param);
					Map<String, Object> info = service.ADM_PRS_S1000A_1(param);
					TCNT = String.valueOf(info.get("TCNT"));
				} else {
					list = service.ADM_PRS_S1001A(param);
					Map<String, Object> info = service.ADM_PRS_S1001A_1(param);
					TCNT = String.valueOf(info.get("TCNT"));
				}
			}
			ajax.put("LIST", list);
			ajax.put("TCNT", TCNT);

			ajaxResponse(request, response, ajax);// 정상

		} catch (Exception e) {
			ajaxResponse(request, response, e);// 오류
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 처리현황 조회
	 * 
	 */
	@RequestMapping(value = "/ADM_PRS_V1000A/")
	public void ADM_PRS_V1000A(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		try {
			String SCDL_SEQ = getParam(request, "SCDL_SEQ", 30);
			String YYMM = getParam(request, "YYMM", 4);

			Map<String, String> param = new HashMap<String, String>();
			param.put("SCDL_SEQ", SCDL_SEQ);
			param.put("YYMM", YYMM);
			
			Map<String, Object> info = service.ADM_PRS_V1000A(param);
			decrypt(info, "NCRQ_SCRQ");//스크래핑 요청
			decrypt(info, "NCRQ_SCRS");//스크래핑 수신응답			
			decrypt(info, "SCDL_OSTR");//NICE요청 문자열 원문			
			decrypt(info, "SCDL_ABRQ");//AIB 요청 XML	
			decrypt(info, "SCDL_ABRS");//AIB 응답 XML			
			decrypt(info, "SCDL_RTRQ");//정보등록 요청			
			decrypt(info, "SCDL_RTRS");//정보등록 수신응답			
			decrypt(info, "SCDL_AFRQ");//원본저장 요청			
			decrypt(info, "SCDL_AFRS");//원본저장 수신응답			
			decrypt(info, "SCDL_LTRQ");//로그등록 요청
			decrypt(info, "SCDL_LTRS");//로그등록 수신응답

			Map<String, Object> ajax = new HashMap<String, Object>();
			ajax.put("INFO", info);

			ajaxResponse(request, response, ajax);// 정상

		} catch (Exception e) {
			ajaxResponse(request, response, e);// 오류
			log.error("처리현황 조회 실패", e);
		}
	}
	
	/**
	 * 본문 복호화
	 * @param info
	 * @param name
	 */
	private void decrypt(Map<String, Object> info, String name) {	
		if(info.get(name) == null){	
			return;
		}
		
		String encrypted = String.valueOf(info.get(name));		
		String plain = null;
		
		try {
			plain =  AESCipher.decode(encrypted, AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
			
			if(plain == null || plain.isEmpty()){
				log.error(String.format("Decryption of %s is failed. The result is NULL. ", name));
				encrypted = "본문 복호화 실패\n복호화 결과가 없음.";
			}
			
		} catch (Exception e) {
			log.error(String.format("Decryption of %s is failed. ", name), e);
			plain = "본문 복호화 실패\n" + e.getMessage();
		}
		info.put(name, plain);
	}

}
