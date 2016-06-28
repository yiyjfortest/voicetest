package voice_service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import untils.Application;
import voice_client.Dic;
import voice_test.service.LoginInfoSVC;

public class UDPServer extends Thread implements Dic
{
	DatagramSocket ds;
	DatagramPacket dp;
	InetAddress ia;
	Map<String, SocketAddress> userMap = new HashMap<String, SocketAddress>();
	
	UDPServer()
	{
		try
		{
			ia = InetAddress.getLocalHost();
			ds = new DatagramSocket(8989, ia);	
			byte buf[] = new byte[1024];
			dp = new DatagramPacket(buf, buf.length);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void run()
	{
		try
		{
			System.out.println("服务器开启成功，IP地址："+ia.getHostAddress());
			while(true)
			{
				ds.receive(dp);
				String msg = new String(dp.getData(),0,dp.getLength());
				parserMsg(msg);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			ds.close();
		}
	}

	public void parserMsg(String msg)
	{
		if(msg.startsWith(SYS_MSG))
		{
			msg = msg.replaceFirst(SYS_MSG, "");
			if(msg.startsWith(ADD_USER))
			{
				msg = msg.replaceFirst(ADD_USER, "");
				addUser(msg);
			}else if (msg.startsWith(LOGOUT))
			{
				msg = msg.replaceFirst(LOGOUT, "");
				userMap.remove(msg);
				msg = SYS_MSG+LOGOUT+msg;
				sendAll(msg);
			}
		}else if(msg.startsWith(USER_MSG))
		{
			msg = msg.replaceFirst(USER_MSG, "");
				msg = USER_MSG + msg;
				sendAll(msg);
		}else if (msg.startsWith(SEND_FILE_MSG))
		{
			msg = msg.replaceFirst(SEND_FILE_MSG, "");
			String[] str = msg.split("#", 2);
			sendFile(str);
		}else if (msg.startsWith(SEND_VOICE_MSG)) {
			msg = msg.replaceFirst(SEND_VOICE_MSG, "");
			String[] str = msg.split("#", 2);
			sendVoiceMsg(str);
		}else if (msg.startsWith(SEND_VOICE_REQ)) {
			msg = msg.replaceFirst(SEND_VOICE_REQ, "");
			String[] str = msg.split("#", 2);
			sendVoice(str);
		}else if (msg.startsWith(VOICE_START)) {
			voiceOperation(VOICE_START);
		}else if (msg.startsWith(VOICE_STOP)) {
			voiceOperation(VOICE_STOP);
		}else if (msg.startsWith(LOGIN_MSG)) {
			msg = msg.replaceFirst(LOGIN_MSG, "");
			String[] str = msg.split("#", 2);
			loginInfo(str);
		}
	}

	public void loginInfo(String[] str) {
		LoginInfoSVC loginInfo = (LoginInfoSVC) Application.getInstance().getContext().getBean("loginService");
		String userName = str[0];
		String password = str[1];
		boolean b = loginInfo.userLogin(userName, password);
		SocketAddress sa = dp.getSocketAddress();
		send(b+"", sa);
	}

	public void sendVoiceMsg(String[] str) {
		if (userMap.containsKey(str[0]))
		{
			SocketAddress usa = userMap.get(str[0]);
			String msg = SEND_VOICE_MSG + str[1];
			send(msg, usa);
		}
	}

	public void voiceOperation(String operation) {
		for (Map.Entry<String, SocketAddress> entry : userMap.entrySet()) {
			send(operation, entry.getValue());
		}
	}

	public void sendVoice(String[] str) {
		if (userMap.containsKey(str[0]))
		{
			SocketAddress usa = userMap.get(str[0]);
			send(SEND_VOICE_REQ_CON, usa);
		}
	}

	public void addUser(String username)
	{
		String addMsg = SYS_MSG+ADD_USER+username;
		sendAll(addMsg);
		SocketAddress sa = dp.getSocketAddress();
		listAll(sa);
		userMap.put(username, sa);
		System.out.println("新增用户:"+username);
		if (userMap.size() >= 2) {
			String[] strs = new String[userMap.size()];
			SocketAddress[] socketAddresses = new SocketAddress[userMap.size()];
			int i = 0;
			for (Map.Entry<String, SocketAddress> entry : userMap.entrySet()) {
				String getIP = entry.getValue().toString();
				String[] strings = getIP.split(":");
				strs[i] = strings[0].replaceFirst("/", "");
				socketAddresses[i] = entry.getValue();
				i++;
			}
			for (SocketAddress socketAddress : socketAddresses) {
				String msg = SYS_MSG+GET_IP+strs[--i];
				send(msg, socketAddress);
			}
		}
	}

	public void send(String msg, SocketAddress usa)
	{
		try
		{
			byte[] by = msg.getBytes();
			DatagramPacket udp = new DatagramPacket(by, 0, by.length, usa);
			ds.send(udp);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void sendAll(String msg)
	{
		Set<String> unSet = userMap.keySet();
		Iterator<String> iterator = unSet.iterator();
		while(iterator.hasNext())
		{
			SocketAddress usa = userMap.get(iterator.next());
			send(msg, usa);
		}
	}

//	public void sendOne(String name, String msg)
//	{
//		if (userMap.containsKey(name))
//		{
//			SocketAddress usa = userMap.get(name);
//			send(msg, usa);
//		}
//	}

	public void listAll(SocketAddress usa)
	{
		Set<String> unSet = userMap.keySet();
		Iterator<String> iterator = unSet.iterator();
		String msg;
		while(iterator.hasNext())
		{	
			String value = iterator.next();
			msg = SYS_MSG+ONLINE_USER+value;
			send(msg, usa);
		}
	}

	public void sendFile(String str[])
	{
		if (userMap.containsKey(str[0]))
		{
			SocketAddress usa = userMap.get(str[0]);
			String msg = SEND_FILE_MSG + str[1];
			send(msg, usa);
		}
	}


	public static void main(String args[])
	{
		new UDPServer().start();
	}
}