package com.easyooo.framework.support.mybatis.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Jdbc Utils
 * @author Killer
 */
public final class JdbcUtil {
	
	public Integer counting(Connection conn, String sql, ParameterSetter setter)
			throws SQLException {
		List<Object[]> dataList = query(conn, sql, setter);
		if(dataList != null && dataList.size() > 0){
			// Extract the first row and first column data 
			Object[] rowData = dataList.get(0);
			if(rowData != null && rowData.length == 1){
				return Integer.valueOf(String.valueOf(rowData[0]));
			}
		}
		return null;
	}

	public List<Object[]> query(Connection conn, String sql,
			ParameterSetter setter) throws SQLException{
		if(conn.isClosed()){
			return null;
		}
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			ps = conn.prepareStatement(sql);
			setter.setParameters(ps);
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = ps.getMetaData();
			return extractData(rsmd, rs);
		}finally{
			
			if(rs != null){
				rs.close();
			}
			
			if(ps != null){
				ps.close();
			}
		}
	}
	
	
	private List<Object[]> extractData(ResultSetMetaData rsmd, ResultSet rs)throws SQLException{
		List<Object[]> data = new ArrayList<Object[]>();
		int columnCount = rsmd.getColumnCount();
		while(rs.next()){
			Object[] objData = new Object[columnCount];
			for (int i = 1; i <= columnCount ; i++) {
				objData[i - 1] = rs.getObject(i);
			}
			
			data.add(objData);
		}
		
		return data;
	}
	
	
	public static void close(Connection conn)throws SQLException{
		if(conn != null && !conn.isClosed()){
			conn.close();
		}
	}

}
