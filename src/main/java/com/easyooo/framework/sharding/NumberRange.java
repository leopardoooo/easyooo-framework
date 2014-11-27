package com.easyooo.framework.sharding;

import java.io.Serializable;

import lombok.ToString;

/**
 * 区间值定义
 * 
 * @author Killer
 */
@SuppressWarnings("serial")
@ToString
public class NumberRange implements Serializable{
	/**
     * The minimum number in this range.
     */
    private final Number min;
    
    /**
     * The maximum number in this range.
     */
    private final Number max;
    
    
    public NumberRange(Number min, Number max){
    	this.min = min;
    	this.max = max;
    }
    
	public Number getMin() {
		return min;
	}

	public Number getMax() {
		return max;
	}

}
