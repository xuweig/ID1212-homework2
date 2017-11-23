package client;
import java.io.IOException;  
import java.net.InetSocketAddress;  
import java.nio.ByteBuffer;  
import java.nio.channels.SelectionKey;  
import java.nio.channels.Selector;  
import java.nio.channels.SocketChannel;  
import java.nio.charset.Charset;  
import java.util.Scanner;  
  
  
public class Client2 {  
  
    private Selector selector = null;  
    static final int port = 1234;  
    private Charset charset = Charset.forName("UTF-8");  
    private SocketChannel sc = null;  
    public void init() throws IOException  
    {  
        selector = Selector.open();   
        sc = SocketChannel.open(new InetSocketAddress("127.0.0.2",port));  
        sc.configureBlocking(false);  
        sc.register(selector, SelectionKey.OP_READ);   
        new Thread(new ClientThread()).start();   
        Scanner scan = new Scanner(System.in);  
        do
        {  
            String line = scan.nextLine();
            if(line.equals("QUIT"))
            {
            	System.out.println("game over");
            	break;
            }
            sc.write(charset.encode(line));  
        }  while(scan.hasNextLine());  
          
    }  
    private class ClientThread implements Runnable  
    {  
        public void run()  
        {  
            try  
            {  
                while(selector.select() > 0)  
                {  
                    for(SelectionKey sk : selector.selectedKeys())  
                    {  
                        selector.selectedKeys().remove(sk);  
                        if(sk.isReadable())  
                        {   
                            SocketChannel sc = (SocketChannel)sk.channel();  
                            ByteBuffer buff = ByteBuffer.allocate(1024);  
                            String content = "";  
                            while(sc.read(buff) > 0)  
                            {  
                                buff.flip();  
                                content += charset.decode(buff);  
                            }  
                            System.out.println(content);  
                            sk.interestOps(SelectionKey.OP_READ);  
                        }  
                    }  
                }  
            }  
            catch (IOException io)  
            {}  
        }  
    }  
      
      
      
    public static void main(String[] args) throws IOException  
    {  
        new Client().init();  
    }  
}  