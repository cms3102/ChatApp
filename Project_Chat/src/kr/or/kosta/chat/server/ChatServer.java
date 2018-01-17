package kr.or.kosta.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

import kr.or.kosta.chat.common.Message;

/**
 * 채팅 서버
 *
 * @author 최명승
 */
public class ChatServer {
	
	public static final int LISTENING_PORT = 2000;
	
	private ServerSocket serverSocket;
	private boolean running;
	
	private Hashtable<String, ChatSession> clients;
	
	public ChatServer() {
		clients = new Hashtable<String, ChatSession>();
	}
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public Hashtable<String, ChatSession> getClients() {
		return clients;
	}

	public void setClients(Hashtable<String, ChatSession> clients) {
		this.clients = clients;
	}

	
	public void startUp() throws IOException {
		serverSocket = new ServerSocket(LISTENING_PORT);
		running = true;
	}
	
	public void shutDown() {
		if(serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void clientListening() {
		while(running) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				String clientIp = socket.getInetAddress().getHostAddress();
				System.out.println("채팅 클라이언트 연결됨 [IP : "+clientIp+"]");
				
				ChatSession client = new ChatSession(this, socket);
				client.start();
				
			}catch (IOException e) {
				e.printStackTrace();
				System.err.println("채팅 클라이언트 연결 중 장애 발생 - [IP : \"+socket.getInetAddress().getHostAddress()+\"]");
			}		
		}
		shutDown();
	}
	
	
	/** 메시지 파싱 및 분배 */
	public void messageDispatch(ChatSession client , String clientMessage) {
		
		String[] tokens = clientMessage.split(Message.DELIMITER);
		String messageType = tokens[0];
		String messageSender = tokens[1];
		String message = null;
		
		switch (messageType) {
		
			/**접속 메시지*/
			case Message.CONNECT:
//				1.새로 연결한 클라이언트에게만 기존 접속자 목록 전송
				String userNickNames = getUserNickNames();
				if(userNickNames != null){
					client.sendMessage(Message.USERLIST + Message.DELIMITER + "SERVER" + Message.DELIMITER + userNickNames);
				}
				
//				2.접속한 클라이언트를 기존 접속자 목록에 추가
				client.setUserNickName(messageSender);
				addClient(client);
				
//				3.접속자 목록에 있는 모든 클라이언트들에게 메시지 전송
				sendAllMessage(clientMessage);
				
//				서버 로그
				System.out.println("["+messageSender+"]님이 연결하였습니다.");
				break;
			
			/**다중 채팅 메시지*/
			case Message.MULTICHAT:
				message = tokens[2];
				sendAllMessage(clientMessage);
				System.out.println("["+messageSender+"] : " + message);
				break;
			
			/**귓속말*/
			case Message.WHISPER:
				String targetUser = tokens[2];
				message = tokens[3];
				ChatSession targetClient = searchClient(targetUser);
				targetClient.sendMessage(clientMessage);
				System.out.println(messageSender + "님이 "+targetUser+"님에게 귓속말 : " + message);
				break;
			
			
			/**접속 종료 메시지*/	
			case Message.DISCONNECT:
//				1.접속자 리스트에서 제거
				removeClient(messageSender);
//				2.현재 접속자 모두에게 종료 메시지 전송
				sendAllMessage(clientMessage);
//				3. 및 소켓 종료
				client.sendMessage(Message.GOODBYE + Message.DELIMITER + "SERVER");
				client.setRunning(false);
				System.out.println(messageSender+"님이 연결을 종료하셨습니다.");
				break;
		}
	}
	
	/** 접속한 모든 클라언트들에게 메시지 전송 */
	public void sendAllMessage(String message) {
		Enumeration<ChatSession> e = clients.elements();
		while (e.hasMoreElements()) {
			ChatSession connectedClient =  e.nextElement();
			connectedClient.sendMessage(message);
		}
	}
	
	/** 접속한 클라이언트 등록 */
	public void addClient(ChatSession client){
		clients.put(client.getUserNickName(), client);
	}
	
	/** 접속이 종료된 클라이언트 제거 */
	public void removeClient(String userNickName){
		clients.remove(userNickName);
	}
	
	/** 접속한 클라이언트 검색 */
	public ChatSession searchClient(String userNickName){
		return clients.get(userNickName);
	}
	
	/** 접속자 목록을 CSV(Comma Separator Value)로 파싱하여 반환 */
	public String getUserNickNames(){
		if(clients.size() == 0 ){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Enumeration<String> e = clients.keys();
		while (e.hasMoreElements()) {
			String userId = e.nextElement();
			sb.append(userId + ",");
		}
		return sb.toString();
	}
	
	
}
