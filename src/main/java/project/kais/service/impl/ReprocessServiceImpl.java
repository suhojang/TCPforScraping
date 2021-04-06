package project.kais.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import project.kais.service.ReprocessService;

@Service("ReprocessService")
public class ReprocessServiceImpl implements ReprocessService {
	
	@Resource(name = "ReprocessDao")
	private ReprocessDao reporcessDao;

	/**
	 * 재처리 대상 목록 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Map<String, Object>> selectCandidateList(Map<String, String> param) throws Exception {
		return reporcessDao.selectCandidateList(param);
	}

	/**
	 * 재처리 대상 상세 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> selectCandidateDetail(Map<String, String> param) throws Exception {
		return reporcessDao.selectCandidateDetail(param);
	}

	/**
	 * 재처리 대상 건수 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public int selectCandidateCount(Map<String, String> param) throws Exception {
		return reporcessDao.selectCandidateCount(param);
	}

	

}
