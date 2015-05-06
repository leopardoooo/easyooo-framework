package com.easyooo.framework.support.mybatis.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyooo.framework.common.util.CglibUtil;

/**
 * Jdbc Utils
 * @author Killer
 */
public final class JdbcUtil {
	
	Logger logger = LoggerFactory.getLogger(JdbcUtil.class);
	
	private SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private String buildStatmentSql(String sql, BoundSql boundSql){
		List<ParameterMapping> mappings = boundSql.getParameterMappings();
		if(mappings == null){
			return sql;
		}
		Object paramObject = boundSql.getParameterObject();
		for (ParameterMapping pm : mappings) {
			String propertyName = pm.getProperty();
			Object value = null;
			if (boundSql.hasAdditionalParameter(propertyName)) {
				value = boundSql.getAdditionalParameter(propertyName);
			}else if(paramObject != null){
				value = CglibUtil.getPropertyValue(paramObject, propertyName);
			}
			
			String phString = "";
			if(value != null ){
				if(value instanceof String){
					phString ="'" + value.toString() +"'";
				}else if(value instanceof Date){
					phString ="'" +  SDF.format((Date)value) +"'";
				}else{
					phString = value.toString();
				}
			}else{
				phString = null;
			}
			sql = sql.replaceFirst("\\?", phString);
		}
		return sql;
	}
	
	public Integer counting(Connection conn, String sql, BoundSql boundSql)
			throws SQLException {
		List<Object[]> dataList = query(conn, buildStatmentSql(sql, boundSql));
		if(dataList != null && dataList.size() > 0){
			// Extract the first row and first column data 
			Object[] rowData = dataList.get(0);
			if(rowData != null && rowData.length == 1){
				return Integer.valueOf(String.valueOf(rowData[0]));
			}
		}
		return null;
	}

	public List<Object[]> query(Connection conn, String sql) throws SQLException{
		if(conn.isClosed()){
			return null;
		}
		
		Statement state = null;
		ResultSet rs = null;
		
		try{
			state = conn.createStatement();
			rs = state.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			return extractData(rsmd, rs);
		}finally{
			if(rs != null){
				rs.close();
			}
			
			if(state != null){
				state.close();
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
