package com.easyooo.framework.rule.java;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;

/**
 *
 * @author Killer
 */
public class CompileException extends Throwable{
	private static final long serialVersionUID = 1L;
	private DiagnosticCollector<?> dc;
	
	public CompileException(DiagnosticCollector<?> dc){
		super();
		this.dc = dc;
	}
	
	public CompileException(Throwable e, DiagnosticCollector<?> dc){
		super(e);
		this.dc = dc;
	}
	
	public String getDiagnosticInfo(){
		StringBuffer sb = new StringBuffer();
		for (Diagnostic<?> diagnostic : dc.getDiagnostics()) {
			sb.append(diagnostic.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
