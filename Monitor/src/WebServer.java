import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class server
 */
@WebServlet("/server")
public class WebServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//保存数据库连接
	DBConnector dbc = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WebServer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		try {
			this.dbc = new DBConnector();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			//System.out.println("doPost");
			String usnm = request.getParameter("username");
			String uspwd = request.getParameter("userpwd");
			if(request.getParameter("type").equals("login")){ //登录
				if(dbc.getPassword(usnm) == null)
					response.getWriter().write("User is not exist");
				else if( dbc.getPassword(usnm).equals(uspwd)) //若用户名密码正确，则跳转主页
					response.getWriter().write("success");
				else response.getWriter().write("wrong password");
			}else{//注册
				if(dbc.getPassword(usnm) != null)
					response.getWriter().write("User has existed");
				else
					response.getWriter().write(dbc.register(usnm, uspwd));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
