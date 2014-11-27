package com.easyooo.framework.support.mybatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import com.easyooo.framework.support.mybatis.util.JdbcUtil;
import com.easyooo.framework.support.mybatis.util.ParameterSetter;

/**
 *
 * @author Killer
 */
public class CountingExecutor {
	
	private MappedStatement ms;
	private Dialect dialect;
	private BoundSql boundSql;
	
	private ParameterHandler parameterHandler;
	
	public CountingExecutor(MappedStatement ms, Dialect dialect, BoundSql boundSql){
		this.ms = ms;
		this.dialect = dialect;
		this.boundSql = boundSql;
		
		parameterHandler = new DefaultParameterHandler(ms, boundSql.getParameterObject(), boundSql);
	}
	
	public Integer execute() throws SQLException {
		String rawSql = boundSql.getSql().trim();
		String countingSql = dialect.getCountingSQL(rawSql);

		Connection conn = null;
		try{
			Environment evn = ms.getConfiguration().getEnvironment();
			conn = evn.getDataSource().getConnection();
			ParameterSetter setter = new ParameterSetter() {
				public void setParameters(PreparedStatement ps)
						throws SQLException {
					parameterHandler.setParameters(ps);
				}
			};
			return new JdbcUtil().counting(conn, countingSql, setter);
		}finally{
			JdbcUtil.close(conn);
		}
	}

}
