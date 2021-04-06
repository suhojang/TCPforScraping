package com.kwic.web.init;

import java.util.Map;

/**
 * KAIS가 제공하는 서비스 정의 인터페이스
 * @author ykkim
 *
 */
public interface InitService {
	
	/**
	 * 서비스명 설정
	 * @param serviceName
	 */
	public void setServiceName(String serviceName);
	
	/**
	 * 서비스명 반환
	 * @return
	 */
	public String getServiceName();
	
	/**
	 * 초기화 파라미터로 서비스 초기화
	 * com.kwic.web.init.ContextInitialListener.contextInitialized()에서 init-service.xml를 읽어 해석하여 값을 찾는다.
	 * @param serviceParams
	 * @throws Exception
	 */
	public void init(Map<String,Object> serviceParams) throws Exception;
	
	/**
	 * 서비스의 스레드 실행
	 * @throws Exception
	 */
	public void service() throws Exception;
	
	/**
	 * 서비스별 초기화 설정값 반환
	 * @return
	 */
	public Map<String,Object> getServiceParams();
	
	/**
	 * 서비스 실행 - 실제 서비스에서 구현한다.
	 * @throws Exception
	 */
	public void execute() throws Exception;
	
	/**
	 * 서비스 종료 - 실제 서비스에서 구현한다.
	 * @throws Exception
	 */
	public void terminate() throws Exception;
}
