package com.hpu.xinqing.utils.standard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hpu.xinqingcommon.constant.CommonConstant;
import com.hpu.xinqingcommon.utils.SFun;
import org.springframework.beans.BeanUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @description 主要和ToStandard类结合使用, 有奇效, 就不用再使用VO造一大堆麻烦的类了.现在也可以使用更多对对象的属性操作功能了
 */
public class StandardChain {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private Map<String,Object> body;


    public StandardChain(Map<String,Object> body){
        this.body = body;
    }

    public <T> StandardChain(T obj){
        this.body = objectMapper.convertValue(obj,new TypeReference<>() {});
    }

    public StandardChain(Supplier<Map<String,Object>> supplier){
        this.body = supplier.get();
    }


    @SafeVarargs
    public static <IN,R> StandardChain getInstance(IN obj, SFun<IN,R> ...funs){
        StandardChain chain = new StandardChain(new HashMap<>());
        chain.put(obj,funs);
        return chain;
    }


    //----------------------------------中间操作(common)
    /**
     * 中间操作
     * 中途为某个类进行赋值
     * @return StandardChain
     */
    public <T> StandardChain copyProperties(T obj){
        Class<T> clazz = (Class<T>) obj.getClass();
        T source = objectMapper.convertValue(this.body, clazz);
        BeanUtils.copyProperties(source,obj);
        return this;
    }

    //打印Map中所有KV
    public void peek(){
        System.out.println(this.body);
    }

    //对map的自定义操作
    public StandardChain doSomething(Function<Map<String,Object>,Map<String,Object>> function){
        this.body= function.apply(this.body);
        return this;
    }

    public void doSomething(Consumer<Map<String,Object>> consumer){
        consumer.accept(this.body);
    }



    //---------------------------------最终操作
    /**
     * 获取最终结果
     * @return Map<String,Object>
     */
    public Map<String,Object> get(){
        return this.body;
    }

    /**
     * 获取最终结果
     * @return T
     */
    public <T> T get(Class<T> clazz){
        return objectMapper.convertValue(this.body,clazz);
    }


    public void end(){
        this.body.clear();
    }


    //---------------------------------------------------中间操作(put)
    public StandardChain put(String key,Object value){
        body.put(key,value);
        return this;
    }

    @SafeVarargs
    public final <IN,R> StandardChain put(IN obj, SFun<IN,R> ...funs){
        for (SFun<IN,R> fun : funs) {
            String k = getParamName(fun);
            R v = fun.apply(obj);
            body.put(k, v);
        }
        return this;
    }


    public StandardChain concat(Map<String,Object> map){
        body.putAll(map);
        return this;
    }


    public <T> StandardChain concat(T obj){
        Map<String,Object> objMap=objectMapper.convertValue(obj,new TypeReference<>() {});
        return concat(objMap);
    }


    //---------------------------------------------------中间操作(remove)
    public StandardChain remove(String key){
        body.remove(key);
        return this;
    }

    public StandardChain remove(String... keys){
        for (String key : keys) {
            body.remove(key);
        }
        return this;
    }

    public StandardChain remove(List<String> keys){
        for (String key : keys) {
            body.remove(key);
        }
        return this;
    }


    @SafeVarargs
    public final <IN,R> StandardChain remove(SFun<IN, R>... funs){
        for (SFun fun : funs) {
            String k = getParamName(fun);
            body.remove(k);
        }
        return this;
    }

    //移除id,userId,createId,updateId字段
    public StandardChain removeDefault(){
        return this.removeId()
                .removeAllUserId()
                .removeLogicDelete();
    }

    //移除逻辑删除
    public StandardChain removeLogicDelete(){
        body.remove("isDelete");
        return this;
    }

    //移除roleId
    public StandardChain removeRoleId(){
        body.remove("roleId");
        return this;
    }


    //移除userId,createId,updateId字段
    public StandardChain removeAllUserId(){
        return this.removeUserId()
                .removeCreateId()
                .removeUpdateId();
    }

    //移除pwd字段
    public StandardChain removePwd(){
        body.remove("pwd");
        body.remove("userPassword");
        return this;
    }

    //移除id字段
    public StandardChain removeId(){
        body.remove("id");
        return this;
    }

    //移除createId字段
    public StandardChain removeCreateId(){
        body.remove("createId");
        return this;
    }

    //移除updateId字段
    public StandardChain removeUpdateId(){
        body.remove("updateId");
        return this;
    }

    //移除userId字段
    public StandardChain removeUserId(){
        body.remove("userId");
        return this;
    }

    private <IN,R> SerializedLambda getLambda(SFun<IN,R> fun){
        try {
            Method writeReplace = fun.getClass().getDeclaredMethod(CommonConstant.WRITE_REPLACE);
            writeReplace.setAccessible(true);
            return (SerializedLambda) writeReplace.invoke(fun);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String getParamName(SerializedLambda lambda){
        String methodName = lambda.getImplMethodName();
        return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
    }

    private <IN,R> String getParamName(SFun<IN,R> fun){
        return getParamName(getLambda(fun));
    }
}

