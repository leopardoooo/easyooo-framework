package com.easyooo.framework.sharding.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Current Connection Holder
 *
 * @author Killer
 */
public class ConnectionHolder {

	private int referenceCount = 0;
	
	private String user;
	private String password;
	
	private Connection conn;
	
	public ConnectionHolder(){
	}
	
	public ConnectionHolder(Connection conn){
		this.conn = conn;
	}
	
	public ConnectionHolder(Connection conn, String user, String pwd){
		this.conn = conn;
		this.user = user;
		this.password = pwd;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean equalUserAndPwd(String user, String pwd){
		return (this.user == user && this.password == pwd);
	}
	
	public void close()throws SQLException{
		if(conn != null && !conn.isClosed()){
			if(this.conn instanceof CloseDelegatingConnection){
				((CloseDelegatingConnection) this.conn).forceClose();
			}else{
				this.conn.close();
			}
		}
	}
	
	public void rollback()throws SQLException{
		if(conn != null){
			this.conn.rollback();
		}
	}
	
	public void commit()throws SQLException{
		if(conn != null){
			this.conn.commit();
		}
	}
	
	public void requested() {
		this.referenceCount++;
	}

	public void released() {
		this.referenceCount--;
	}
	
	public int getReferenceCount() {
		return referenceCount;
	}

	@Override
	public String toString() {
		return conn != null ? conn.toString() : null;
	}
}
