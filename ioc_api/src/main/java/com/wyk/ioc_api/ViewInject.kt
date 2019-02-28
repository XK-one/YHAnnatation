package com.wyk.ioc_api

open interface ViewInject<T>{
    fun inject(t: T, source: Any)
}