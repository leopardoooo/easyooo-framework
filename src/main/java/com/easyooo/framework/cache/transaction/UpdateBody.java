package com.easyooo.framework.cache.transaction;

import java.io.Serializable;
import java.util.Collection;

/**
 *
 * JVM 缓存同步参数的数据结构定义
 *
 * @author Killer
 */
@SuppressWarnings("serial")
public class UpdateBody implements Serializable{

	private String clientId;
	private Collection<Command> commands;
	
	public UpdateBody() {
		super();
	}

	public UpdateBody(String clientId,
			Collection<Command> commands) {
		super();
		this.clientId = clientId;
		this.commands = commands;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Collection<Command> getCommands() {
		return commands;
	}

	public void setCommands(Collection<Command> commands) {
		this.commands = commands;
	}
	
}
