package server;
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.*;
import java.nio.ByteBuffer; 


public class Server
{
	private static ServerSocketChannel serverSocketChannel;
	private static final int PORT = 1234;
	private static Selector selector;
	private static Charset charset = Charset.forName("UTF-8"); 
	
	public static void main(String[] args)
	{	
		ServerSocket serverSocket;
		System.out.println("Opening port¡­\n");
		try
		{
			serverSocketChannel =
			ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocket = serverSocketChannel.socket();

			InetSocketAddress netAddress =
			new InetSocketAddress(PORT);
			serverSocket.bind(netAddress);
			selector = Selector.open();
			serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
			System.exit(1);
		}
		processConnections();
	}
	private static void processConnections()
	{
		String[] process = new String[20];
		int[] hashcode=new int[20];
		int i=0;
		do
		{
			try
			{
				int numKeys = selector.select();
				if (numKeys > 0)
				{
					Set<SelectionKey> eventKeys =
					selector.selectedKeys();
					Iterator<SelectionKey> keyCycler =
					eventKeys.iterator();
					while (keyCycler.hasNext())
					{
						SelectionKey key =
						(SelectionKey)keyCycler.next();
						keyCycler.remove();
						if (key.isAcceptable())
						{
							SocketChannel socketChannel = serverSocketChannel.accept();
							hashcode[i] = socketChannel.hashCode();
							process[i]=acceptConnection(key,socketChannel);								
							i++;
							continue;
						}
						if (key.isReadable())
						{
							SocketChannel socketChannel = (SocketChannel)key.channel();
							int k;
							lable:
							for(k=0;k<20;k++)
							{

								if (hashcode[k]==socketChannel.hashCode())
								{
									break lable;
								}
								
							}
							process[k]=acceptData(key,process[k],socketChannel);
							continue;
						}
					}
				}
			}
			catch(IOException ioEx)
			{
				ioEx.printStackTrace();
				System.exit(1);
			}
		}while (true);
	}
	private static String acceptConnection(SelectionKey key,SocketChannel socketChannel) throws IOException
	{
		Socket socket;
		
		socketChannel.configureBlocking(false);
		socket = socketChannel.socket();
		System.out.println("Connection on "+ socket + ".");
		String word=choseword();
		String message="";
		for(int m=0;m<word.length();m++)
		{
			message=message+"_ ";
		}
		socketChannel.write(charset.encode(message+50)); 
		ByteBuffer buffer = ByteBuffer.allocate(2048);
		buffer.flip();
		while (buffer.remaining()>0)
		socketChannel.write(buffer);
		socketChannel.register(selector,SelectionKey.OP_READ);
		selector.selectedKeys().remove(key);
		return word+message+(char)word.length()+(char)50;
	}
	
	private static String acceptData(SelectionKey key,String s,SocketChannel socketChannel)throws IOException
	{
		String result="";
		int p,q;
		int i=0;
		i=(s.length()-2)/3;
		System.out.println(s);
		char a;
		int point=0;
		a=s.charAt(3*i);
		point=(int)s.charAt(3*i+1);
		int b=(int)a;
		String word=""; 
		word=s.substring(0,i);
		System.out.println(word);
		ByteBuffer buffer = ByteBuffer.allocate(2048);
		buffer.clear();
		socketChannel.read(buffer);
		String content = "";  
		if(b!=0)
		{

	       // while(socketChannel.read(buffer) > 0)  
	        {  
	            buffer.flip();  
	            content += charset.decode(buffer); 
	        }
	        if(content.length()==1)
	        {
	        	char m=content.charAt(0);
	        	p= word.indexOf(m);
	        	if(p==-1)
	        	{
	        		b=b-1;
	        		s=s.substring(0,s.length()-2)+(char)(b)+(char)point;
	        	}
	        	else
	        	{
	        		labelc:
	        		for(int o=100;o>0;o--)
	        		{
	        			q=word.indexOf(m,p+1);
	        			String l="";
	        			for(int k=0;k<s.length();k++)
	        			{
	        				if(k!=word.length()+2*p)
	        				{
	        					l=l+s.charAt(k);
	        				}
	        				else
	        				{
	        					l=l+m;
	        				}
	        				
	        			}
	        			s=l;
	        			if(q==-1)
	        			{
	        				break labelc;
	        			}
	        			else
	        			{
	        				p=q;
	        			}
	        		}
	        	}
	        }
	        else
	        {
	        	int f=content.compareTo(word);
	        	if(f==0)
	        	{
	        		result="That's right.";
	        		point=point+1;
	        		word=choseword();
	    			String message="";
	    			for(int m=0;m<word.length();m++)
	    			{
	    				message=message+"_ ";
	    			}
	    			s=word+message+(char)word.length()+(char)point;
	        	}
	        	else
	        	{
	        		b=b-1;
	        		s=s.substring(0,s.length()-2)+(char)(b)+(char)point;
	        	}
	        }
		}
		else
		{
			result="no more chance.";
			point=point-1;
			word=choseword();
			String message="";
			for(int m=0;m<word.length();m++)
			{
				message=message+"_ ";
			}
			s=word+message+(char)word.length()+(char)point;
		}
	        String output=result+s.substring(((s.length()-2)/3),s.length()-3)+point;
	        
			
			socketChannel.write(charset.encode(output)); 
				selector.selectedKeys().remove(key);
				return s;
			}

		

	
	private static String choseword()
	{
		String[]Words=new String[51528];
		File file =new File("D://courses//network programming//words.txt");
		//route may have to change in other hosts.
		FileReader reader = null;
		try {
			reader = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int fileLen= (int)file.length();
		char[] chars=new char[fileLen];
		try {
			reader.read(chars);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String txt=String.valueOf(chars);
		Words=txt.split("\n");
		int picknumber;
		picknumber=(int)(Math.random()*51528);
		String pick;
		pick=Words[picknumber]; //Pick a random word from list.
		System.out.println(pick); 
		return pick;
	}
}		
