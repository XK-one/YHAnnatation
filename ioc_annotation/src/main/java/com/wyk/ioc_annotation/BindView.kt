package com.wyk.ioc_annotation


@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class BindView(val value: Int){
}