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

import com.kwic.util.StringUtil;
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;
import project.kais.service.ADM_PCD_Service;
import project.kais.service.ADM_PRS_Service;
import project.kais.service.ReprocessService;

/**
 * 전송실패 재처리
 * @author ykkim
 *
 */
@Controller
public class ReprocessController extends Controllers {
	private static Logger log = LoggerFactory.getLogger(ReprocessController.class);

	@Resource(name = "propertiesService")
	private EgovPropertyService properties;

	/** service */
	@Resource(name = "ADM_PCD_Service")
	private ADM_PCD_Service pcdService;
	
	/** service */
	@Resource(name = "ADM_PRS_Service")
	private ADM_PRS_Service scrapingService;
	
	@Resource(name="ReprocessService")
	private ReprocessService reprocessSeervice;
	
	@RequestMapping(value = "/ADM_REP_010000/") //@RequestMapping(value = "/reprocess/list/")
	public String ADM_PRS_010000(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		List<Map<String, Object>> cd0002 = pcdService.ADM_PCD_S1200A("0002");
		List<Map<String, Object>> cd0003 = pcdService.ADM_PCD_S1200A("0003");
		model.addAttribute("CD0002", cd0002);
		model.addAttribute("CD0003", cd0003);
		return "/ADM/reprocess/list";
	}
	
	/**
	 * 처리현황 목록 조회
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value = "/ADM_REP_S1000A/")
	public void ADM_PRS_S1000A(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		try {
			String YYMM         = getParam(request, "YYMM", 4);
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

			if (PAGE_NO == null || "".equals(PAGE_NO)) {
				PAGE_NO = "1";
			}

			if (ROWPERPAGE == null || "".equals(ROWPERPAGE)) {
				ROWPERPAGE = properties.getString("pageSize");
			}

			String STNO = String.valueOf((Integer.parseInt(PAGE_NO) - 1) * Integer.parseInt(ROWPERPAGE) + 1);
			String EDNO = String.valueOf(Integer.parseInt(PAGE_NO) * Integer.parseInt(ROWPERPAGE));

			Map<String, String> param = new HashMap<String, String>();
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
			int tcnt = 0;
			Map<String, Object> ajax = new HashMap<String, Object>();
			if (!scrapingService.CHECK_YYMM_NCRQ(param)) {
				list = new ArrayList<Map<String, Object>>();
				tcnt = 0;
			} else {
				list = (List<Map<String, Object>>) reprocessSeervice.selectCandidateList(param);
				tcnt = reprocessSeervice.selectCandidateCount(param);
			}
			ajax.put("LIST", list);
			ajax.put("TCNT", tcnt);

			ajaxResponse(request, response, ajax);// 정상

		} catch (Exception e) {
			ajaxResponse(request, response, e);// 오류
			logger.error(e.getMessage(), e);
		}
	}
}
