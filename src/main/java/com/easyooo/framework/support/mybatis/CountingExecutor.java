package com.easyooo.framework.support.mybatis;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;

import com.easyooo.framework.support.mybatis.util.JdbcUtil;

/**
 *
 * @author Killer
 */
public class CountingExecutor {
	
	private MappedStatement ms;
	private Dialect dialect;
	private BoundSql boundSql;
	
	public CountingExecutor(MappedStatement ms, Dialect dialect, BoundSql boundSql){
		this.ms = ms;
		this.dialect = dialect;
		this.boundSql = boundSql;
	}
	
	public Integer execute() throws SQLException {
		String rawSql = boundSql.getSql().trim();
		String countingSql = dialect.getCountingSQL(rawSql);

		Connection conn = null;
		try{
			Environment evn = ms.getConfiguration().getEnvironment();
			conn = evn.getDataSource().getConnection();
			return new JdbcUtil().counting(conn, countingSql, boundSql);
		}finally{
			JdbcUtil.close(conn);
		}
	}

}
