package project.kais.schedule;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import project.kais.schedule.task.Task;

/**
 * 작업 실행 정보
 * @author ykkim
 *
 */
public class TaskInfo {
	
	/**
	 * 작업 실행환경을 가져오기 위한 컨텍스트
	 */
	private FileSystemXmlApplicationContext context;
	
	/**
	 * 작업 식별자
	 */
	private String key;
	
	/**
	 * 작업 실행여부
	 */
	private boolean isRunning;
	
	/**
	 * 작업
	 */
	private Task task;
	
	/**
	 * 작업 종료여부
	 */
	private boolean shutdown;
	
	public void setContext(FileSystemXmlApplicationContext context){
		this.context	= context;
	}
	public void setKey(String key){
		this.key	= key;
	}
	public void setRunning(boolean isRunning){
		this.isRunning	= isRunning;
	}
	public void setTask(Task task){
		this.task	= task;
	}
	
	public FileSystemXmlApplicationContext getContext(){
		return context;
	}
	public String getKey(){
		return key;
	}
	public boolean isRunning(){
		return isRunning;
	}
	public Task getTask(){
		return task;
	}
	public void shutdown(){
		shutdown	= true;
	}
	public boolean isShutdown(){
		return shutdown;
	}
	
}
