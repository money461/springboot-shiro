package com.zyd.shiro.aspect;

import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.zyd.shiro.util.IpUtil;


/**
 * . 使用AOP统一处理Web请求日志
 * @author Qianli
 * 
 * 2018年6月1日 下午7:10:30
 */
@Aspect
@Order(5)
@Component
public class WebLogAspect {

	/**
	 * org.slf4j.Logger
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	

	    ThreadLocal<Long> startTime = new ThreadLocal<>();

	    @Pointcut("execution(public * com.zyd.shiro.controller..*.*(..))")
	    public void webLog(){}

	    @Before("webLog()")
	    public void doBefore(JoinPoint joinPoint) throws Throwable {
	        startTime.set(System.currentTimeMillis());

	        // 接收到请求，记录请求内容
	        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	        HttpServletRequest request = attributes.getRequest();
	        
	        // 记录下请求内容
	        logger.info("URL : " + request.getRequestURL().toString());
	        logger.info("HTTP_METHOD : " + request.getMethod());
	        logger.info("CLIENT_IP : " + IpUtil.getRealIp(request));
	        logger.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
//	        logger.info("METHOD_PARAM_NAMES : " + Arrays.toString( ((CodeSignature)joinPoint.getStaticPart().getSignature()).getParameterNames()));
	        logger.info("METHOD_PARAM_NAMES : " + Arrays.toString( ((MethodSignature)joinPoint.getSignature()).getParameterNames()));
	        logger.info("PARAM_VALUES : " + Arrays.toString(joinPoint.getArgs()));

	        Enumeration<String> e = request.getParameterNames();
	        while(e.hasMoreElements()){
	        	String param = (String)e.nextElement();//调用nextElement方法获得元素
	        	logger.info("{}={}",param,request.getParameter(param));
	        }
	        
	    }

	    @AfterReturning(returning = "ret", pointcut = "webLog()")
	    public void doAfterReturning(Object ret) throws Throwable {
	        // 处理完请求，返回内容
	        logger.info("RESPONSE : " + ret);
	        logger.info("SPEND TIME : " + (System.currentTimeMillis() - startTime.get()));
	        logger.info("--------------------------响应分割线---------------------------------");
	    }

}
