import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
//http://blog.csdn.net/zhangyiacm/article/details/49488721
public class Client {
	
	public static void main(String[] args) throws Exception, IOException {
		
		 String host = "127.0.0.1";
		 int port = 9999; // 端口号
		 int count = 0; //记录发送了多少次
		 Socket client = new Socket(host, port);// 定义socket;

		while (true) {
			Writer wt = new OutputStreamWriter(client.getOutputStream()); // 定义输出流
			int z1 = (new Random()).nextInt(2); // 1为启动，0为关机,使用随机生成
			int z2 = 0; // 0表示正常，其他表示不正常
			if (z1 == 1)
				z2 = (new Random()).nextInt(4) + 1; // 生成1-4之内的随机数
			else
				z2 = 0;
			String str = "#mj" + args[0] + "," + z1 + "," + z2 + ",000001,u";
			count++;
			System.out.println(count+" : "+str);
			
			wt.write(str); // #mj2011102701,1,0,000000,u
			wt.flush();

			try {
				Thread.sleep(1000 * 3);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break; //若定时器发生问题则跳出循环
			}
		}
		client.close();
	}
}
