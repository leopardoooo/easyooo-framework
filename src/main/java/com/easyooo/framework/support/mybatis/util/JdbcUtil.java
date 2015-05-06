package com.easyooo.framework.support.mybatis.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
		
		Long start = System.currentTimeMillis();;
		try{
			ps = conn.prepareStatement(sql);
			setter.setParameters(ps);
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = ps.getMetaData();
			return extractData(rsmd, rs);
		}finally{
			// 如果查询结果超过4秒就输出错误日志
			if(System.currentTimeMillis() - start >= 4000){
				StringBuffer sb = new StringBuffer();
				sb.append("ps：" + ps + "\n");
				sb.append("sql：" + sql + ",params: "+ JSON.toJSONString(params));
				logger.error(sb.toString());
			}
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
