package kr.or.kosta.chat.client;

import javax.swing.JFrame;

public class ClientApp {

	public static void main(String[] args) {
		ChatFrame frame = new ChatFrame();
		frame.setContents();
		frame.setSize(400, 450);
		GUIUtil.setCenterScreen(frame);
		GUIUtil.setLookNFeel(frame, GUIUtil.STYLE_NIMBUS);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		frame.eventRegist();
	}

}
