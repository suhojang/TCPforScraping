package com.kwic.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import project.kais.logger.Logger;
import project.kais.logger.LoggerFactory;

import com.kwic.exception.DefinedException;
import com.kwic.io.JXL;
import com.kwic.telegram.web.JHttpClient;
import com.kwic.util.StringUtil;
import com.kwic.web.KwicServletRequest;
import com.kwic.web.servlet.RequestTokenInterceptor;

public class Controllers {
	
	protected Logger logger = LoggerFactory.getLogger("com.kwic");

	public static final int ROW_PER_PAGE = 20;

	public static final String REQUEST_ERROR_MSG = "REQUEST_ERROR_MSG";

	public static final Map<String, String> CONTENT_TYPE_IMAGE = new HashMap<String, String>();
	static {
		CONTENT_TYPE_IMAGE.put("3ds", "image/x-3ds");
		CONTENT_TYPE_IMAGE.put("art", "image/x-jg");
		CONTENT_TYPE_IMAGE.put("bmp", "image/bmp");
		CONTENT_TYPE_IMAGE.put("btif", "image/prs.btif");
		CONTENT_TYPE_IMAGE.put("cgm", "image/cgm");
		CONTENT_TYPE_IMAGE.put("cmx", "image/x-cmx");
		CONTENT_TYPE_IMAGE.put("dib", "image/bmp");
		CONTENT_TYPE_IMAGE.put("djv", "image/vnd.djvu");
		CONTENT_TYPE_IMAGE.put("djvu", "image/vnd.djvu");
		CONTENT_TYPE_IMAGE.put("dmg", "application/x-apple-diskimage");
		CONTENT_TYPE_IMAGE.put("dwg", "image/vnd.dwg");
		CONTENT_TYPE_IMAGE.put("dxf", "image/vnd.dxf");
		CONTENT_TYPE_IMAGE.put("fbs", "image/vnd.fastbidsheet");
		CONTENT_TYPE_IMAGE.put("fh", "image/x-freehand");
		CONTENT_TYPE_IMAGE.put("fh4", "image/x-freehand");
		CONTENT_TYPE_IMAGE.put("fh5", "image/x-freehand");
		CONTENT_TYPE_IMAGE.put("fh7", "image/x-freehand");
		CONTENT_TYPE_IMAGE.put("fhc", "image/x-freehand");
		CONTENT_TYPE_IMAGE.put("fpx", "image/vnd.fpx");
		CONTENT_TYPE_IMAGE.put("fst", "image/vnd.fst");
		CONTENT_TYPE_IMAGE.put("g3", "image/g3fax");
		CONTENT_TYPE_IMAGE.put("gif", "image/gif");
		CONTENT_TYPE_IMAGE.put("ico", "image/x-icon");
		CONTENT_TYPE_IMAGE.put("ief", "image/ief");
		CONTENT_TYPE_IMAGE.put("iso", "application/x-iso9660-image");
		CONTENT_TYPE_IMAGE.put("jpe", "image/jpeg");
		CONTENT_TYPE_IMAGE.put("jpeg", "image/jpeg");
		CONTENT_TYPE_IMAGE.put("jpg", "image/jpeg");
		CONTENT_TYPE_IMAGE.put("ktx", "image/ktx");
		CONTENT_TYPE_IMAGE.put("mac", "image/x-macpaint");
		CONTENT_TYPE_IMAGE.put("mdi", "image/vnd.ms-modi");
		CONTENT_TYPE_IMAGE.put("mmr", "image/vnd.fujixerox.edmics-mmr");
		CONTENT_TYPE_IMAGE.put("npx", "image/vnd.net-fpx");
		CONTENT_TYPE_IMAGE.put("odi", "application/vnd.oasis.opendocument.image");
		CONTENT_TYPE_IMAGE.put("oti", "application/vnd.oasis.opendocument.image-template");
		CONTENT_TYPE_IMAGE.put("pbm", "image/x-portable-bitmap");
		CONTENT_TYPE_IMAGE.put("pct", "image/pict");
		CONTENT_TYPE_IMAGE.put("pcx", "image/x-pcx");
		CONTENT_TYPE_IMAGE.put("pgm", "image/x-portable-graymap");
		CONTENT_TYPE_IMAGE.put("pic", "image/pict");
		CONTENT_TYPE_IMAGE.put("pict", "image/pict");
		CONTENT_TYPE_IMAGE.put("png", "image/png");
		CONTENT_TYPE_IMAGE.put("pnm", "image/x-portable-anymap");
		CONTENT_TYPE_IMAGE.put("pnt", "image/x-macpaint");
		CONTENT_TYPE_IMAGE.put("ppm", "image/x-portable-pixmap");
		CONTENT_TYPE_IMAGE.put("psd", "image/vnd.adobe.photoshop");
		CONTENT_TYPE_IMAGE.put("qti", "image/x-quicktime");
		CONTENT_TYPE_IMAGE.put("qtif", "image/x-quicktime");
		CONTENT_TYPE_IMAGE.put("ras", "image/x-cmu-raster");
		CONTENT_TYPE_IMAGE.put("rgb", "image/x-rgb");
		CONTENT_TYPE_IMAGE.put("rlc", "image/vnd.fujixerox.edmics-rlc");
		CONTENT_TYPE_IMAGE.put("sgi", "image/sgi");
		CONTENT_TYPE_IMAGE.put("sid", "image/x-mrsid-image");
		CONTENT_TYPE_IMAGE.put("svg", "image/svg+xml");
		CONTENT_TYPE_IMAGE.put("svgz", "image/svg+xml");
		CONTENT_TYPE_IMAGE.put("t3", "application/x-t3vm-image");
		CONTENT_TYPE_IMAGE.put("tga", "image/x-tga");
		CONTENT_TYPE_IMAGE.put("tif", "image/tiff");
		CONTENT_TYPE_IMAGE.put("tiff", "image/tiff");
		CONTENT_TYPE_IMAGE.put("uvg", "image/vnd.dece.graphic");
		CONTENT_TYPE_IMAGE.put("uvi", "image/vnd.dece.graphic");
		CONTENT_TYPE_IMAGE.put("uvvg", "image/vnd.dece.graphic");
		CONTENT_TYPE_IMAGE.put("uvvi", "image/vnd.dece.graphic");
		CONTENT_TYPE_IMAGE.put("wbmp", "image/vnd.wap.wbmp");
		CONTENT_TYPE_IMAGE.put("wdp", "image/vnd.ms-photo");
		CONTENT_TYPE_IMAGE.put("webp", "image/webp");
		CONTENT_TYPE_IMAGE.put("xbm", "image/x-xbitmap");
		CONTENT_TYPE_IMAGE.put("xif", "image/vnd.xiff");
		CONTENT_TYPE_IMAGE.put("xpm", "image/x-xpixmap");
		CONTENT_TYPE_IMAGE.put("xwd", "image/x-xwindowdump");
		CONTENT_TYPE_IMAGE.put("pdf", "application/pdf");
	}

	/**
	 * ServletOutputStream 으로 이미지를 직접 write 하는 방식을 취한다. 이미지의 경로가 보안사항일 경우 사용한다.
	 * 
	 * @param imagePathP
	 *            다운로드할 이미지경로
	 * @param response
	 *            HttpServletResponse
	 * @exception IOException
	 */
	public void imageView(String imagePathP, HttpServletResponse response) throws Exception {
		String imagePath = StringUtils.replace(imagePathP, "\\", File.separator);
		       imagePath = StringUtils.replace(imagePath,  "/",  File.separator);

		String extType = "$%^&*()!@#$";
		if (imagePath.lastIndexOf(".") >= 0) {
			extType = imagePath.substring(imagePath.lastIndexOf(".") + 1);
		}

		String contentType = "";
		response.setStatus(HttpServletResponse.SC_OK);
		contentType = CONTENT_TYPE_IMAGE.get(extType.toLowerCase());
		if (contentType == null){
			throw new Exception("The file is not an image.");
		}

		File imgFile = new File(imagePath);
		if (!imgFile.exists() || imgFile.isDirectory()){
			throw new Exception("[" + imagePath + "] 파일을 찾을 수 없습니다.");
		}

		imageView(new FileInputStream(imgFile), response, contentType);
	}

	/**
	 * ServletOutputStream 으로 이미지를 직접 write 하는 방식을 취한다. 이미지의 경로가 보안사항일 경우 사용한다.
	 * 
	 * @param is
	 *            다운로드할 이미지 stream
	 * @param response
	 * @param imageContentType
	 *            이미지 타입
	 * @exception IOException
	 */
	public void imageView(InputStream is, HttpServletResponse response, String imageContentType) throws Exception {
		response.setContentType(imageContentType);
		write(is, response.getOutputStream());
	}

	/**
	 * 
	 * @param bytes
	 * @param response
	 * @param imageContentType
	 * @throws Exception
	 */
	public void imageView(byte[] bytes, HttpServletResponse response, String imageContentType) throws Exception {
		response.setContentType(imageContentType);
		write(bytes, response.getOutputStream());
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public String getImageContentType(String fileName) throws Exception {
		String extType = null;
		String contentType = "";
		if (fileName.lastIndexOf(".") >= 0) {
			extType = fileName.substring(fileName.lastIndexOf(".") + 1);
		}
		else {
			extType = fileName;
		}
		contentType = CONTENT_TYPE_IMAGE.get(extType.toLowerCase());
		if (contentType == null){
			throw new Exception("The file is not an image.");
		}
		return contentType;
	}

	/**
	 * 파일다운로드를 담당한다.
	 * @param filePath  다운로드할 파일경로
	 * @param fileName  저장될 파일명
	 * @param response HttpServletResponse
	 * @exception IOException
	 */
	public void fileDown(String filePath, String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String browser = getBrowser(request);
		String encodedFilename = "";

		if (browser.equals("MSIE")) {
			encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
		} else if (browser.equals("Firefox")) {
			encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (browser.equals("Opera")) {
			encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (browser.equals("Chrome")) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < fileName.length(); i++) {
				char c = fileName.charAt(i);
				if (c > '~') {
					sb.append(URLEncoder.encode("" + c, "UTF-8"));
				} else {
					sb.append(c);
				}
			}
			encodedFilename = sb.toString();
		} else {
			encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=" + encodedFilename);

		File fFile = new File(filePath);
		if (!fFile.exists() || fFile.isDirectory()) {
			return;
		}
		write(new FileInputStream(fFile), response.getOutputStream());
	}

	/**
	 * 파일다운로드를 담당한다.
	 * @param bytes 다운로드할 파일 bytes
	 * @param fileName 저장될 파일명
	 * @param response HttpServletResponse
	 * @exception IOException
	 */
	public void fileDown(byte[] bytes, String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String browser = getBrowser(request);
		String encodedFilename = "";

		if (browser.equals("MSIE")) {
			encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
		} else if (browser.equals("Firefox")) {
			encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (browser.equals("Opera")) {
			encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (browser.equals("Chrome")) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < fileName.length(); i++) {
				char c = fileName.charAt(i);
				if (c > '~') {
					sb.append(URLEncoder.encode("" + c, "UTF-8"));
				} else {
					sb.append(c);
				}
			}
			encodedFilename = sb.toString();
		} else {
			encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Content-Length", String.valueOf(bytes.length));
		response.setHeader("Content-Disposition", "attachment; filename=" + encodedFilename);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");

		write(bytes, response.getOutputStream());
	}

	/**
	 * 파일다운로드를 담당한다.
	 * @param bytes 다운로드할 파일 bytes
	 * @param fileName 저장될 파일명
	 * @param response HttpServletResponse
	 * @exception IOException
	 */
	public void imageFileDown(byte[] bytes, String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String browser = getBrowser(request);
		String encodedFilename = "";

		if (browser.equals("MSIE")) {
			encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
		} else if (browser.equals("Firefox")) {
			encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (browser.equals("Opera")) {
			encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (browser.equals("Chrome")) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < fileName.length(); i++) {
				char c = fileName.charAt(i);
				if (c > '~') {
					sb.append(URLEncoder.encode("" + c, "UTF-8"));
				} else {
					sb.append(c);
				}
			}
			encodedFilename = sb.toString();
		} else {
			encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", getImageContentType(fileName));
		response.setHeader("Content-Length", String.valueOf(bytes.length));
		response.setHeader("Content-Disposition", "attachment; filename=" + encodedFilename);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");

		write(bytes, response.getOutputStream());
	}

	/**
	 * 엑셀 다운로드를 담당한다.
	 * @param jxl  다운로드할 엑셀 객체
	 * @param fileName  저장될 파일명
	 * @param encoding 인코딩
	 * @param response HttpServletResponse
	 * @exception IOException
	 */
	public void excelDown(HttpServletResponse response, JXL jxl, String fileName) throws Exception {
		excelDown(response, jxl, fileName, "UTF-8");
	}

	/**
	 * 
	 * @param response
	 * @param jxl
	 * @param fileName
	 * @param encoding
	 * @throws Exception
	 */
	public void excelDown(HttpServletResponse response, JXL jxl, String fileName, String encoding) throws Exception {
		byte[] bytes = jxl.getBytes();
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName.indexOf(".XLS") < 0 ? fileName + ".XLS" : fileName, encoding == null ? "UTF-8" : encoding) + ";");
		response.setContentType("application/octet-stream;charset=" + encoding);
		response.setContentLength(bytes.length);

		write(bytes, response.getOutputStream());
	}

	/**
	 * OutputStream 으로 write.
	 * @param is 입력 stream
	 * @param os 출력 stream
	 * @exception IOException
	 */
	public static void write(InputStream is, OutputStream os) throws IOException {
		if (is == null || os == null) {
			return;
		}
		byte[] buf = new byte[1024];
		int iReadSize = 0;
		try {
			while ((iReadSize = is.read(buf)) != -1) {
				os.write(buf, 0, iReadSize);
			}
			os.flush();
		} catch (IOException ie) {
			throw ie;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException ie) {
			}
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException ie) {
			}
		}
	}

	/**
	 * OutputStream 으로 write.
	 * @param is  입력 stream
	 * @param os  출력 stream
	 * @exception IOException
	 */
	public static void write(byte[] bytes, OutputStream os) throws IOException {
		if (bytes == null || os == null)
			return;
		try {
			os.write(bytes);
			os.flush();
		} catch (IOException ie) {
			throw ie;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException ie) {
			}
		}
	}

	/**
	 * 브라우저 구분 얻기.
	 * 
	 * @param request
	 * @return
	 */
	private static String getBrowser(HttpServletRequest request) {
		String header = request.getHeader("User-Agent");
		if (header == null) {
			return "MSIE";
		} else if (header.indexOf("MSIE") > -1) {
			return "MSIE";
		} else if (header.indexOf("Chrome") > -1) {
			return "Chrome";
		} else if (header.indexOf("Opera") > -1) {
			return "Opera";
		}
		return "MSIE";
	}

	public static final String RESULT_CD = "RESULT_CD";
	public static final String RESULT_ERCD = "RESULT_ERCD";
	public static final String RESULT_MSG = "RESULT_MSG";
	public static final String PARAMETERS = "PARAMETERS";
	public static final String EXCEPTION = "exception";
	public static final String REDIRECT_URI = "REDIRECT_URI";
	public static final String REDIRECT_TYPE = "REDIRECT_TYPE";

	public static final String RESULT_ERCD_NOSESSION = "NO_SESSION";
	public static final String RESULT_ERCD_NOAUTH = "NO_AUTH";

	/**
	 * 
	 * @param model
	 * @param e
	 */
	public void error(Model model, Exception e) {
		logger.error(e.getMessage(), e);
		if (e instanceof DefinedException){
			model.addAttribute(REQUEST_ERROR_MSG, e.getMessage());
		}
		else{
			model.addAttribute(REQUEST_ERROR_MSG, "오류가 발생하였습니다.");
		}
	}

	/**
	 * 
	 * @param response
	 * @param e
	 * @throws Exception
	 */
	public void writeError(HttpServletResponse response, Exception e) throws Exception {
		logger.error(e.getMessage(), e);
		String msg = "오류가 발생하였습니다.";
		if (e instanceof DefinedException){
			msg = e.getMessage();
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/html; charset=UTF-8");

		response.getWriter().append(msg);
		response.getWriter().close();
	}

	/**
	 * 
	 * @param response
	 * @param error
	 * @param url
	 * @throws Exception
	 */
	public void writeMobileError(HttpServletResponse response, String error, String url) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("")
		   .append("<!doctype html>\n")
		   .append("<html lang=\"kr\">\n")
		   .append("<head>\n")
		   .append("	<meta charset=\"UTF-8\">\n")
		   .append("	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n")
		   .append("	<meta http-equiv=\"X-UA-Compatible\" content=\"IE=10,chrome=1\">\n")
		   .append("	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\" />\n")
		   .append("	<title>::출금동의 증빙시스템::</title>\n")
		   .append("	<link href=\"/mobile/css/main.css\" rel=\"stylesheet\" type=\"text/css\">\n")
		   .append("	<script type=\"text/javascript\" src=\"/mobile/js/jquery-1.11.2.min.js\"></script>\n")
		   .append("	<script type=\"text/javascript\" src=\"/mobile/js/jquery-ui.min.js\"></script>\n")
		   .append("	<script type=\"text/javascript\" src=\"/mobile/js/jquery.animateSprite.min.js\"></script>\n")
		   .append("	<script type=\"text/javascript\" src=\"/mobile/js/common.js\"></script>\n")	
		   .append("	<script type=\"text/javascript\" src=\"/mobile/js/web.js\"></script>\n").append("\n")
		   .append("	<script type=\"text/javascript\">\n").append("	function fn_init(){\n")
		   .append("		malert('" + StringUtil.replace(error, "\n", "<br/>") + "',function(){\n");
		
		if (url != null && !"".equals(url)){
			sb.append("			$(location).attr('href','" + url + "');\n");
		}
		sb.append("		});\n")
		  .append("	}\n")
		  .append("	$(document).ready(function(){fn_init();});\n")
		  .append("	</script>\n")
		  .append("</head>\n")
		  .append("<body>\n")
		  .append("</body>\n")
		  .append("</html>\n");

		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/html; charset=UTF-8");

		response.getWriter().append(sb.toString());
		response.getWriter().close();
	}

	/**
	 * response for ajax (jsonp)
	 * 
	 */
	public void ajaxResponse(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(RequestTokenInterceptor.ATTRIBUTE_NAME, request.getSession().getAttribute(RequestTokenInterceptor.ATTRIBUTE_NAME));
		result.put(RESULT_CD, "Y");
		// result.put(RESULT_MSG, "정상처리되었습니다.");
		result.put(RESULT_MSG, "");
		ajaxResponse(response, result, checkParameter(request, "callback"), "UTF-8");
	}

	public void ajaxResponse(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
		logger.error(e.getMessage(), e);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(RequestTokenInterceptor.ATTRIBUTE_NAME, 	request.getSession().getAttribute(RequestTokenInterceptor.ATTRIBUTE_NAME));
		result.put(RESULT_CD, "N");
		if (e instanceof DefinedException){
			result.put(RESULT_MSG, e.getMessage());
		}
		else{
			result.put(RESULT_MSG, "오류가 발생하였습니다.");
		}
		ajaxResponse(response, result, checkParameter(request, "callback"), "UTF-8");
	}

	public void ajaxResponse(HttpServletRequest request, HttpServletResponse response, Map<String, Object> obj) throws Exception {
		obj.put(RequestTokenInterceptor.ATTRIBUTE_NAME, request.getSession().getAttribute(RequestTokenInterceptor.ATTRIBUTE_NAME));
		ajaxResponse(response, obj, checkParameter(request, "callback"), "UTF-8");
	}

	public void ajaxResponse(HttpServletResponse response, Map<String, Object> obj, String callback) throws Exception {
		ajaxResponse(response, obj, callback, "UTF-8");
	}

	public void ajaxResponse(HttpServletResponse response, Map<String, Object> obj, String callback, String encoding) throws Exception {
		if (obj.get(RESULT_CD) == null) {
			obj.put(RESULT_CD, "Y");
			obj.put(RESULT_MSG, "");
		}

		String jsonString = new ObjectMapper().writeValueAsString(obj);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "application/json; charset=" + encoding);
		response.getWriter().append(callback == null || "".equals(callback) ? "" : callback).append("(").append(jsonString).append(")");
		response.getWriter().close();
	}

	public void jsonResponseNoMessage(HttpServletRequest request, HttpServletResponse response, Map<String, Object> obj)	throws Exception {
		jsonResponseNoMessage(response, obj, checkParameter(request, "callback"), "UTF-8");
	}

	public void jsonResponseNoMessage(HttpServletResponse response, Map<String, Object> obj, String callback)	throws Exception {
		jsonResponseNoMessage(response, obj, callback, "UTF-8");
	}

	public void jsonResponseNoMessage(HttpServletResponse response, Map<String, Object> obj, String callback,	String encoding) throws Exception {
		String jsonString = new ObjectMapper().writeValueAsString(obj);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "application/json; charset=" + encoding);
		response.getWriter().append(jsonString);
		response.getWriter().close();
	}

	/**
	 * response for ajax (jsonp)
	 * 
	 */
	public void jsonResponse(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(RESULT_CD, "Y");
		// result.put(RESULT_MSG, "정상처리되었습니다.");
		result.put(RESULT_MSG, "");
		jsonResponse(response, result, "UTF-8");
	}

	public void jsonResponse(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
		logger.error(e.getMessage(), e);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(RESULT_CD, "N");
		if (e instanceof DefinedException){
			result.put(RESULT_MSG, e.getMessage());
		}
		else{
			result.put(RESULT_MSG, "오류가 발생하였습니다.");
		}
		jsonResponse(response, result, "UTF-8");
	}

	public void jsonResponse(HttpServletRequest request, HttpServletResponse response, Map<String, Object> obj) throws Exception {
		jsonResponse(response, obj, "UTF-8");
	}

	public void jsonResponse(HttpServletResponse response, Map<String, Object> obj, String encoding) throws Exception {
		if (obj.get(RESULT_CD) == null) {
			obj.put(RESULT_CD, "Y");
			// obj.put(RESULT_MSG, "정상처리되었습니다.");
			obj.put(RESULT_MSG, "");
		}

		String jsonString = new ObjectMapper().writeValueAsString(obj);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "application/json; charset=" + encoding);
		response.getWriter().append(jsonString);
		response.getWriter().close();
	}

	public String checkParameter(HttpServletRequest request, String paramId) throws Exception {
		String param = request.getParameter(paramId);
		if (param == null && request.getAttribute("SAVED_REQUEST") != null)
			try {
				param = ((KwicServletRequest) request.getAttribute("SAVED_REQUEST")).getParameter(paramId);
			} catch (Exception e) {
			}
		if (param == null){
			return "";
		}

		String lowerParam = param.toLowerCase(Locale.KOREA);
		String[] keywords = new String[] { "<script", "javascript", "<iframe", "<object", "<applet", "<embed", "<form", "select", "insert", "update", "delete", "merge", "drop", "declare", "/*", "*/", "--" };
		for (int i = 0; i < keywords.length; i++){
			if (lowerParam.indexOf(keywords[i]) >= 0) {
				throw new DefinedException("입력값으로 [" + StringUtil.replace(StringUtil.replace(keywords[i], "<", "&lt;"), ">", "&gt;") + "]를 사용할 수 없습니다.");
			}
		}

		return param;
	}

	public String getParam(HttpServletRequest request, String paramId) throws Exception {
		return getParam(request, paramId, -1);
	}

	public String getParam(HttpServletRequest request, String paramId, int maxByteSize) throws Exception {
		String param = checkParameter(request, paramId);
		if (maxByteSize > 0 && param.getBytes().length > maxByteSize) {
			throw new DefinedException(paramId + " 제한 크기를 초과하였습니다.");
		}
		return param;
	}

	public Map<String, String> getParamMap(HttpServletRequest request) {
		Map<String, String> paramMap = new HashMap<String, String>();
		Enumeration<?> e = request.getParameterNames();
		String name = null;
		while (e.hasMoreElements()) {
			name = (String) e.nextElement();
			paramMap.put(name, request.getParameter(name));
		}
		return paramMap;
	}

	public String getDecodeParam(HttpServletRequest request, String paramId) throws Exception {
		return new String(java.net.URLDecoder.decode(checkParameter(request, paramId), "UTF-8"));
	}

	public String getParam(HttpServletRequest request, String paramId, String paramName, boolean required) throws Exception {
		return getParam(request, paramId, paramName, required, -1);
	}

	public String getParam(HttpServletRequest request, String paramId, String paramName, boolean required, int maxByteSize) throws Exception {
		String param = checkParameter(request, paramId);
		if (required && "".equals(param)){
			throw new DefinedException(paramName + " 입력되지 않았습니다.");
		}
		if (maxByteSize > 0 && param.getBytes().length > maxByteSize) {
			throw new DefinedException(paramName + " 제한 크기를 초과하였습니다.");
		}
		return param;
	}

	public String getDecodeParam(HttpServletRequest request, String paramId, String paramName, boolean required) throws Exception {
		return getDecodeParam(request, paramId, paramName, required, -1);
	}

	public String getDecodeParam(HttpServletRequest request, String paramId, String paramName, boolean required, int maxByteSize) throws Exception {
		String param = new String(java.net.URLDecoder.decode(checkParameter(request, paramId), "UTF-8"));
		if (required && "".equals(param)){
			throw new DefinedException(paramName + " 입력되지 않았습니다.");
		}
		if (maxByteSize > 0 && param.getBytes().length > maxByteSize){
			throw new DefinedException(paramName + " 제한 크기를 초과하였습니다.");
		}
		return param;
	}

	public String setParam(Map<String, String> map, HttpServletRequest request, String paramId) throws Exception {
		return setParam(map, request, paramId, null, false);
	}

	public String setDecodeParam(Map<String, String> map, HttpServletRequest request, String paramId) throws Exception {
		return setDecodeParam(map, request, paramId, null, false);
	}

	public String setParam(Map<String, String> map, HttpServletRequest request, String paramId, String paramName, boolean required) throws Exception {
		String param = checkParameter(request, paramId);
		if (required && "".equals(param)) {
			throw new DefinedException(paramName + " 입력되지 않았습니다.");
		}
		map.put(paramId, param);
		return param;
	}

	public String setDecodeParam(Map<String, String> map, HttpServletRequest request, String paramId, String paramName, boolean required) throws Exception {
		String param = new String(java.net.URLDecoder.decode(checkParameter(request, paramId), "UTF-8"));
		if (required && "".equals(param)){
			throw new DefinedException(paramName + " 입력되지 않았습니다.");
		}
		map.put(paramId, param);
		return param;
	}

	public String[] getPagingValue(HttpServletRequest request) throws Exception {
		int PAGE_NO = Integer.parseInt(getParam(request, "PAGE_NO", "페이지번호가", true));
		int ROW_PER_PAGE = Integer.parseInt(getParam(request, "ROW_PER_PAGE", "페이지당 표시 건수가", true));
		if (ROW_PER_PAGE > 50) {
			throw new DefinedException("페이지당 표시 건수가가 올바르지 않습니다.");
		}
		int ST_ROW_NO = (PAGE_NO - 1) * ROW_PER_PAGE + 1;
		int ED_ROW_NO = PAGE_NO * ROW_PER_PAGE;

		return new String[] { String.valueOf(ST_ROW_NO), String.valueOf(ED_ROW_NO) };
	}

	public void setPagingValue(Map<String, String> param, HttpServletRequest request) throws Exception {
		int PAGE_NO = Integer.parseInt(getParam(request, "PAGE_NO", "페이지번호가", true));
		int ROW_PER_PAGE = Integer.parseInt(getParam(request, "ROW_PER_PAGE", "페이지당 표시 건수가", true));
		if (ROW_PER_PAGE > 100) {
			throw new DefinedException("페이지당 표시 건수가 제한 건수를 초과하였습니다.");
		}
		int ST_ROW_NO = (PAGE_NO - 1) * ROW_PER_PAGE + 1;
		int ED_ROW_NO = PAGE_NO * ROW_PER_PAGE;

		param.put("ST_ROW_NO", String.valueOf(ST_ROW_NO));
		param.put("ED_ROW_NO", String.valueOf(ED_ROW_NO));
	}

	/**
	 * 접속 IP 확인
	 */
	public static String getRemoteIP(HttpServletRequest request) {
		String ip = request.getHeader("WL-Proxy-Client-IP");
		if (ip == null || "".equals(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || "".equals(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (ip == null || "".equals(ip)) {
			ip = request.getRemoteAddr();
		}
		if ("0:0:0:0:0:0:0:1".equals(ip)){
			ip = "127.0.0.1";
		}
		return ip;
	}

	/**
	 * 접속 브라우져 종류 확인
	 */
	public String getRemoteBrowser(HttpServletRequest request) {
		String user_agent = request.getHeader("user-agent");

		// 웹브라우저 종류 조회
		String webKind = "";
		if (user_agent.toUpperCase().indexOf("GECKO") != -1) {
			if (user_agent.toUpperCase().indexOf("NESCAPE") != -1) {
				webKind = "Netscape (Gecko/Netscape)";
			} else if (user_agent.toUpperCase().indexOf("FIREFOX") != -1) {
				webKind = "Mozilla Firefox (Gecko/Firefox)";
			} else {
				webKind = "Mozilla (Gecko/Mozilla)";
			}
		} else if (user_agent.toUpperCase().indexOf("MSIE") != -1) {
			if (user_agent.toUpperCase().indexOf("OPERA") != -1) {
				webKind = "Opera (MSIE/Opera/Compatible)";
			} else {
				webKind = "Internet Explorer (MSIE/Compatible)";
			}
		} else if (user_agent.toUpperCase().indexOf("SAFARI") != -1) {
			if (user_agent.toUpperCase().indexOf("CHROME") != -1) {
				webKind = "Google Chrome";
			} else {
				webKind = "Safari";
			}
		} else if (user_agent.toUpperCase().indexOf("THUNDERBIRD") != -1) {
			webKind = "Thunderbird";
		} else {
			webKind = "Other Web Browsers";
		}
		return webKind;
	}

	/**
	 * 접속 브라우져 바젼 확인
	 */
	public String getRemoteBrowserVersion(HttpServletRequest request) {
		String user_agent = request.getHeader("user-agent");

		// 웹브라우저 버전 조회
		String webVer = "";
		String[] arr = { "MSIE", "OPERA", "NETSCAPE", "FIREFOX", "SAFARI" };
		for (int i = 0; i < arr.length; i++) {
			int s_loc = user_agent.toUpperCase().indexOf(arr[i]);
			if (s_loc != -1) {
				int f_loc = s_loc + arr[i].length();
				webVer = user_agent.toUpperCase().substring(f_loc, f_loc + 5);
				webVer = webVer.replaceAll("/", "").replaceAll(";", "").replaceAll("^", "").replaceAll(",", "").replaceAll("//.", "");
			}
		}
		return webVer;
	}

	public String remoteRequest(String url, HttpServletRequest request) throws Exception {
		String jsonString = null;
		try {
			jsonString = JHttpClient.getInstance().doPost(url, getParamMap(request), "UTF-8").trim();
		} catch (Exception e) {
			throw e;
		}
		return jsonString;
	}

	public void remoteRequest(String url, HttpServletRequest request, HttpServletResponse response, Model model)
			throws Exception {
		try {
			String jsonString = JHttpClient.getInstance().doPost(url, getParamMap(request), "UTF-8").trim();
			String callback = checkParameter(request, "callback");
			response.setStatus(HttpServletResponse.SC_OK);
			response.setHeader("Content-Type", "application/json; charset=UTF-8");
			response.getWriter().append(callback == null || "".equals(callback) ? "" : callback).append("(").append(jsonString).append(")");
			response.getWriter().close();
		} catch (Exception e) {
			ajaxResponse(request, response, e);// 오류
			logger.error(e.getMessage(), e);
		}
	}

	public String getVariablePath(HttpServletRequest request, String fixUrl) {
		String var = StringUtil.replace(request.getRequestURI(), fixUrl, "");
		if (var.endsWith("/")){
			var = var.substring(0, var.length() - 2);
		}
		return var;
	}
}
