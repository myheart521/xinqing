package com.hpu.xinqingcommon.utils;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface SFun<T,R> extends Serializable, Function<T,R> {
}
