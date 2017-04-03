import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class SocketServer {
	private static final int port = 9999; // 监听端口
	private Selector selector = null; // 定义一个selector监听所有的channel
	private Charset charset = Charset.forName("UTF-8"); // 定义字符格式
	private DBConnector dbc;

	public void init() throws Exception {
		dbc = new DBConnector(); // 新建数据库连接
		selector = Selector.open();

		ServerSocketChannel svCnl = ServerSocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress("127.0.0.1", port);
		svCnl.bind(isa);
		svCnl.configureBlocking(false);
		svCnl.register(selector, SelectionKey.OP_ACCEPT);// 将svCnl放入selector监听
		System.out.println("服务器接开始循环接收");

		Map<SelectionKey, StringBuilder> map = new HashMap<SelectionKey, StringBuilder>(); // 建立一个SK和String的map几个用于客户端上次的信息

		while (selector.select() > 0) {

			for (SelectionKey sk : selector.selectedKeys()) {
				selector.selectedKeys().remove(sk);

				if (sk.isAcceptable()) {
					SocketChannel sc = svCnl.accept();
					sc.configureBlocking(false);
					sc.register(selector, SelectionKey.OP_READ);
				}
				if (sk.isReadable()) {
					SocketChannel sc = (SocketChannel) sk.channel();

					ByteBuffer bb = ByteBuffer.allocate(1024);
					StringBuilder content = new StringBuilder();

					try { // 利用try catch捕获read异常（某个客户端退出）

						if (sc.read(bb) > 0) {
							bb.flip(); // 防止回绕
							content.append(charset.decode(bb));

							// 关于java.nio.ByteBuffer;
							// http://blog.csdn.net/zhoujiaxq/article/details/22822289
							System.out.println("服务器接收到：" + content);

							// 将信息存入数据库
							String deviceId = content.substring(3, 13);
							String deviceState = content.substring(14, 15);
							String state = content.substring(16, 17);
							String groupId = content.substring(18, 24);
//							dbc.insertDevice(deviceId, deviceState, state, groupId);

							map.put(sk, content);// 将对应的content和sk添加去map中,自动覆盖
							sk.interestOps(SelectionKey.OP_READ);// 是sk对应的channel为准备下次读取
							
						}

					} catch (Exception e) {
						System.out.println("客户端主动关闭时关闭该客户端");
						// 表示客户端断开连接 ，参考如下博客
						// http://blog.csdn.net/cao478208248/article/details/41648513
						// http://www.oschina.net/question/558872_168070
						content = map.get(sk);// 从map中找到需要关闭的sk以及其对应的content;
						
						String deviceId = content.substring(3, 13);
						String deviceState = "0"; // 更新为离线状态
						String state = "0";
						String groupId = content.substring(18, 24);
						dbc.insertDevice(deviceId, deviceState, state, groupId);

						System.out.println("客户端主动关闭，写入数据库为离线状态完成"+content);
						sc.close(); //关闭SocketChannel
						sk.cancel(); //取消该SelectionKey.
					}
				}
			}
		}
	}
/*//IO多路复用，当开启多个客户端的时候有的时候会在for (SelectionKey sk : selector.selectedKeys())处出问题，
	public static void main(String[] args) throws Exception, IOException {
		new SocketServer().init();
	}*/
}
