package com.hpu.xinqing.handler;
import com.hpu.xinqingcommon.constant.MessageConstant;
import com.hpu.xinqingcommon.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.hpu.xinqingcommon.result.Result;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     *@Description:SQL异常
     * @Param: ex
     * @Return:result
    */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
//        错误信息：Duplicate entry 'qihe' for key 'employee.idx_username'
        String message=ex.getMessage();
        if(message.contains("Duplicate entry")){
            String[] s= message.split(" ");
            String username=s[2];
            String mes=username+ MessageConstant.ALTREADY_EXISTS;
            return Result.error(mes);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

}
