package com.easyooo.framework.rule.java;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyooo.framework.rule.RuleException;
import com.easyooo.framework.rule.impl.AbstractRuleClassLoader;
import com.easyooo.framework.rule.impl.ScriptWriter;

/**
 * Java动态编译
 * 
 * @author Killer
 */
public class JavaRuleClassLoader extends AbstractRuleClassLoader {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private String classpath;
	private final List<String> OPTIONS;

	public JavaRuleClassLoader() {
		initClassPath();
		OPTIONS = new ArrayList<String>();
		OPTIONS.add("-encoding");
		OPTIONS.add("UTF-8");
		OPTIONS.add("-classpath");
		OPTIONS.add(classpath);
	}
	
	private void initClassPath() {
		// get parent classLoader
		URLClassLoader parentClassLoader = (URLClassLoader) this.getClass()
				.getClassLoader();
		
		// classpath from parent class loader
		StringBuilder sb = new StringBuilder();
		for (URL url : parentClassLoader.getURLs()) {
			String p = url.getFile();
			sb.append(p).append(File.pathSeparator);
		}
		this.classpath = sb.toString();
	}

	@Override
	public Class<?> loadClass(File sourceFile, ScriptWriter sw) 
			throws RuleException {
		StandardJavaFileManager sjfm = null;
		try{
			JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
			sjfm = javaCompiler.getStandardFileManager(null, null, null);
			Iterable<? extends JavaFileObject> it = 
					sjfm.getJavaFileObjects(sourceFile);
			
			// Fault diagnosis device
			DiagnosticCollector<JavaFileObject> dc = 
					new DiagnosticCollector<JavaFileObject>();
			
			// create compliation task
			CompilationTask task = javaCompiler.getTask(null, sjfm,
					dc, OPTIONS, null, it);
			
			// start compliation
			boolean success = task.call();
			
			if(success){
				return sw.getScriptClass();
			}else{
				this.printError(dc);
				throw new CompileException(dc);
			}
		}catch(NullPointerException e){
			throw new RuleException("Do not support the compilation, Please try to for JDK jre replacement" , e);
		}catch(Throwable e){
			throw new RuleException("Compile the Java file error" , e);
		}finally{
			try {
				sjfm.close();
			} catch (IOException e) {
				logger.error("Compile the Java source file, unable "
						+ "to shut StandardJavaFileManager", e);
			}
		}
	}
	
	
	private void printError(DiagnosticCollector<JavaFileObject> dc){
		for (Iterator<?> ite = dc.getDiagnostics().iterator(); ite.hasNext(); ) {
			logger.error(ite.next().toString());
		}
	}
}
