package com.wyk.ioc_compiler

import javax.lang.model.element.Element
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.TypeElement

class ClassValidator {
    companion object {
        fun isPrivate(annotatedClass: Element): Boolean{
            return annotatedClass.modifiers.contains(PRIVATE)
        }
        fun getClassName(type: TypeElement, packageName: String): String{
            val packageLen =  packageName.length + 1
            //qualifiedName返回此类型元素的规范名称
            return type.qualifiedName.toString().substring(packageLen).replace(".","$")
        }
    }
}