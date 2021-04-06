package com.kwic.web.init;

import java.util.Map;

public interface Handler {
	
	/**
	 * 서비스 설정정보 반환
	 * @return
	 */
	public Map<String, Object> getServiceParam();

	/**
	 * 서비스 개별 정보 설정
	 * @param name 설정정보명
	 * @param obj 설정정보 값
	 * @throws Exception
	 */
	public void put(String name, Object obj) throws Exception;

	/**
	 * 연결 성립
	 * @throws Exception
	 */
	public void handle() throws Exception;

	/**
	 * 결과코드 저장
	 * @param resultCode
	 */
	public void setResultCode(String resultCode);

	/**
	 * 결과코드 반환
	 * @return
	 */
	public String getResultCode();
}
