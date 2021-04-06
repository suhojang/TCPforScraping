package project.kais.init.client.except;

import java.util.Map;

import com.kwic.xml.parser.JXParser;

import project.kais.init.server.parser.CommonParser;
import project.kais.init.server.parser.Parser;

/**
 * Info-Box 서버스크래핑 요청전문
 * @author ykkim
 *
 */
public class Except_1S101_22 extends Except {

	@Override
	public Map<String, String> exceptRequest(Map<String, String> aibReqParam, Map<String, Object> request, Map<String, Object> sysParam) throws Exception {
		if (request != null) {
			String ISSUE_TYPE	= String.valueOf(request.get("ISSUE_TYPE"));
			String SSN			= String.valueOf(request.get("SSN"));
			//발급문서 진위여부 온라인일 경우 주민번호 체크하여 생년월일 설정
			if ((ISSUE_TYPE != null && "1".equals(ISSUE_TYPE)) && (SSN != null && SSN.length() >= 6)) {
				aibReqParam.put("ISSUEKEY2", SSN.substring(0,6));	//생년월일
			}
		}
		return aibReqParam;
	}

	@Override
	public Map<String, Object> exceptResponse(Map<String, Object> aibResParam, Map<String, Object> response, Map<String, Object> sysParam) throws Exception {

		/**
		 * 상품코드(22) 주민등록등초본 진위확인 요청부 - 발급구분 [ 1:온라인 , 2:오프라인 ] - 주민등록번호 - 성명 
		 * 위 3가지 항목을 나이스에서 입력한 그대로 기웅->나이스 등록전문에 넣어주기 위한 예외 처리.
		 */
		if (response != null) {
			String NCRQ_BZCD   = String.valueOf(response.get("NCRQ_BZCD"));    //거래구분코드
			String SCDL_G_BZCD = String.valueOf(response.get("SCDL_G_BZCD"));  //NICE 상품코드
			String SCDL_OSTR   = String.valueOf(response.get("SCDL_OSTR"));    //NICE요청 문자열 원문
			String SCDL_ABRS   = String.valueOf(response.get("SCDL_ABRS"));    //AIB응답 원문
			String encoding    = String.valueOf(sysParam.get("peer-encoding")); //

			Map<String, Object> reqInfo = new CommonParser().parse(NCRQ_BZCD + "-" + SCDL_G_BZCD, SCDL_OSTR.getBytes(encoding), encoding, Parser.REQUEST);

			String ISSUE_TYPE = String.valueOf(reqInfo.get("ISSUE_TYPE"));
			String NM         = String.valueOf(reqInfo.get("NM"));
			String SSN        = String.valueOf(reqInfo.get("SSN"));
			String BZ22_14_14 = ""; // 하드코딩 (나이스 요청에 의해서 응답값을 줄 때 6번인 경우 민원사무명에 온라인발급 9번인경우 오프라인발급 )
			
			JXParser jxp	= new JXParser(SCDL_ABRS);
			if (ISSUE_TYPE == null) {
				ISSUE_TYPE = "";
			}
			
			if (NM == null){
				NM = "";
			}
			if (SSN == null) {
				SSN = "";
			}

			// 등초본 업무코드구분(온라인)
			if ("1".equals(ISSUE_TYPE)) {
				ISSUE_TYPE = "6";
				
				String ISSUEKEY			= jxp.getAttribute(jxp.getElement(jxp.getRootElement(), "ISSUEKEY"), "VALUE");	//성명
				String MINWONSAMUNM		= jxp.getAttribute(jxp.getElement(jxp.getRootElement(), "MINWONSAMUNM"), "VALUE");	//민원사무명
				
				BZ22_14_14	= MINWONSAMUNM;
				NM			= ISSUEKEY;
				SSN			= "";
			}
			
			// 등초본 업무코드구분(오프라인)
			if ("2".equals(ISSUE_TYPE)) {
				ISSUE_TYPE = "9";

				String ISSUEKEY			= jxp.getAttribute(jxp.getElement(jxp.getRootElement(), "ISSUEKEY"), "VALUE");	//생년월일

				BZ22_14_14		= "주민등록표등본(초본)교부";
				NM				= "";
				SSN				= ISSUEKEY;
			}

			aibResParam.put("BZ22_14_08", ISSUE_TYPE);
			aibResParam.put("BZ22_14_11", NM);
			aibResParam.put("BZ22_14_12", SSN);
			aibResParam.put("BZ22_14_14", BZ22_14_14);
		}

		return aibResParam;
	}

}
