package project.kais.schedule.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

@Component
public class Task_manageips extends Task {
	
	private static Logger log = LoggerFactory.getLogger(Task_manageips.class);

	private static String MANAGE_IPS;

	/**
	 * 관리대상 IP 반환
	 * @return
	 */
	public static String getManageIps() {
		try {
			if (MANAGE_IPS == null) {
				load();
			}
		} catch (Exception e) {
			log.error("Loading managed IPs is failed.", e);
		}
		return MANAGE_IPS;
	}

	/**
	 * 혀용 IP 여부검사
	 * @param ip
	 * @return
	 */
	public static boolean isAllowedIp(String ip) {
		if (getManageIps().indexOf("[ALL]") >= 0) {
			return true;
		}

		if ("".equals(getManageIps().trim())) {
			return true;
		}

		if (getManageIps().indexOf("[" + ip + "]") >= 0) {
			return true;
		}
		return false;
	}

	public Task_manageips() throws Exception {
		super();
	}

	@Override
	protected void execute(Map<String, String> params) throws Exception {
		try {
			load();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 관리대상 IP 목록 설정 파일을 로딩한다.
	 * @throws Exception
	 */
	private static void load() throws Exception {
		String path = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/config/ManageIPs.cfg");
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		String line = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));

			while ((line = reader.readLine()) != null) {
				buffer.append(line).append(System.getProperty("line.separator"));
			}
			MANAGE_IPS = buffer.toString();
		} catch (Exception e) {
			log.error("Loading /WEB-INF/config/ManageIPs.cfg file is faild.", e);
			throw e;
		} finally {
			try {
				if (reader != null){
					reader.close();
				}
			} catch (Exception ex) {
				log.error("Reader for /WEB-INF/config/ManageIPs.cfg file is not closed.", ex);
			}
		}
	}
}