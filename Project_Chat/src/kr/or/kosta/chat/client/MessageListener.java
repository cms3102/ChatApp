package kr.or.kosta.chat.client;

import java.io.BufferedReader;
import java.io.IOException;

import kr.or.kosta.chat.common.Message;
import kr.or.kosta.chat.server.ChatSession;

/**
 * 채팅서버에서 전송되는 메시지 수신 스레드
 * @author 최명승
 */
public class MessageListener extends Thread {
	
	private ChatFrame ui;
	private BufferedReader in;
	
	private boolean running;
	
	public MessageListener(ChatFrame ui, BufferedReader in) {
		this.ui = ui;
		this.in = in;
		this.running = true;
	}
	
	/** 메시지 파싱 및 렌더링 */
	public void process(String serverMessage){
		String[] tokens = serverMessage.split(Message.DELIMITER);
		String messageType = tokens[0];
		String messageSender = tokens[1];
		String message = null;
		
		switch (messageType) {
			/**연결메시지*/
			case Message.CONNECT:
				ui.appendMessage(messageSender+"님이 연결되었습니다.");
				ui.appendUser(messageSender);
				break;
			/** 접속자 목록 수신 */
			case Message.USERLIST:
				message = tokens[2];
				String[] nickNames = message.split(",");
				ui.appendUsers(nickNames);
				break;
			
			/**다중 메시지*/
			case Message.MULTICHAT:
				message = tokens[2];
				ui.appendMessage("["+messageSender+"] : " + message);
				break;
				
			/**귓속말*/
			case Message.WHISPER:
				message = tokens[3];
				ui.appendMessage("["+messageSender+"]님의 귓속말 : " + message);
				break;
			
			
			/**접속 종료 메시지*/	
			case Message.DISCONNECT:
				ui.appendMessage(messageSender+"님이 연결 종료하였습니다.");
				ui.removeUser(messageSender);
				break;
			
			/**서버 연결 종료 메시지*/	
			case Message.GOODBYE:
				running = false;
				ui.postDisConnect();
				break;
		}
	}
	
	
	public void run() {
		try {
			while(running) {
				String serverMessage = in.readLine();
				if(serverMessage == null) break;
				System.out.println("[디버깅] : Server Message: " + serverMessage);
				process(serverMessage);
			}
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
	}
}
