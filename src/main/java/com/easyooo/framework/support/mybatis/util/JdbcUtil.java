package com.easyooo.framework.support.mybatis.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * Jdbc Utils
 *  TODO 临时修改
 * @author Killer
 */
public final class JdbcUtil {
	
	Logger logger = LoggerFactory.getLogger(JdbcUtil.class);
	
	public Integer counting(Connection conn, String sql, ParameterSetter setter, Object params)
			throws SQLException {
		List<Object[]> dataList = query(conn, sql, setter, params);
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
			ParameterSetter setter, Object params) throws SQLException{
		if(conn.isClosed()){
			return null;
		}
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			ps = conn.prepareStatement(sql);
			setter.setParameters(ps);
			// 设置超时时间为5秒
			ps.setQueryTimeout(5);
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = ps.getMetaData();
			return extractData(rsmd, rs);
		}catch(SQLTimeoutException e){
			logger.error("query timeout", e);
			logger.error("sql:{}, params:{}", sql, JSON.toJSONString(params));
			return null;
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
