/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache;

/**
 * <p> 实体类可以实现该接口完成获取主键的属性值， 
 * 实现了该接口在性能上会有所提升，省去了反射获取主键的消耗</p>
 * <p><b>这也是建议的做法</b></p>
 * 
 * @author Killer
 */
public interface IKeyValue {

	String[] getKeyValues();

}
