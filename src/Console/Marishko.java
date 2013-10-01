package Console;

import java.awt.Color;

import java.awt.Container;
import java.awt.List;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;



import java.util.Collection;


import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JTextArea;

import javax.swing.JLabel;

import javax.swing.JPanel;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;

import com.skype.Chat;
import com.skype.SkypeException;





public class Marishko extends JApplet{

	/**
	 * @param args
	 */
	
	static TextArea ChatLog; 
	static JPanel ctrlpanel;
	
	
	static Marishko instance=null;
	static List BudyList = null;
	
	TextField usernamef;
	
	
	
	public static Marishko get_Instance()
	{		
		if (instance == null)
		{
			instance =  new Marishko();
		}		
		return instance;		
	}
	
	
	public void initCtrlPanel(Container c)
	{
		ctrlpanel = new JPanel();
		ctrlpanel.setLayout(null);
		
		ctrlpanel.setBounds(600,0, 300, 600);
		ctrlpanel.setBackground(Color.decode("0xfafaea"));
		
		 usernamef = new TextField(10);		
		 usernamef.setBounds(10, 20, 90, 20);
		 
		 JLabel label1 = new JLabel("Пользователь:");
		 label1.setBounds(10, 0, 250, 20);
		 
		 JButton butt1 = new JButton("h");		 
		 butt1.setBounds(100, 20, 50, 20);
		 
		 butt1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				allowUserToChat(usernamef.getText());
				sayHello(usernamef.getText());
				
			}

			
		});
		 
		 JButton butt2 = new JButton("s");		 
		 butt2.setBounds(150, 20, 50, 20);
		 
		 butt2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
											
				saySomething( allowUserToChat(usernamef.getText()) );
				
			}

			
		});
		 
		 JLabel label2 = new JLabel("Контакты");
		 label2.setBounds(10, 40, 250, 20);
		 
		 BudyList = new List();
		 BudyList.setBounds(10, 60, 190, 200);
		 
		 BudyList.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				usernamef.setText( BudyList.getItem(Integer.parseInt(e.getItem().toString())));
				
			}
		});
				 
		
		ctrlpanel.add(label1);
		ctrlpanel.add(butt1);
		ctrlpanel.add(butt2);
		ctrlpanel.add(label2);
		ctrlpanel.add(BudyList);
		 
		ctrlpanel.add(usernamef);
		
		
		
		
		c.add(ctrlpanel);
	}
	
	protected MUser allowUserToChat(String username) {

		MUser u = UserMgr.getUserByName(username);
		u.isAllow=true;
		DBManager.allowUserToChat(u);
		return(u);
	}


	public void init()
	{
	
		instance = this;
		
		
	
		
		
		Container c = this.getContentPane ();
		
		c.setLayout(null);
		
		ChatLog = new TextArea();
		ChatLog.setBounds(0,0,600, 600);
		ChatLog.setBackground(Color.decode("0xfafafa"));
		c.add(ChatLog); 
		
		initCtrlPanel(c);
		
		
		setSize(800,600);
		
		
		
		Marishko bot = Marishko.get_Instance();
		
			
		DBConnect();
		//SystemLog("Mask updated:" + Integer.toString(DBManager.RecalcAllMask()) );
		//JabberConnect();
		initSkype();
		
		
		
		
		
		
		
	}
	
	public Marishko()
	{
			
	}
	
	static void initSkype()
	{
		
		try {
			SkypeMgr.init();
			SkypeMgr.loadFriendsList(BudyList);
			
		} catch (SkypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	static void DBConnect()
	{
		if (DBManager.connection == null)
			DBManager.Connect();
	}
	
	static void processMessage(String Author, MaMessage msg)
	{
		MUser u = UserMgr.getUserByName(Author);
		processMessage(u, msg);
	}
	
	

	public static void processChat(MUser u, MaMessage msg) {
		// TODO Auto-generated method stub
	MaMessage ans = DBManager.getAnswer(msg, u, true);
		//MaMessage ans = WordMgr.getAnswer(u,msg);
		
		if (ans!=null)
		sendMessage(u, ans);
	}
	
	static void processMessage(MUser Author, MaMessage msg)
	{		
		MaMessage Answer;
		
		//--output
		Log("["+Author.Login + "] user:" + msg.getBody());
		

		if (Author.wait_time!=0) // Прекратить придумывать случайную фразу, пользователь уже ответил.
			Author.stopWait4Something();
		
		//--Process message in db
		WordMgr.processUserMessage(Author, msg);
				
		//---Answer
		Answer = WordMgr.getAnswer(Author,msg);
		
		if (Answer!=null)
		sendMessage(Author, Answer);
	}
	
	static void sendMessage(MUser to, MaMessage Msg)
	{
	sendMessage(to.Login, Msg.getBody());	
	}
	
	static void sendMessage(String to, String Msg)
	{
		MUser u = UserMgr.getUserByName(to);
		if (u.isAllow==false) return;
		
		//JabberMgr.sendMessage(u, Msg);
		Log("["+to+"] Bot :" + Msg);
		SkypeMgr.sendMessage(u, Msg);
		
	}
	
	static void SystemLog(String txt)
	{
		Log("System: "+txt);
		System.out.println(txt);
	}
	
	static void Log(String txt)
	{
		
		ChatLog.append(txt+"\n\r");	
		
	}
	
	public static void main(String[] args) {
	
	

		System.out.println("stop");
	}
	
	
	
	
	public static void sayHello(String to)
	{
		MUser u = UserMgr.getUserByName(to);
		MaMessage msg = WordMgr.getHello(u);
		
		
		sendMessage(to, msg.getBody());
	}

	public static void saySomething(MUser u) {
		MaMessage msg = WordMgr.getSomething(u);
		
		sendMessage(u, msg);
		
		
	}
	
	static public void SelfOrganization()
	{
	/*
	 * Процедура регулярной самоорганизации включает в себя:
	 * 1) Усиливание и ослабление связей между фразами;
	 * 2) Определение ценных диалогов и проставление тэгов.
	 * 
	 * */	
	}


	public static void See2Learn(MUser u, MaMessage msg) {
		// TODO Auto-generated method stub
		WordMgr.See2Learn(u, msg);
	}



}
