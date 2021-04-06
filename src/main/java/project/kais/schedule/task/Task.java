package project.kais.schedule.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;

import project.kais.schedule.Scheduler;

public abstract class Task {

	public static final int STATUS_NORMAL = 1;
	public static final int STATUS_ERROR = 0;
	public static final int STATUS_NOTYET = -1;

	protected static SimpleDateFormat logSf = new SimpleDateFormat("yyyyMMddHHmmss");

	private static Logger log = LoggerFactory.getLogger(Task.class);

	protected String contextPath;
	protected String bizName;
	protected String key;
	protected String name;
	protected String cron;
	protected long successCount;
	protected long errorCount;

	protected Date lastStartDate;
	protected Date lastEndDate;
	protected int lastStatus = STATUS_NORMAL;
	protected String lastMessage;

	protected String params;
	protected Map<String, String> paramMap;
	protected boolean isRemoved;
	protected boolean isSingleJob;
	protected boolean isLastDay;

	public static Object getBean(String name) {
		return ContextLoader.getCurrentWebApplicationContext().getBean(name);
	}

	public Task() throws Exception {
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * 배치업무 컨트롤
	 * @throws Exception
	 */
	public void executeTask() throws Exception {
		if (isLastDay) {
			Calendar cal = Calendar.getInstance();
			if (cal.getActualMaximum(Calendar.DAY_OF_MONTH) != cal.get(Calendar.DAY_OF_MONTH)){
				return;
			}
		}

		if (isSingleJob) {
			Scheduler.getInstance().shutdown(key);
		}
		
		// 이전 작업이 구동중이라면 중지
		if (Scheduler.getInstance().isRunning(key)) {
			log.debug("[" + key + "] Previous work in progress and then stop running.");
			return;
		}
		
		// 로딩이 완료되지 않은 상태에서의 실행 중지
		if (paramMap == null) {
			log.debug("[" + key + "] Ready for operation stops not been completed.");
			return;
		}
		
		lastStartDate = Calendar.getInstance().getTime();

		try {
			successCount = 0;
			errorCount = 0;
			Scheduler.getInstance().setRunning(key, true);
			Scheduler.getInstance().setTask(key, this);

			// 업무 시작
			execute(paramMap);

			lastStatus = STATUS_NORMAL;
			lastMessage = "정상종료";

		} catch (Exception e) {
			lastStatus = STATUS_ERROR;
			lastMessage = "" + e.getMessage();
			/*
			 * 최종 결과 에러 메시지는 670자 이내로 자른다.
			 */
			if (lastMessage.length() >= 670) {
				lastMessage = lastMessage.substring(0, 669);
			}

			log.error("[" + key + "] error occurs.", e);
		} finally {
			lastEndDate = Calendar.getInstance().getTime();
			Scheduler.getInstance().setRunning(key, false);

			if (isRemoved) {
				Scheduler.getInstance().remove(key);
			}
		}
	}

	/**
	 * 배치업무 실행
	 * 
	 * @param params
	 * @throws Exception
	 */
	protected abstract void execute(Map<String, String> params) throws Exception;

	/**
	 * 처리건수 카운팅
	 */
	public void success() {
		success(true);
	}

	public void success(boolean isPlus) {
		if (isPlus) {
			successCount++;
		} else {
			successCount--;
		}
	}

	/**
	 * 진행 중 처리 건수 반환
	 * 
	 * @return
	 */
	public long getSuccessCount() {
		return successCount;
	}

	/**
	 * 실패건수 계수
	 */
	public void error() {
		error(true);
	}

	protected void error(boolean isPlus) {
		if (isPlus) {
			errorCount++;
		} else {
			errorCount--;
		}
	}

	public void setErrorCount(long errorCount) {
		this.errorCount = errorCount;
	}

	public String getKey() {
		return key;
	}

	public String getBizName() {
		return bizName;
	}

	public String getName() {
		return name;
	}

	public boolean isSingleJob() {
		return isSingleJob;
	}

	public Date getLastStartDate() {
		return lastStartDate;
	}

	public String getParams() {
		return params;
	}

	public Date getLastEndDate() {
		return lastEndDate;
	}

	public int getLastStatus() {
		return lastStatus;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public String getCron() {
		return cron;
	}

	public boolean isLastDay() {
		return isLastDay;
	}

	public void setBizName(String bizName) {
		this.bizName = bizName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	public void setSingleJob(boolean isSingleJob) {
		this.isSingleJob = isSingleJob;
	}

	public void setLastStartDate(Date lastStartDate) {
		this.lastStartDate = lastStartDate;
	}

	public void setLastEndDate(Date lastEndDate) {
		this.lastEndDate = lastEndDate;
	}

	public void setLastStatus(int lastStatus) {
		this.lastStatus = lastStatus;
	}

	public void setLastMessage(String lastMessage) {
		if (lastMessage != null) {
			if (lastMessage.length() >= 670) {
				lastMessage = lastMessage.substring(0, 669);
			}
		}
		this.lastMessage = lastMessage;
	}

	public void setRemoved(boolean isRemoved) {
		this.isRemoved = isRemoved;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public void setIsLastDay(boolean isLastDay) {
		this.isLastDay = isLastDay;
	}

	public void setSuccessCount(long successCount) {
		this.successCount = successCount;
	}

	public long getErrorCount() {
		return errorCount;
	}

	@Override
	public String toString() {
		return "Task [contextPath=" + contextPath + ", bizName=" + bizName + ", key=" + key + ", name=" + name
				+ ", cron=" + cron + ", successCount=" + successCount + ", errorCount=" + errorCount
				+ ", lastStartDate=" + lastStartDate + ", lastEndDate=" + lastEndDate + ", lastStatus=" + lastStatus
				+ ", lastMessage=" + lastMessage + ", params=" + params + ", paramMap=" + paramMap + ", isRemoved="
				+ isRemoved + ", isSingleJob=" + isSingleJob + ", isLastDay=" + isLastDay + "]";
	}

}
