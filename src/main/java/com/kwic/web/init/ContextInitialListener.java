package com.kwic.web.init;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContextEvent;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import com.kwic.xml.parser.JXParser;

public class ContextInitialListener extends ContextLoaderListener {

	private static Logger log = LoggerFactory.getLogger(ContextInitialListener.class);	

	/**
	 * 서비스 목록
	 * init-service.xml에 정의한 서비스로 초기화한다.
	 */
	List<InitService> taskList = new Vector<InitService>();

	public ContextInitialListener() {
	}

	public ContextInitialListener(WebApplicationContext context) {
		super(context);
	}

	/**
	 * 컨텍스트 초기화 할 때 서비스 초기화
	 * 각 서비스 별로 setServiceName(),  init(), service() 메서드 호출
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		log.debug("--------------- context initial services start -------------");
		String configPath = event.getServletContext().getRealPath("/WEB-INF/config/init/init-service.xml");
		if (!new File(configPath).exists()) {
			log.error(String.format("Cannot found a config file=%s", configPath));
			return;
		}

		JXParser parser = null;
		InitService service = null;
		Class<?> serviceClass = null;
		Map<String, Object> params = null;
		try {
			parser = new JXParser(new File(configPath));

			Element[] services = parser.getElements("//service");
			Element[] paramElements = null;

			/*
			 * 1. 서비스명 지정          setServiceName()
			 * 2. 초기화 파라미터 설정  init()
			 * 3. 서비스 사작             service()
			 */
			for (int i = 0; i < services.length; i++) {
				serviceClass = Class.forName(parser.getValue(parser.getElement(services[i], "service-class")));
				service = (InitService) serviceClass.newInstance();
				taskList.add(service);
				service.setServiceName(parser.getValue(parser.getElement(services[i], "name")));

				paramElements = parser.getElements(parser.getElement(services[i], "init-params"), "param");
				params = new HashMap<String, Object>();
				for (int j = 0; j < paramElements.length; j++) {
					log.debug("\tparam : " + parser.getAttribute(paramElements[j], "name") + "=" + parser.getValue(paramElements[j]));
					params.put(parser.getAttribute(paramElements[j], "name"), parser.getValue(paramElements[j]));
				}
				service.init(params);
				service.service();
				log.debug(String.format("%s Service is called and started. parameters=%s", service.getServiceName(), params));
			}
		} catch (Exception e) {
			log.error("Service initialiation is failed.", e);
		}
		log.debug("--------------- context initial services end -------------");
	}

	/**
	 * 컨텍스트 종료할 때 서비스 종료
	 * 각 서비스별로 terminate() 메서드 호출
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		log.debug("--------------- context initial services terminate -------------");
		for (int i = 0; i < taskList.size(); i++) {
			try {
				taskList.get(i).terminate();
				log.debug(taskList.get(i).getServiceName() + " service destroyed.");
			} catch (Exception e) {
				log.error("Service finalization is failed.", e);
			}
		}
		super.contextDestroyed(event);
	}
}
