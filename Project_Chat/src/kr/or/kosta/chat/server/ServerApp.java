package kr.or.kosta.chat.server;

import java.io.IOException;

import kr.or.kosta.chat.server.ChatServer;

public class ServerApp {
	
	public static void main(String[] args) {
		ChatServer chatServer = new ChatServer();
		try {
			chatServer.startUp();
			System.out.println("채팅 서버 시작됨[PORT:"+ChatServer.LISTENING_PORT+"]");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("채팅 서버 구동 실패[PORT:"+ChatServer.LISTENING_PORT+"]");
			return;
		}
		
		chatServer.clientListening();
	}

}
