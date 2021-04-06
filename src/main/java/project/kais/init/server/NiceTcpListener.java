package project.kais.init.server;

import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kwic.math.Calculator;
import com.kwic.support.CryptoKeyGenerator;
import com.kwic.util.StringUtil;
import com.kwic.web.init.Handler;
import com.kwic.web.init.InitServiceImpl;

public class NiceTcpListener extends InitServiceImpl {
	
	private static Logger log = LoggerFactory.getLogger(NiceTcpListener.class);
	
	/**
	 * 핸들러 식별자(ID) 생성시 사용하는 최종 시퀀스
	 */
	private static int sequence;
	
	/**
	 * 사용자 요청을 접수하는 리스너의 서버 소켓
	 */
	private static ServerSocket serverSocket;
	
	/**
	 * 핸들러 클래스의 생성자
	 */
	private Constructor<?> handlerConstructor;

	/**
	 * 서버 연결 소켓풀
	 */
	private static Map<String, Socket> clients = new Hashtable<String, Socket>();
	private static boolean stop;

	/**
	 * 서버의 포트를 개방함으로써 서비스를 시작한다.
	 */
	@Override
	public void execute() throws Exception {
		Class<?> handlerClass = Class.forName(getServiceParamString("handler-class"));
		handlerConstructor = handlerClass.getConstructor();
		Handler handler = null;
		Socket socket = null;
		String clientId = sequence();
		int port = getServiceParamInt("port");
		int timeout = (int) Calculator.calculate(getServiceParamString("timeout"));
		String parserClass = getServiceParamString("parser-class");
		Map<String,Object> initParam = super.getServiceParams();
		
		try {
			serverSocket = new ServerSocket(port);
			log.debug(String.format("Listener service execution is opend. service name=%s, clientID=%s, port=%d", super.getServiceName(), clientId, port));

 			while (!stop && serverSocket != null && !serverSocket.isClosed()) {
				try {
					/*
					 * 여기서 사용자 요청을 접수한다.
					 */
					socket = serverSocket.accept();
					
					/*
					 * 사용자 요청이 접수되면 핸들러 클래스를 인스턴스화하여 실제로 필요한 업무를 실행한다.
					 */
					socket.setSoTimeout(timeout);
					handler = (Handler) handlerConstructor.newInstance();
					handler.put("client-id",      clientId);    //핸들러의 식별자
					handler.put("client-socket",  socket);      //핸들러가 요청 및 응답을 위하 사용할  접속 통로인 소켓
					handler.put("parser-class",   parserClass); //요청 및 응답 해석을 위한 파서 클래스
					handler.put("service-params", initParam);	//핸들러의 설정값 파라미터				
					handler.handle();                           //핸들러 작동 시작
					
				} catch (Exception e) {
					log.error(String.format("Listener service execution is failed. service name=%s, clientID=%s, port=%d, timeout=%d, param=%s", super.getServiceName(), clientId, port, timeout, initParam), e);
				}
			}//while
 			
		} catch (Exception e) {
			log.error(String.format("Listener service execution is failed. service name=%s, clientID=%s, port=%d, timeout=%d, param=%s", super.getServiceName(), clientId, port, timeout, initParam), e);
			throw e;
		} finally {
			log.debug(String.format("Listener service execution is closed. service name=%s, clientID=%s, port=%d", super.getServiceName(), clientId, port));
		}
	}

	/**
	 * 서비스 종료
	 * 소켓풀을 초기화 한다.
	 */
	@Override
	public void terminate() {
		stop = true;
		Iterator<String> iter = clients.keySet().iterator();
		String id = null;
		
		while (iter.hasNext()) {
			try {
				id = iter.next();
				clients.get(id).close();  //핸들러의 소켓을 차단한다.
				clients.remove(id);       //핸들러를 커넥션풀에서 제거한다.
			} catch (Exception e) {
				log.error(String.format("Service termination is failed. service ID=%s", id), e);
			}
		}
		
		try {
			serverSocket.close();  //리스너를 차단한다.
			serverSocket = null;
		} catch (Exception e) {
			log.error("Server socket closing is failed.", e);
		}
	}

	/**
	 * 리스너의 연결객체 풀에 연결 객체를 추가한다.
	 * @param id 연결객체 제어를 위한 ID
	 * @param socket 연결객체(소켓)
	 */
	public static void addClientSocket(String id, Socket socket) {
		clients.put(id, socket);
	}

	/**
	 * 연결 제거
	 * @param id
	 */
	public static void removeClientSocket(String id) {
		
		Socket socket =  clients.get(id);
		if(socket == null){
			return;
		}
		
		try {
			if(!socket.isClosed() && socket.getOutputStream() != null){
				socket.getOutputStream().close();
			}
		} catch (Exception e) {
			log.error(String.format("Closing of output stream is failed. client ID=%s", id), e);
		}
		
		try {
			if(!socket.isClosed() && socket.getInputStream() != null){
				socket.getInputStream().close();
			}
		} catch (Exception e) {
			log.error(String.format("Closing of input stream is failed. client ID=%s", id), e);
		}
		
		try {			
			clients.remove(id);
			
		} catch (Exception e) {
			log.error(String.format("Disconnection is failed. client ID=%s", id), e);
		} finally {
			try {
				if (socket != null){
					socket.close();
					socket = null;
				}
			} catch (Exception e) {
				log.error(String.format("Socket closing is failed. client ID=%s", id), e);
			}
		}
	}

	/**
	 * 핸들러 객체의 client ID를 생성한다.
	 * @return
	 */
	public synchronized static String sequence() {
		int MAX_VALUE = 99999999;
		if (sequence >= MAX_VALUE){
			sequence = 0;
		}
		String key = CryptoKeyGenerator.getRandomKey(
											CryptoKeyGenerator.ALGORITHM_SEED128
											,new int[] { CryptoKeyGenerator.KEY_TYPE_NUM
													   , CryptoKeyGenerator.KEY_TYPE_ENG_CAPITAL,
						                                 CryptoKeyGenerator.KEY_TYPE_ENG_SMALL });

		return key + "-" + StringUtil.addChar(String.valueOf(sequence++), String.valueOf(MAX_VALUE).length(), "0", true);
	}

	/**
	 * 현재 연결 건수 반환
	 * @return
	 */
	public static int currentRequestCount() {
		return clients.size();
	}
}
