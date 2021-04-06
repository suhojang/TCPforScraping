package project.kais.init.client;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.springframework.web.context.ContextLoader;

import com.kwic.util.StringUtil;
import com.kwic.xml.parser.JXParser;

public class AIB_ERRORS {
	private static AIB_ERRORS instance;

	public static final String DEFAULT_ERROR_CODE = "E888";
	public static final String DEFAULT_NORMAL_CODE = "P000";

	Map<String, ERROR> info = new HashMap<String, ERROR>();

	private AIB_ERRORS() throws Exception {
		String structPath = StringUtil.replace(	ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"), "\\", "/") + "/aib-error-codes.xml";
		JXParser erJxp = new JXParser(new File(structPath));

		Element[] items = erJxp.getElements("//item");
		Element[] errors = null;
		List<Map<String, String>> list = null;
		Map<String, String> error = null;
		ERROR er = null;

		for (int i = 0; i < items.length; i++) {
			er = new ERROR();
			er.setBzcd(erJxp.getAttribute(items[i], "SPECIALCODE") + "-" + erJxp.getAttribute(items[i], "MODULE"));
			er.setAnothers(erJxp.getAttribute(items[i], "anothers"));
			er.setNormal(erJxp.getAttribute(items[i], "normal"));

			errors = erJxp.getElements(items[i], "error");
			list = new ArrayList<Map<String, String>>();
			for (int j = 0; j < errors.length; j++) {
				error = new HashMap<String, String>();
				error.put("code",    erJxp.getAttribute(errors[j], "code"));
				error.put("message", erJxp.getValue(errors[j]).trim());
				list.add(error);
			}
			er.setList(list);
			info.put(er.getBzcd(), er);
		}
	}

	public static AIB_ERRORS getInstance() throws Exception {
		synchronized (AIB_ERRORS.class) {
			if (instance == null) {
				instance = new AIB_ERRORS();
			}
			return instance;
		}
	}

	public String getErrorCode(String bzcd, String msg) {
		if (bzcd == null || "".equals(bzcd.trim()))
			return DEFAULT_ERROR_CODE;

		ERROR er = info.get(bzcd);
		if (er == null)
			return DEFAULT_ERROR_CODE;

		if (msg == null || "".equals(msg.trim()))
			return er.getNormal();

		List<Map<String, String>> list = er.getList();

		for (int i = 0; i < list.size(); i++) {
			if (msg.indexOf(list.get(i).get("message")) >= 0)
				return list.get(i).get("code");
		}
		return er.getAnothers();
	}
}

class ERROR {
	String bzcd;
	String anothers;
	String normal;
	List<Map<String, String>> list;

	public String getBzcd() {
		return bzcd;
	}

	public void setBzcd(String bzcd) {
		this.bzcd = bzcd;
	}

	public String getAnothers() {
		return anothers;
	}

	public void setAnothers(String anothers) {
		this.anothers = anothers;
	}

	public String getNormal() {
		return normal;
	}

	public void setNormal(String normal) {
		this.normal = normal;
	}

	public List<Map<String, String>> getList() {
		return list;
	}

	public void setList(List<Map<String, String>> list) {
		this.list = list;
	}

}
