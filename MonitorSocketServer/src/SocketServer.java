import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class SocketServer {
	private static final int port = 9999; // 监听端口
	private Selector selector = null; // 定义一个selector监听所有的channel
	public Charset charset = Charset.forName("UTF-8");

	public void init() throws Exception {
		selector = Selector.open();

		ServerSocketChannel svCnl = ServerSocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress("127.0.0.1", port);

		svCnl.bind(isa);
		svCnl.configureBlocking(false);
		svCnl.register(selector, SelectionKey.OP_ACCEPT);// 将svCnl放入selector监听
		System.out.println("服务器接开始循环接收");
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

					try {
						while (sc.read(bb) > 0) {
							bb.flip(); // 防止回绕
							content.append(charset.decode(bb));
						}

						// 关于java.nio.ByteBuffer;
						// http://blog.csdn.net/zhoujiaxq/article/details/22822289
						System.out.println("服务器接收到：" + content);

						sk.interestOps(SelectionKey.OP_READ);// 是sk对应的channel为准备下次读取

					} catch (Exception e) {
						System.out.println("客户端主动关闭时关闭该客户端");
						sc.close();
						sk.cancel();
					}
				}
			}
		}
	}

	public static void main(String[] args) throws Exception, IOException {
		new SocketServer().init();
	}
}
