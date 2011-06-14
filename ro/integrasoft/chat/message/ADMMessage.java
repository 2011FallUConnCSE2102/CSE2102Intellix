
package ro.integrasoft.chat.message;

public class ADMMessage extends Message {

	public static final int ADM_Get_Users = 1;
	public static final int ADM_Get_Groups = 2;
	public static final int ADM_Get_Rights = 3;
	public static final int ADM_Add_User = 4;
	public static final int ADM_Add_Group = 5;
	public static final int ADM_Del_Group = 6;
	public static final int ADM_Del_User = 7;

	public static final int ADM_Users = 101;
	public static final int ADM_Groups = 102;
	public static final int ADM_Rights = 103;

	public static final int ADM_Change_User_Prop = 201;
	public static final int ADM_Change_Group_Prop = 202;

	private int msgId;

	public ADMMessage(int msgId, Object o) {
		super(o);
		setType("admin");
		this.msgId = msgId;
	}

	public int getMsgId() {
		return msgId;
	}

	public void setMsgId(int msg) {
		msgId = msg;
	}
}