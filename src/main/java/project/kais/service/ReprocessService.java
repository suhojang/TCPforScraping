package project.kais.service;

import java.util.List;
import java.util.Map;

public interface ReprocessService {
	
	/**
	 * 재처리 대상 목록 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>>selectCandidateList(Map<String, String> param) throws Exception;

	/**
	 * 재처리 대상 상세 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectCandidateDetail(Map<String, String> param) throws Exception;


	/**
	 * 재처리 대상 건수 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int selectCandidateCount(Map<String, String> param) throws Exception;

}
