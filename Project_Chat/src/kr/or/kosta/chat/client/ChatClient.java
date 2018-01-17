package kr.or.kosta.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * 채팅 클라이언트
 * 
 * @author 최명승
 */
public class ChatClient {
	public static final String SERVER_IP = "localhost";
	public static final int SERVER_PORT = 2000;
	
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private boolean running;
	
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public PrintWriter getOut() {
		return out;
	}
	public void setOut(PrintWriter out) {
		this.out = out;
	}
	public BufferedReader getIn() {
		return in;
	}
	public void setIn(BufferedReader in) {
		this.in = in;
	}
	
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public void connect() throws UnknownHostException, IOException {
		socket = new Socket(SERVER_IP, SERVER_PORT);
		out = new PrintWriter(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		running = true;
	}
	
	public void disConnect() {
		if(socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void messageSend(String message) {
		out.println(message);
		out.flush();
	}
	
	
	public void messageListening() {
		try {
			while(running) {
				String serverMessage = in.readLine();
				if(serverMessage == null) break;
				System.out.println(serverMessage);
			}
		}catch (IOException e) {
//			e.printStackTrace();
			
		}
	}
	
	public static void main(String[] args) {
		ChatClient chatClient = new ChatClient();
		try {
			chatClient.connect();
			System.out.println("채팅 서버 연결["+SERVER_IP+"] >>>");
			
			Scanner scanner = new Scanner(System.in);
			while(chatClient.running) {
				String inputMessage = scanner.nextLine();
				if(inputMessage.equalsIgnoreCase("exit")) {
					break;
				}
				
				inputMessage = "(사용자) : " + inputMessage;
				chatClient.messageSend(inputMessage);
				new Thread() {
					public void run() {
						chatClient.messageListening();
					}
				}.start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("채팅 서버 연결["+SERVER_IP+"] 실패");
		}
		
		chatClient.disConnect();
		System.out.println("채팅 서버 연결["+SERVER_IP+"] 종료");
	}
	
}










