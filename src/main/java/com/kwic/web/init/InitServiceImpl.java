package com.kwic.web.init;

import java.util.Map;

import org.springframework.web.context.ContextLoader;

import project.kais.logger.Logger;
import project.kais.logger.LoggerFactory;

/**
 * exceute(), terminate()는 실제 서비스에서 구현됨.
 * @author ykkim
 *
 */
public abstract class InitServiceImpl extends Thread implements InitService {

	private static Logger log = LoggerFactory.getLogger(InitServiceImpl.class);	

	/**
	 * 서비스별 파라미터 
	 */
	private Map<String, Object> serviceParams;
	
	/**
	 * 서비스명
	 */
	private String serviceName;
	
	/**
	 * 서비스명 설정
	 * @param serviceName
	 */
	@Override
	public void setServiceName(String serviceName){
		this.serviceName	= serviceName;
	}	

	/**
	 * 서비스명 반환
	 * @return
	 */
	@Override
	public String getServiceName(){
		return serviceName;
	}
	
	/**
	 * 초기화 파라미터로 서비스 초기화
	 * com.kwic.web.init.ContextInitialListener.contextInitialized()에서 init-service.xml를 읽어 해석하여 값을 찾는다.
	 * @param serviceParams
	 * @throws Exception
	 */
	@Override
	public void init(Map<String,Object> serviceParams) throws Exception{
		this.serviceParams	= serviceParams;
	}	

	/**
	 * 서비스의 스레드 실행
	 * @throws Exception
	 */
	@Override
	public void service() throws Exception {
		this.start();
	}	

	/**
	 * 서비스별 초기화 설정값 반환
	 * @return
	 */
	@Override
	public Map<String,Object> getServiceParams(){
		return serviceParams;
	}
	
	/**
	 * 특정 파라미터의 값 가져오기
	 * @param paramName
	 * @return
	 */
	public Object getServiceParam(String paramName){
		return serviceParams.get(paramName);
	}
	
	/**
	 * init-service.xml에 정의된 각 서비스의  특정 파라미터의 값을 문자열로 가져오기
	 * @param name
	 * @return
	 */
	public String getServiceParamString(String paramName){
		return String.valueOf(serviceParams.get(paramName));
	}
	
	/**
	 * 특정 파라미터의 값을 정수로 가져오기
	 * @param name
	 * @return
	 */
	public int getServiceParamInt(String paramName){
		return Integer.parseInt(String.valueOf(serviceParams.get(paramName)));
	}
	
	/**
	 * 특정 파라미터의 값을 long형 정수로 가져오기
	 * @param name
	 * @return
	 */
	public long getServiceParamLong(String name){
		return Long.parseLong(String.valueOf(serviceParams.get(name)));
	}
	
	/**
	 * 특정 파라미터의 값을 float 실수로 가져오기
	 * @param name
	 * @return
	 */
	public float getServiceParamFloat(String paramName){
		return Float.parseFloat(String.valueOf(serviceParams.get(paramName)));
	}
	
	/**
	 * 특정 파라미터의 값을 double 실수로 가져오기
	 * @param name
	 * @return
	 */
	public double getServiceParamDouble(String paramName){
		return Double.parseDouble(String.valueOf(serviceParams.get(paramName)));
	}
	
	/**
	 * 스프링 컨테스트의 저장소에서 bean을 검색하여 찾아온다.
	 * @param name
	 * @return
	 */
	public static Object getBean(String paramName){
		return ContextLoader.getCurrentWebApplicationContext().getBean(paramName);
	}
	
	/**
	 * 서비스 실행
	 */
	@Override
	public void run(){
		try{
			//서버 시작 후 1분 여유시간
//			Thread.sleep(60*1000);
			execute();
			log.debug(String.format("%s Service execution is started.", serviceName));
		}catch(Exception e){
			//서비스 실행 실패
			log.error(String.format("%s Service execution is failed.", serviceName), e);
		}
	}
	
	@Override
	public String toString() {
		return "InitServiceImpl [params=" + serviceParams + ", serviceName=" + serviceName + "]";
	}
}
