package kr.or.kosta.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 클라이언트와의 데이터 송수신 스레드
 * 
 * @author 최명승
 */
public class ChatSession extends Thread {
	
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private boolean running;
	
	private String userNickName;
	
	private ChatServer chatServer;
	
	public ChatSession(ChatServer chatServer, Socket socket) throws IOException {
		this.chatServer = chatServer;
		this.socket = socket;
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new PrintWriter(socket.getOutputStream());
		this.running = true;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public BufferedReader getIn() {
		return in;
	}

	public void setIn(BufferedReader in) {
		this.in = in;
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}
	
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public String getUserNickName() {
		return userNickName;
	}

	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}

	public ChatServer getChatServer() {
		return chatServer;
	}

	public void setChatServer(ChatServer chatServer) {
		this.chatServer = chatServer;
	}
	
	/** 본인에게 메시지 전송 */
	public void sendMessage(String message){
		out.println(message);
		out.flush();
	}
	
	public void close() {
		
		
	}

	@Override
	public void run() {
		try {
			while(running) {
				String chatMessage = in.readLine();
				if(chatMessage == null) break;
				System.out.println("[Debug] : Client Message: " + chatMessage);
				chatServer.messageDispatch(this, chatMessage);
			}
		}catch (IOException e) {
			System.out.println(socket.getInetAddress().getHostAddress()+" 클라이언트 연결 비정상적 종료.");
		} finally {
			if(socket != null) {
				try {
					socket.close();
				} catch (IOException e) {}
			}
		}
	}
	
	
	

}
