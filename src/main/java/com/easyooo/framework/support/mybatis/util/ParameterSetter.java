package com.easyooo.framework.support.mybatis.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * @author Killer
 */
public interface ParameterSetter {

	void setParameters(PreparedStatement ps) throws SQLException;

}
