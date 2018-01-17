package kr.or.kosta.chat.client;

import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;

/**
 * JList Model
 * @author 최명승
 */
public class UserListModel extends AbstractListModel{

	Vector<String> list;
	
	public UserListModel(){
		list = new Vector<String>();
	}
	
	
	@Override
	public int getSize() {
		return list.size();
	}
	

	@Override
	public Object getElementAt(int index) {
		return list.elementAt(index);
	}
	
	/**
	 * 유저 등록
	 */
	public void addUser(String nickName){
		list.addElement(nickName);
		fireContentsChanged(list, 0, list.size());
	}
	
	/**
	 * 유저 리스트 등록
	 */
	public void addUsers(List<String> nickNameList){
		list.clear();
		list.addAll(nickNameList);
		fireContentsChanged(list, 0, list.size());
	}
	
	public void addUsers(String[] nickNameList){
		list.clear();
		for (String nickName : nickNameList) {
			list.addElement(nickName);
		}
		fireContentsChanged(list, 0, list.size());
	}
	
	/**
	 * 닉네임으로 유저 삭제
	 */
	public void removeUser(String nickName){
		list.removeElement(nickName);
		// 뷰, 컨트롤러에 데이터가 변경되었음을 알림
		fireContentsChanged(list, 0, list.size());
	}
	
	/**
	 * 인덱스로 유저 삭제
	 */
	public void removeUser(int index){
		list.removeElementAt(index);
		fireContentsChanged(list, 0, list.size());
	}
	
	/**
	 * 전체 유저 삭제
	 */
	public void removeAll() {
		list.clear();
		fireContentsChanged(list, 0, list.size());
	}
	
}





