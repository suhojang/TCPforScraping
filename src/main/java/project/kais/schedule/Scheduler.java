package project.kais.schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.context.ContextLoader;

import project.kais.schedule.task.Task;

import com.kwic.util.StringUtil;
import com.kwic.xml.parser.JXParser;

import egovframework.rte.fdl.property.EgovPropertyService;

/**
 * 스케줄러
 * @author ykkim
 *
 */
public class Scheduler {
	
	private static Logger log = LoggerFactory.getLogger(Scheduler.class);

	private static Scheduler instance;
	
	/**
	 * 애플리케이션 속성정보 읽어오기
	 */
	private static EgovPropertyService properties;
	
	/**
	 * schedules.xml 해석을 위한 parser
	 */
	private static JXParser scheduleParser;
	
	/**
	 * 스케줄 정보 XML 파일이 있는 디렉토리 경로
	 * context-common.xml 참고
	 */
	private static String schedulePath;
	
	/**
	 * 작업정보 저장소
	 * key로 식별한다.
	 */
	private static Map<String, TaskInfo> contextMap = new HashMap<String, TaskInfo>();
	
	/**
	 * 스케줄 정보 로딩 및 실행 여부
	 */
	private static boolean startup = false;

	/**
	 * schedule-base.xml의 내용
	 */
	private String baseScheduleText;

	/**
	 * 기본 생성자
	 */
	private Scheduler() {
	}

	/**
	 * 팩토리 메서드
	 * @return
	 */
	public static Scheduler getInstance() {
		synchronized (Scheduler.class) {
			if (instance == null) {
				instance = new Scheduler();
			}
			return instance;
		}
	}

	/**
	 * 스케줄 정보 파일 경로 지정
	 * context-commons.xml에 정의한 프러퍼티 schedulePath로 인해 스프링이 실행한다.
	 * @param schedulePath
	 * @throws Exception
	 */
	public void setSchedulePath(final String schedulePath) throws Exception {
		new Thread() {
			@Override
			public void run() {
				try {
					
					/*
					 * context-properties.xml에 정의된 bean 호출
					 * 참고 :  egovframework.rte.fdl.property.impl.EgovPropertyServiceImpl
					 * propertiesService가 아직 로딩되지 않았다면 1초기 대기한다.
					 */
					while (properties == null) {
						try {
							properties = (EgovPropertyService) ContextLoader.getCurrentWebApplicationContext().getBean("propertiesService");
						} catch (Exception e) {
							//bean의 생성순서상 propertiesService가 아직 생성되지 않았다.
						}
						
						try {
							Thread.sleep(1 * 1000);
						} catch (Exception ex) {
						}
					}//while
					
					if (schedulePath.startsWith("/WEB-INF")) {
						Scheduler.schedulePath = getRealPath(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath(schedulePath)) + File.separator;
					} else {
						Scheduler.schedulePath = getRealPath(schedulePath) + File.separator;
					}

					/*
					 * context-properties.xml에 정의된  속성값
					 */
					if (properties.getBoolean("isMaster")){
						startupAllApplications();
					}
				} catch (Exception e) {
					log.error("Could not run scheduler", e);
				}
			}
		}.start();
	}

	public boolean isSchedulerStartUp() {
		return startup;
	}

	public String getSchedulepath() {
		return schedulePath;
	}

	public ApplicationContext getContext(String key) {
		return null;
	}

	/**
	 * 전체 스케줄을 실행한다.
	 * @throws Exception
	 */
	public void startupAllApplications() throws Exception {
		loadAllXmlApplications();
	}

	/**
	 * 전체 스케줄 종료
	 * @throws Exception
	 */
	public void shutdownAllApplication() throws Exception {
		if (!startup){
			return;
		}
		String[] keys = getCurrentKeys();
		for (int i = 0; i < keys.length; i++) {
			shutdown(keys[i], true);
		}
		startup = false;
	}
	
	/**
	 * 운영체제가 윈도우, 리눅스/유닉스인지 여부에 따라 파일구분자를 바꿔준다.
	 * @param path
	 * @return
	 */
	static private String getRealPath(String path){
		if(File.separator.equals("\\")){
			return path.replace("/", "\\");
		}else{
			return path.replace("\\", "/");
		}
	}

	/**
	 * schedules.xml에서 스케줄 정의 정보를 가져온다.
	 * @throws Exception
	 */
	private void loadAllXmlApplications() throws Exception {
		startup = true;
		File file = new File(schedulePath, "schedules.xml");
		scheduleParser = new JXParser(file);
		
		//파싱해서 가져온 스케줄 목록을 담을 리스트
		Map<String, Map<String, String>> scheduleList = new HashMap<String, Map<String, String>>();

		/**
		 * schedules.xml을 읽어서 스케줄 목록을  만들어 scheduleList에 담는다.
		 */
		Element[] nodes = scheduleParser.getElements("//entry");
		String key = null;
		String field = null;
		String entry = null;
		for (int i = 0; i < nodes.length; i++) {
			entry = scheduleParser.getAttribute(nodes[i], "key");
			key   = entry.substring(0, entry.indexOf("_"));
			field = entry.substring(entry.indexOf("_") + 1);

			if (scheduleList.get(key) == null){
				scheduleList.put(key, new HashMap<String, String>());
			}

			scheduleList.get(key).put(field, scheduleParser.getValue(nodes[i]));
		}
				
		/*
		 * 개별 스케줄을 실행한다.
		 */
		Iterator<String> iter = scheduleList.keySet().iterator();
		while (iter.hasNext()) {
			key = iter.next();
			startup(scheduleList.get(key));
		}	
	}

	/**
	 * schedule-base.xml 읽어수 문자열로 반환한다.
	 * @return
	 * @throws Exception
	 */
	private String getBaseText() throws Exception {
		if (baseScheduleText != null){
			return baseScheduleText;
		}
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		String line = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(schedulePath, "schedule-base.xml"))));
			while ((line = reader.readLine()) != null){
				buffer.append(line).append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			log.error(String.format("Reading schedule-base.xml file is failed"), e);
			throw e;
		} finally {
			try {
				if (reader != null){
					reader.close();
				}
			} catch (Exception e) {
				log.error(String.format("Reading schedule-base.xml file is failed. reader is not closed."), e);
			}
		}
		return buffer.toString();
	}

	/**
	 * 개별 스케줄을 동기식으로  실행한다.
	 * 각 스케줄별로 xml 파일을 임시로 생성한다. 파열명 형식은 context-schedule-스케줄명.xml 이다. (ex. context-schedule-manageips.xml)
	 * @param scheduleInfoMap 스케줄 정보
	 * @throws Exception
	 */
	public synchronized void startup(Map<String, String> scheduleInfoMap) throws Exception {
		
		/*
		 * 1. 개별 스케줄 정보 xml을 만든다.
		 * 	<bean id="task_manageips" class="project.kais.schedule.task.Task_manageips"/>
		 *  <util:properties id="schedules" location="D:\eGovFrame\workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp1\wtpwebapps\KAIS\WEB-INF\config\kais\schedule/schedules.xml" />
		 *  <task:scheduler id="manageips" pool-size="10"/>
		 *  <task:scheduled-tasks scheduler="manageips">
		 *  <task:scheduled ref="task_manageips" method="executeTask" cron="#{schedules.manageips_cron}" />
		 *  </task:scheduled-tasks>
		 */
		String key = scheduleInfoMap.get("key");
		FileWriter writer = null;
		File contextScheduleFile = null;
		try {
			contextScheduleFile = new File(schedulePath, "context-schedule-" + key + ".xml");
			writer = new FileWriter(contextScheduleFile);
			writer.write(StringUtil.replace(StringUtil.replace(getBaseText(), "$path$", schedulePath), "$key$", key));
			writer.flush();
		} catch (Exception e) {
			//스케줄 실행을 위한 "context-schedule-" + key + ".xml" 쓰기 실패
			log.error(String.format("Making schedule file is failed. file=%s", contextScheduleFile.getAbsolutePath()), e);
			throw e;
		} finally {
			try {
				if (writer != null){
					writer.close();
				}
			} catch (Exception e) {
				log.error(String.format("Closing file writer is failed. file=%s", contextScheduleFile.getAbsolutePath()), e);
			}
		}
		// }

		/*
		 * 실행중인 스케줄은 종료한다.
		 */
		if (contextMap.get(key) != null              && 
			contextMap.get(key).getContext() != null && 
			(contextMap.get(key).getContext().isRunning() || contextMap.get(key).getContext().isActive())) {
			shutdown(key);
		}

		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(contextScheduleFile.getCanonicalPath());
		String[] beanNames = context.getBeanFactory().getBeanDefinitionNames();		
		
		Task task = null;
		for (int i = 0; i < beanNames.length; i++) {
			if (context.getBean(beanNames[i]) instanceof Task) {
				task = (Task) context.getBean(beanNames[i]);
				task.setKey(key);
				task.setContextPath(schedulePath);
				loadProperties(task, scheduleInfoMap);
			}
		}

		TaskInfo taskInfo = new TaskInfo();
		taskInfo.setKey(key);
		taskInfo.setContext(context);
		// 이전에 스케쥴이 구동된 적이 있다면 이전의 구동정보를 적용
		if (contextMap.get(key) != null && contextMap.get(key).getTask() != null && contextMap.get(key).isRunning()) {
			taskInfo.setTask(contextMap.get(key).getTask());
			taskInfo.setRunning(contextMap.get(key).isRunning());

			contextMap.get(key).getTask().setCron(task.getCron());
			contextMap.get(key).getTask().setName(task.getName());
			contextMap.get(key).getTask().setBizName(task.getBizName());

		} else if (contextMap.get(key) != null && contextMap.get(key).getTask() != null) {

			task.setLastStatus(contextMap.get(key).getTask().getLastStatus());
			task.setLastStartDate(contextMap.get(key).getTask().getLastStartDate());
			task.setLastEndDate(contextMap.get(key).getTask().getLastEndDate());
			task.setLastMessage(contextMap.get(key).getTask().getLastMessage());
			task.setSuccessCount(contextMap.get(key).getTask().getSuccessCount());
			task.setErrorCount(contextMap.get(key).getTask().getErrorCount());

			taskInfo.setTask(task);
			taskInfo.setRunning(false);
		} else {
			taskInfo.setTask(task);
			taskInfo.setRunning(false);
		}

		contextMap.put(key, taskInfo);

		log.debug(String.format("Scheduler %s started. %s", key, taskInfo));
	}

	/**
	 * task를 map 으로 된 정보로 설정한다.
	 * @param task
	 * @param scheduleInfoMap
	 */
	private void loadProperties(Task task, Map<String, String> scheduleInfoMap) {
		try {
			task.setParams(scheduleInfoMap.get("params"));
			task.setName(scheduleInfoMap.get("name"));
			task.setParamMap(parseParams(task.getParams()));
			task.setCron(scheduleInfoMap.get("cron"));
			task.setIsLastDay("Y".equals(scheduleInfoMap.get("lastday")) ? true : false);
			task.setSingleJob("1".equals(scheduleInfoMap.get("type")) ? true : false);
		} catch (Exception e) {
			log.error(String.format("Task mapping error. %s", scheduleInfoMap), e);
		}
	}

	/**
	 * 스케줄 중지
	 * @param key
	 * @throws Exception
	 */
	public void shutdown(String key) throws Exception {
		shutdown(key, false);
	}

	/**
	 * 구동중지, remove=true라면 영구삭제
	 * @param key
	 * @param remove
	 * @throws Exception
	 */
	public synchronized void shutdown(String key, boolean remove) throws Exception {
		if (contextMap.get(key) == null || contextMap.get(key).getContext() == null){
			return;
		}
		if (contextMap.get(key).getContext().isRunning()){
			contextMap.get(key).getContext().stop();
		}
		if (contextMap.get(key).getContext().isActive()){
			contextMap.get(key).getContext().close();
		}

		contextMap.get(key).shutdown();

		if (remove && contextMap.get(key).isRunning()){
			contextMap.get(key).getTask().setRemoved(true);
		}
		else if (remove){
			contextMap.remove(key);
		}

		log.debug("Scheduler [" + key + "] terminated.");
	}

	/**
	 * 작업정보 저장소에서 삭제
	 * @param key
	 */
	public synchronized void remove(String key) {
		contextMap.remove(key);
	}

	public void setTask(String key, Task task) {
		if (contextMap.get(key) == null){
			return;
		}
		if (contextMap.get(key).getTask() != null) {
			task.setLastStatus(contextMap.get(key).getTask().getLastStatus());
			task.setLastStartDate(contextMap.get(key).getTask().getLastStartDate());
			task.setLastEndDate(contextMap.get(key).getTask().getLastEndDate());
			task.setLastMessage(contextMap.get(key).getTask().getLastMessage());
			task.setSuccessCount(contextMap.get(key).getTask().getSuccessCount());
			task.setErrorCount(contextMap.get(key).getTask().getErrorCount());
		}

		contextMap.get(key).setTask(task);
	}

	public Task getTask(String key) {
		if (contextMap.get(key) == null){
			return null;
		}
		return contextMap.get(key).getTask();
	}

	public String[] getCurrentKeys() {
		Object[] arr = contextMap.keySet().toArray();
		String[] keys = new String[arr.length];

		for (int i = 0; i < arr.length; i++) {
			keys[i] = String.valueOf(arr[i]);
		}
		Arrays.sort(keys);
		return keys;
	}

	public void setRunning(String key, boolean isRunning) {
		if (contextMap.get(key) == null){
			return;
		}
		contextMap.get(key).setRunning(isRunning);
	}

	public boolean isRunning(String key) {
		if (contextMap.get(key) == null){
			return false;
		}
		return contextMap.get(key).isRunning();
	}

	public boolean isShutdown(String key) {
		if (contextMap.get(key) == null){
			return false;
		}
		return contextMap.get(key).isShutdown();
	}

	public Date getLastStartDate(String key) {
		if (contextMap.get(key) == null || contextMap.get(key).getTask() == null){
			return null;
		}
		return contextMap.get(key).getTask().getLastStartDate();
	}

	public Date getLastEndDate(String key) {
		if (contextMap.get(key) == null || contextMap.get(key).getTask() == null){
			return null;
		}
		return contextMap.get(key).getTask().getLastEndDate();
	}

	public int getLastStatus(String key) {
		if (contextMap.get(key) == null || contextMap.get(key).getTask() == null){
			return Task.STATUS_NOTYET;
		}
		return contextMap.get(key).getTask().getLastStatus();
	}

	public String getLastMessage(String key) {
		if (contextMap.get(key) == null || contextMap.get(key).getTask() == null){
			return null;
		}
		return contextMap.get(key).getTask().getLastMessage();
	}

	public void setLastStatus(String key, int status) {
		if (contextMap.get(key) == null || contextMap.get(key).getTask() == null){
			return;
		}
		contextMap.get(key).getTask().setLastStatus(status);
	}

	public void setLastMessage(String key, String message) {
		if (contextMap.get(key) == null || contextMap.get(key).getTask() == null){
			return;
		}
		contextMap.get(key).getTask().setLastMessage(message);
	}

	/**
	 * 쉼표로 이름=값으로 구분된 문자열을 분해하여 map 으로 만들어 반환한다.
	 * @param params
	 * @return
	 */
	public static Map<String, String> parseParams(String params) {
		Map<String, String> paramMap = new HashMap<String, String>();
		if (params == null || "".equals(params.trim()) || params.indexOf("=") < 0){
			return paramMap;
		}
		String[] arr = StringUtil.split(params, ",");
		String[] vals = null;
		for (int i = 0; i < arr.length; i++) {
			vals = StringUtil.split(arr[i], "=");
			if (vals.length == 2){
				paramMap.put(vals[0].toUpperCase().trim(), StringUtil.replace(vals[1].toUpperCase(), "$c;", ","));
			}else if (vals.length == 1){
				paramMap.put(vals[0].toUpperCase().trim(), "");
			}
		}
		return paramMap;
	}
}
