package project.kais.init.server.parser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kwic.exception.DefinedException;
import com.kwic.security.aes.AESCipher;
import com.kwic.util.ByteUtil;
import com.kwic.util.StringUtil;

import project.kais.init.KaisState;
import project.kais.service.ScrapingService;

public class Parser_1S101 extends Parser {

	private static Logger log = LoggerFactory.getLogger(Parser_1S101.class); 
	
	/** service */
	private ScrapingService service;

	/**
	 * 본문 파싱 및 DB 저장
	 */
	public void execute() throws Exception {
		service = (ScrapingService) getBean("ScrapingService");

		String H_NCE_RQTM = String.valueOf(super.getReqMap().get("H_NCE_RQTM"));// NICE 전문 전송시각yyyyMMddHHmmss
		
		// INF_LST - 요청정보목록
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) super.getReqMap().get("INF_LST");

		String bzCd = null;// 상품코드
		byte[] gArea = null;// 요청내역

		Map<String, String> param = new HashMap<String, String>();
		param.put("NCRQ_SEQ",   super.getSerial()); //NICE 스크래핑 요청 key
		param.put("SCDL_MBRNO", String.valueOf(super.getReqMap().get("CST_MBR_NO"))); //회원사 고객번호
		param.put("SCDL_STS",   KaisState.AIB_TGT_A0); //처리상태 - AIB요청 대상
		param.put("YYMM",       H_NCE_RQTM.substring(2, 6));
		param.put("SCDL_CNT",   "0"); //스크래핑 처리횟수

		String[] kwBzCd = null;
		Map<String, Object> parsingInfoMap = null;
		String ostr = null;

		for (int i = 0; i < list.size(); i++) {
			try {
				bzCd = (String) list.get(i).get("G_BZCD");
				if ("".equals(bzCd.trim())) {
					list.get(i).put("RTCD", "E831"); //반복부 상품코드 미입력
					continue;
				}
				gArea = (byte[]) list.get(i).get("G_AREA");
				param.put("SCDL_SEQ",    super.getSerial() + StringUtil.addChar(i + 1, 3)); //AIB 거래키
				param.put("SCDL_G_BZCD", bzCd); //NICE 상품코드

				ostr = AESCipher.encode(new String(gArea, super.getEncoding()), 
						               AESCipher.DEFAULT_KEY, 
						               AESCipher.TYPE_256, 
						               "UTF-8", 
						               AESCipher.MODE_ECB_NOPADDING);
				param.put("SCDL_OSTR", ostr); //NICE요청 문자열 원문

				try {
					parsingInfoMap = parse(super.getBzcd() + "-" + bzCd, gArea, super.getEncoding(), Parser.REQUEST);
				} catch (Exception e) {
					log.error("", e);
					list.get(i).put("RTCD", "E833"); //반복부 상품정보 해석 오류
					continue;
				}

				kwBzCd = getKwBzCd(super.getBzcd() + "-" + bzCd, parsingInfoMap);
				if (kwBzCd == null) {
					list.get(i).put("RTCD", "E832"); //정의되지 않은 상품코드
					continue;
				}
				param.put("SCDL_BZCD", kwBzCd[0]); //기웅 전문코드
				param.put("SCDL_JBCD", kwBzCd[1]); //기웅 업무코드

				//////////////////////////////////////////////////////////////////////////
				/**
				 * 예외처리 상품코드(22) 등초본발급 진위여부등록 분기 처리 [ 온라인 : 0019-6 / 오프라인 :
				 * 0025-9 ]
				 */
				// if("22".equals(bzCd)){
				// //1:온라인 , 2:오프라인
				// String ISSUE_TYPE = String.valueOf(info.get("ISSUE_TYPE"));
				// if("2".equals(ISSUE_TYPE)){
				// param.put("SCDL_BZCD" , "0025");
				// param.put("SCDL_JBCD" , "9");
				// }
				// }
				//////////////////////////////////////////////////////////////////////////

				list.get(i).putAll(parsingInfoMap);

				param.put("SCDL_SCAF_ID", String.valueOf(parsingInfoMap.get("SCAF_ID"))); //NICE SCAF_ID
				try {
					service.INSERT_REQUEST_SCDL(param);
					log.debug(String.format("[DB-INSERT_REQUEST_SCDL] Saving details is success. SCDL_SEQ=%s, NCRQ_SEQ=%s, SCDL_MBRNO=%s, SCDL_G_BZCD=%s, SCDL_BZCD=%s, SCDL_JBCD=%s, SCDL_SCAF_ID=%s, SCDL_STS=%s, SCDL_CNT=%s", 
							param.get("SCDL_SEQ"), param.get("NCRQ_SEQ"), param.get("SCDL_MBRNO"), param.get("SCDL_G_BZCD"), param.get("SCDL_BZCD"), param.get("SCDL_JBCD"), param.get("SCDL_SCAF_ID"), param.get("SCDL_STS"), param.get("SCDL_CNT")  ));
				} catch (Exception e) {
					log.error(String.format("Saving details is failed. %s", param), e);
					list.get(i).put("RTCD", "E834"); //반복부 상품정보 저장 오류
					continue;
				}
			} catch (DefinedException e) {
				log.warn("Parsing and insert are failed.", e);
				continue;
			} catch (Exception e) {
				log.error("Because of unknown error, parsing and insert are failed.", e);
				list.get(i).put("RTCD", "E839"); //반복부 상품정보 해석 중 정의되지 않은 오류
			}
		}
	}

	/**
	 * make normal response
	 */
	@SuppressWarnings("unchecked")
	public byte[] makeResponse() throws Exception {
		super.getReqMap().put("H_RTCD",      super.getHandler().getResultCode());
		super.getReqMap().put("H_ORG_TL_NO", super.getSerial());
		super.getReqMap().put("H_ORG_RQTM",  new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()));

		int RQ_CNT = 0;
		if (super.getReqMap().get("INF_LST") != null) { //요청정보목록
			RQ_CNT = ((List<Map<String, Object>>) super.getReqMap().get("INF_LST")).size();
		}
		super.getReqMap().put("RQ_CNT", String.valueOf(RQ_CNT));

		byte[] bytes = make(super.getBzcd(), super.getReqMap(), super.getEncoding());

		Map<String, Object> p = super.getHandler().getServiceParam();
		int lenStIdx = Integer.parseInt(String.valueOf(p.get("length-start-index")));
		int lenSz = Integer.parseInt(String.valueOf(p.get("length-size")));

		byte[] lengthBytes = ByteUtil.addByte(String.valueOf(bytes.length - ("0".equals(super.getSysParam().get("length-contain-self")) ? lenSz : 0)).getBytes(),
				                              ByteUtil.APPEND_CHARACTER_ZERO, lenSz, true);
		// set total length
		System.arraycopy(lengthBytes, 0, bytes, lenStIdx, lenSz);

		return bytes;
	}

	/**
	 * make error response
	 * H_RTCD 응답코드
	 * H_ORG_TL_NO 기관전문 관리번호
	 * H_ORG_RQTM 기관전문 전송시각
	 * INF_LST 요청정보 목록
	 * RQ_CNT 요청건수
	 */
	@SuppressWarnings("unchecked")
	public byte[] makeResponse(Exception e) throws Exception {
		if ("P000".equals(super.getHandler().getResultCode())){
			super.getReqMap().put("H_RTCD", "E899"); //요청전문 해석 중 정의되지 않은 오류
		}
		else{
			super.getReqMap().put("H_RTCD", super.getHandler().getResultCode());
		}

		super.getReqMap().put("H_ORG_TL_NO", super.getSerial());
		super.getReqMap().put("H_ORG_RQTM", new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()));
		int RQ_CNT = 0;
		if (super.getReqMap().get("INF_LST") != null) {//요청정보목록
			RQ_CNT = ((List<Map<String, Object>>) super.getReqMap().get("INF_LST")).size();
		}
		super.getReqMap().put("RQ_CNT", String.valueOf(RQ_CNT));

		byte[] bytes = make(super.getBzcd(), super.getReqMap(), super.getEncoding());

		Map<String, Object> p = super.getHandler().getServiceParam();
		int lenStIdx = Integer.parseInt(String.valueOf(p.get("length-start-index")));
		int lenSz = Integer.parseInt(String.valueOf(p.get("length-size")));
		byte[] lengthBytes = ByteUtil.addByte(String.valueOf(bytes.length - ("0".equals(super.getSysParam().get("length-contain-self")) ? lenSz : 0)).getBytes(),
				                              ByteUtil.APPEND_CHARACTER_ZERO, lenSz, true);

		System.arraycopy(lengthBytes, 0, bytes, lenStIdx, lenSz);

		return bytes;
	}
}
