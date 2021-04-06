package project.kais.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kwic.service.DaoSupport;

@Repository("ReprocessDao")
public class ReprocessDao extends DaoSupport {

	
	/**
	 * 해당월의 NICE 스크래핑 요청 테이블 생성여부 확인
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectCandidateList(Map<String, String> param) {
		return list("Reprocess.selectCandidateList", param);
	}

	/**
	 * 처리현황 목록 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectCandidateDetail(Map<String, String> param) {
		return (Map<String, Object>) select("Reprocess.selectCandidateDetail", param);
	}

	/**
	 * 처리현황 목록 건수 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int selectCandidateCount(Map<String, String> param) {
		return Integer.valueOf(String.valueOf(select("Reprocess.selectCandidateCount", param))) ;
	}


}
