package kr.or.kosta.chat.client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import kr.or.kosta.chat.common.Message;

/**
 * 채팅 화면
 * 
 * @author 최명승
 */
public class ChatFrame extends JFrame {
	
	ChatClient chatClient;
	MessageListener messageListener;
	String nickName;
	
	
	JPanel northPanel, southPanel;
	JLabel nickNameL;
	JTextField nickNameTF, inputTF;
	JButton connectB, sendB;
	JTextArea messageTA;
	JList<String> userList;
	UserListModel userListModel;
	
	JComboBox<String> userCombobox;
	DefaultComboBoxModel<String> comboboxModel;
	
	boolean connected = false;
	
	public ChatFrame() {
		this("즐거운 대화 나누세요");
	}
	
	public ChatFrame(String title) {
		super(title);
		northPanel = new JPanel();
		southPanel = new JPanel();
		nickNameL = new JLabel("대화명", JLabel.CENTER);
		nickNameTF = new JTextField();
		inputTF = new JTextField();
		connectB = new JButton(" 연결 ");
		sendB = new JButton(" 전송 ");
		
		messageTA = new JTextArea();
		messageTA.setEditable(false);
		messageTA.setBackground(new Color(253, 254, 223));
		userListModel = new UserListModel();
		userList = new JList<String>(userListModel);
		
		comboboxModel = new DefaultComboBoxModel<String>();
		comboboxModel.addElement("전체에게");
		userCombobox = new JComboBox<String>(comboboxModel);
	}
	
	/**
	 * 프레임에 비주얼 컴포넌트 배치
	 */
	public void setContents() {
		 
//		패널 배치
		northPanel.setLayout(new BorderLayout());
		northPanel.add(nickNameL, BorderLayout.WEST);
		northPanel.add(nickNameTF, BorderLayout.CENTER);
		northPanel.add(connectB, BorderLayout.EAST);
		
		southPanel.setLayout(new BorderLayout());
		southPanel.add(userCombobox, BorderLayout.WEST);
		southPanel.add(inputTF, BorderLayout.CENTER);
		southPanel.add(sendB, BorderLayout.EAST);
		
//		프레임 배치
		add(northPanel, BorderLayout.NORTH);
		add(new JScrollPane(messageTA), BorderLayout.CENTER);
		JScrollPane sp = new JScrollPane(userList);
		sp.setPreferredSize(new Dimension(100, sp.getHeight()));
		add(sp, BorderLayout.EAST);
		add(southPanel, BorderLayout.SOUTH);
	}
	
	/** 종료 */
	public void exit() {
		setVisible(false);
		dispose();
		System.exit(0);
	}
	
	/** 서버 연결 */
	public void connect() {
		nickName = nickNameTF.getText();
		// 유효성 검증
		if(isEmpty(nickName)) {
			JOptionPane.showMessageDialog(this, "대화명을 입력하여 주세요.", "입력 에러", JOptionPane.ERROR_MESSAGE);
			nickNameTF.requestFocus();
			return;
		}
		
		chatClient = new ChatClient();
		
		try {
			
			chatClient.connect();
			setTitle(nickName + "님이 입장하셨습니다.");
			connectB.setText(" 연결 종료 ");
			nickNameTF.setEnabled(false);
			nickNameTF.setEditable(false);
			connected = true;
			
			receiveMessage();
			
			chatClient.messageSend(Message.CONNECT + Message.DELIMITER + nickName);
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "메신저 서버에 연결할 수 없습니다.\n"+e.toString(), "서버 연결 실패", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/** 서버 연결 해제*/
	public void disConnect() {
		chatClient.messageSend(Message.DISCONNECT + Message.DELIMITER + nickName);
	}
	
	public void postDisConnect() {
		chatClient.disConnect();
		setTitle("즐거운 대화 나누세요");
		connectB.setText(" 연결 ");
		nickNameTF.setEnabled(true);
		nickNameTF.setEditable(true);
		nickNameTF.setText("");
		nickNameTF.requestFocus();
		messageTA.setText("");
		userListModel.removeAll();
		connected = false;
		System.out.println("서버 연결 종료됨");
	}
	
	/** 서버에 메시지 전송 */
	public void sendMessage() {
		String message = inputTF.getText();
		// 유효성 검증
		if(isEmpty(message))  return;
		
		String targetUser = comboboxModel.getElementAt(userCombobox.getSelectedIndex());
		if("전체에게".equals(targetUser)) {
			chatClient.messageSend(Message.MULTICHAT + Message.DELIMITER + nickName + Message.DELIMITER + message);
		}else {
			// 귓속말
			chatClient.messageSend(Message.WHISPER + Message.DELIMITER + nickName + Message.DELIMITER + targetUser + Message.DELIMITER + message);
		}
		inputTF.setText("");
		inputTF.requestFocus();
	}
	
	/** 서버로부터 메시지 수신 */
	public void receiveMessage() {
		messageListener = new MessageListener(this, chatClient.getIn());
		messageListener.start();
	}
	
	
	/** 닉네임 선택 */
	public void selectUser() {
		messageTA.append(userList.getSelectedValue()+"님을 선택하였습니다..\n");
	}
	
	/** 메시지창에 메시지 출력 */
	public void appendMessage(String message) {
		messageTA.append(message + "\n");		
	}
	
	/** 사용자 목록 추가 */
	public void appendUsers(String[] users){
		userListModel.addUsers(users);
		for (String user : users) {
			userCombobox.addItem(user);
		}
		
	}
	
	/** 사용자 추가 */
	public void appendUser(String user){
		userListModel.addUser(user);
		userCombobox.addItem(user);
	}
	
	/** 유저 제거 */
	public void removeUser(String user){
		userListModel.removeUser(user);
	}
	
	private boolean isEmpty(String string) {
		if(string == null || string.trim().length() == 0) {
			return true;			
		}
		return false;
	}
	
	/**
	 * 이벤트소스에 이벤트 처리
	 */
	public void eventRegist() {	
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(!connected) {
					exit();
				}
			}
		});
		
		connectB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(connectB.getText().trim().equals("연결")){
					connect();
				}else {
					disConnect();
				}
			}
		});
		
		nickNameTF.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		
		
		sendB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		
		inputTF.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		
		userList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					selectUser();
				}
			}
		});
		
	}

}






