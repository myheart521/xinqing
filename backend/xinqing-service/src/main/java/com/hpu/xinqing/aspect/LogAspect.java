package com.hpu.xinqing.aspect;

import com.hpu.xinqing.utils.common.StringUtils;
import com.hpu.xinqingcommon.context.ReqInfo;
import com.hpu.xinqingcommon.context.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LogAspect {

    SpelExpressionParser parser=new SpelExpressionParser();

    @Pointcut("@annotation(log)||@within(log)")
    public void logPointcut(Log log) {}

    @Around("logPointcut(logMy)")
    public Object logPointcut(ProceedingJoinPoint joinPoint,Log logMy) throws Throwable {
        StringBuffer sb = new StringBuffer();
        ReqInfo reqInfo = RequestHolder.getReqInfo();
        long beforeT = System.currentTimeMillis();
        Object res = joinPoint.proceed();
        long afterT = System.currentTimeMillis();
        long time = afterT - beforeT;
        //初始化StringBuffer
        sb.append("userId:").append(reqInfo.getUserId())
                .append("    ip: \"").append(reqInfo.getIp()).append("\"")
                .append("   ").append(reqInfo.getMethod())
                .append("  ").append(reqInfo.getUrl())
                .append("    耗时: ").append(time).append("ms")
                ;
        //处理desc
        String desc = logMy.desc();
        String expression = logMy.expression();
        if (!desc.equals("")){
            sb.append("\ndesc: ").append(desc);
        }
        //处理表达式
        if (!expression.equals("")){
            String expressionStr =null;
            //可能要获取入参
            if (StringUtils.hasSharp(expression)){
                //存入将参数存入Context
                StandardEvaluationContext context = new StandardEvaluationContext();
                Object[] args = joinPoint.getArgs();
                MethodSignature signature =(MethodSignature) joinPoint.getSignature();
                String[] parameterNames = signature.getParameterNames();
                for (int i = 0; i < args.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
                expressionStr=parser.parseExpression(expression).getValue(context, String.class);
            }
            //无需获取入参
            else {
                expressionStr=parser.parseExpression(expression).getValue(String.class);
            }
            sb.append(expressionStr);
        }
        log.info(sb.toString());
        return res;
    }

}
