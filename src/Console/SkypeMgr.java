package Console;

import java.awt.List;
import java.util.Date;
import java.util.Vector;

import com.skype.Chat;
import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.Friend;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.User;

public class SkypeMgr{

	 
	

	public static void init() throws SkypeException
	{

		
		  Skype.setDeamon(false); // to prevent exiting from this program
	        Skype.addChatMessageListener(new ChatMessageAdapter() {
	        	
	        	public void chatMessageSent (ChatMessage received) throws SkypeException {
	        		
	        		 if (received.getType().equals(ChatMessage.Type.SAID)) {
		                   
		                	
		                	int MembersCnt = getRealActiveMembers(received.getChat()).size();
		                	processMesage(received, MembersCnt);
		                	
				        	
		                	
		                }
	        	}
	        	
	            public void chatMessageReceived(ChatMessage received) throws SkypeException {
	                if (received.getType().equals(ChatMessage.Type.SAID)) {
	                   
	                	
	                	int MembersCnt = getRealActiveMembers(received.getChat()).size();
	                	processMesage(received, MembersCnt);
	                	
			        	
	                	
	                }
	            }

				private Vector<String> getRealActiveMembers(Chat chat) throws SkypeException {
					// TODO Определяет реальное количество пользователей по времени последних сообщений
					int cnt = 1; 
				Long dt = System.currentTimeMillis();
				Long curtime = System.currentTimeMillis();
				ChatMessage[] CM = chat.getAllChatMessages();
				ChatMessage msg;
				Vector<String> users = new Vector<String>();
				boolean found=false;
				
				users.add(Config.botname);
				
					if (CM.length==0) return users;
					
					int i=0;
					while ((dt>(curtime-Config.inChatTime)) && (i<CM.length))
					{
						
						msg = CM[i];
						
						
						dt=msg.getTime().getTime();

						found=false;
						for (String u : users)
						{
							if (msg.getSender().getId().equals(u))
							{
								found=true;
								break;
							}											
						}
						
						if ((!found) && (dt>(curtime-Config.inChatTime)))
							{
							users.add(msg.getSender().getId());
							cnt++;
							}
					i++;	
					}
				
					return users;
				}

				private void processMesage(ChatMessage received, int FriendsCnt) throws SkypeException {
					// TODO Auto-generated method stub
					

		        	MaMessage msg = new MaMessage(received.getContent());
		        	msg = DBManager.loadMessageAttr(msg);
		        	
		        	String UserName = received.getSender().getId();
		        	
		        	MUser u = UserMgr.getUserByName(UserName);
		        			
		        	msg.owner = u;
		        	
		        	if (u.isAllow==false) return;

		        	u.Chat = received.getChat();
		        	
		        	
		        	// Если пользователь досылает сообщения, объединять их в одно и выставлять признак
		        	msg = WordMgr.AppendIfItNeed(u, msg);
		    		
		        	
		        	//Если в чате один юзер, значит он общается с ботом
		        	if ((FriendsCnt==2) && (u.Chat.getAllActiveMembers().length==2)) 
		        	{	        	
		        		if (u.LinkedUser!=null) u.LinkedUser=null;		        			
			        	Marishko.processMessage(u, msg);
		        			
		        	}
		        	else
		        	if (FriendsCnt==3) //Наблюдаем за общением Пользователь - Пользователь
		        	{
		        		//Marishko.SystemLog("ChatMode: User vs User");
		        		
		        		if (u.LinkedUser==null)
		        		UserMgr.linkUsers(getRealActiveMembers(received.getChat()));
		        		
		        		if (u.LinkedUser!=null)
		        		Marishko.See2Learn(u, msg);
		        	}
		        	else //Если в чате несколько юзеров, значит они общаются между собой
		        	if (!Config.OnlyStudyMode) 
		        	{
		        		if (u.LinkedUser!=null) u.LinkedUser=null;
		        		
		        		//Marishko.SystemLog("ChatMode: Bot vs User's");
		        		
		        		Marishko.processChat(u, msg);
		        	}
					
				}
	        });	
	        
	        
	      //  AuthorizeAll();
		
	}

	private static void AuthorizeAll() throws SkypeException {
		// TODO Auto-generated method stub
		for (Friend fr : Skype.getContactList().getAllUserWaitingForAuthorization())
		{
			fr.setAuthorized(true);
		}
	
	}

	public static void loadFriendsList(List budyList) throws SkypeException {
		// TODO Auto-generated method stub
		

			for (Friend fr : Skype.getContactList().getAllFriends())
			{
				budyList.add(fr.getId());
			}
		
			
		
	}

	public static void sendMessage(MUser u, String msg) {
		// TODO Auto-generated method stub
		 try {
			//Skype.chat(u.Login).send(msg);
			 
			 if (u.Chat==null)
			u.Chat = Skype.chat(u.Login);	 
				 
			 u.Chat.send(msg);
			
			
			
		} catch (SkypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	 

}
