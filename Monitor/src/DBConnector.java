import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetMetaData;

public class DBConnector {
	String db_url = "jdbc:mysql://localhost:3306/monitor";
	String db_user = "root";
	String db_pwd = "hello";
	
	public DBConnector() throws ClassNotFoundException{
		Class.forName("com.mysql.jdbc.Driver");
	}
	
	public Connection getConnection() throws SQLException{
		return (Connection) java.sql.DriverManager.getConnection(db_url, db_user, db_pwd);
	}
	
	public void db_close(Connection con, PreparedStatement prepStmt, ResultSet rs) throws Exception{
		if(rs != null) rs.close();
		if(prepStmt != null) prepStmt.close();
		if(con != null) con.close();
	}
	
	public String rsToJson(ResultSet rs) throws Exception{
		JSONArray array = new JSONArray();
		ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
		int colCount = metaData.getColumnCount(); //获取列数
		
		while(rs.next()){//遍历result Set中的每条数据
			JSONObject jsObj = new JSONObject();
			for(int i = 1; i <= colCount; ++i){//遍历每一列
				String colName = metaData.getColumnLabel(i);
				String val = rs.getString(colName);
				jsObj.put(colName, val);
			}
			array.put(jsObj);
		}		
		//db_close(con, prepStmt, rs); //关闭连接
		return array.toString();
	}
	
	public String getPassword(String name) throws Exception{
		Connection con = getConnection();
		String sql = "select * from monitor_user where user_Name=?";
		PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(sql);
		prepStmt.setString(1, name);//用name参数代替第一个？
		ResultSet rs = prepStmt.executeQuery(); //执行sql语句
		String pwd = null;
		if(rs.next()) pwd = rs.getString(2); //获取第二个字段，即密码
		db_close(con, prepStmt, rs); //关闭连接
		return pwd;
	}
	
	public String getDevice() throws Exception{
		Connection con = getConnection();
		String sql = "select * from monitor_device";
		PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(sql);
		ResultSet rs = prepStmt.executeQuery(); //执行sql语句
		
		//转化为json输出
		JSONArray array = new JSONArray();
		ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
		int colCount = metaData.getColumnCount(); //获取列数
		
		while(rs.next()){//遍历result Set中的每条数据
			JSONObject jsObj = new JSONObject();
			for(int i = 1; i <= colCount; ++i){//遍历每一列
				String colName = metaData.getColumnLabel(i);
				String val = rs.getString(colName);
				jsObj.put(colName, val);
			}
			array.put(jsObj);
		}
		
		db_close(con, prepStmt, rs); //关闭连接
		return array.toString();
	}
	
	public String register(String name, String pwd) throws Exception{
		Connection con = getConnection();
		String sql = "insert into monitor_user(user_Name,user_Password) values(?,?)";
		PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(sql);
		prepStmt.setString(1, name);//用name参数代替第一个？
		prepStmt.setString(2, pwd);//用pwd参数代替第二个？
		
		int rs = prepStmt.executeUpdate(); //执行sql语句
		String rt = "fail";
		if(rs > 0) rt = "success";
		con.close(); //关闭连接
		prepStmt.close();
		//db_close(con, prepStmt, rs); //关闭连接
		return rt;
	}
	
	public String insertDevice(String deviceId, String deviceState, String state, String groupId) throws Exception{
		//System.out.println(deviceId +","+ deviceState +","+  state +","+  groupId);
		
		Connection con = getConnection();
		String sql = "select * from monitor_device where device_Id="+deviceId;
		PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(sql);
		ResultSet rs = prepStmt.executeQuery(); //执行sql语句
		if(rs.next()){ //说明存在该设备，则进行更新操作
			sql = "UPDATE monitor_device set device_State="+deviceState+", state="+state+" where device_Id="+deviceId;
			prepStmt = (PreparedStatement) con.prepareStatement(sql);
		}else{
			sql = "insert into monitor_device(device_Id,device_State,state ,group_Id) values(?,?,?,?)";
			prepStmt = (PreparedStatement) con.prepareStatement(sql);
			prepStmt.setString(1, deviceId);//用name参数代替第一个？
			prepStmt.setString(2, deviceState);//用pwd参数代替第一个？
			prepStmt.setString(3, state);//用pwd参数代替第一个？
			prepStmt.setString(4, groupId);//用pwd参数代替第一个？
		}
		
		String rt = "fail";
		if(prepStmt.executeUpdate() > 0) rt = "success"; //执行插入sql语句 返回一个int类型的值
		con.close(); //关闭连接
		prepStmt.close();
		//db_close(con, prepStmt, rs); //关闭连接
		return rt;
	}
}
