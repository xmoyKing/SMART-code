import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;
 







import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
 
//该注解用来指定一个URI，客户端可以通过这个URI来连接到WebSocket。类似Servlet的注解mapping。无需在web.xml中配置。
@ServerEndpoint("/websocket")
public class WebSocket {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    DBConnector dbc = null; //数据库连接对象
    
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<WebSocket>();
     
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    
    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     * @throws Exception 
     */
    @OnOpen
    public void onOpen(Session session) throws Exception{
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        //System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        
        //创建数据库连接
        try {
			this.dbc = new DBConnector();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Timer timer = new Timer();
        //延迟0s，同时每1秒调用一次,此处必须使用new TimerTask()，同时重写run方法
        timer.schedule(new TimerTask(){
        	public void run(){
        		for(WebSocket item: webSocketSet){             
                    try {
                        item.sendMessage(dbc.getDevice());
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
        	}
        }, 0, 1000);
        
    }
     
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1    
        //System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }
     
    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
    }
     
    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("发生错误");
        error.printStackTrace();
    }
     
    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException{
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }
 
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
 
    public static synchronized void addOnlineCount() {
        WebSocket.onlineCount++;
    }
     
    public static synchronized void subOnlineCount() {
        WebSocket.onlineCount--;
    }
}