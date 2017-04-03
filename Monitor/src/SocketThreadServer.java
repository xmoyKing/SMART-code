import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

//采用多线程接收处理客户端
public class SocketThreadServer extends Thread{
	private Socket client;
	private DBConnector dbc;
	public SocketThreadServer(Socket c) throws Exception{
		this.dbc = new DBConnector(); // 新建数据库连接
		this.client = c;
	}
	
	public void run(){
		String backup = "0",content = "",deviceId = "",deviceState = "",state,groupId = "000001";
		try{
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));  
            // Mutil User but can't parallel  
            while (true) {
            	char[] s = new char[27];
                int rt = in.read(s);
                if(rt == -1) break;
                content = new String(s);
                System.out.println("Server："+content);  
				state = content.substring(16, 17);
                if( backup.equals(state))//若本次状态与上次有变化则写入数据库
                	continue; //否则跳过继续下一次循环
            
                backup = state;//将当前的状态保存起来
				// 将信息存入数据库
				deviceId = content.substring(3, 13);
				deviceState = content.substring(14, 15);
				dbc.insertDevice(deviceId, deviceState, state, groupId);
            }
		}catch(Exception e){
			System.out.println("出错啦~");
		} finally {
			deviceState = "0"; // 更新为离线状态
			state = "0";
			try {
				dbc.insertDevice(deviceId, deviceState, state, groupId);
				System.out.println("客户端主动关闭，写入数据库为离线状态完成"+content);
	            client.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } 
	}
    @SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(9999);  
        while (true) {
        	System.out.println("Socket Thread Server 接收新的客户端");  
        	SocketThreadServer mc = new SocketThreadServer(server.accept());  
            mc.start();
        }  
    }  
}
