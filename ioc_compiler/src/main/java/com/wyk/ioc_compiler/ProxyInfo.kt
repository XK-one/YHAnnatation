package com.wyk.ioc_compiler

import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements
import kotlin.text.StringBuilder

/**
 * elementUtils: 操作元素的工具类
 * classElement: 类或接口的元素信息类
 */
class ProxyInfo(elementUtils: Elements, classElement: TypeElement) {

    private var typeElement: TypeElement? = null
    private var packageName: String? = null
    private var proxyClassName: String? = null

    var injectVariables= mutableMapOf<Integer, VariableElement>()

    companion object {
        val PROXY = "ViewInject"

    }

    /***
     *  获得包名，类名，生成代理类类名
     */
    init {
        this.typeElement = classElement
        val packageElement = elementUtils.getPackageOf(classElement)
        val packageName = packageElement.qualifiedName.toString()
        //className
        val className = ClassValidator.getClassName(classElement, packageName)  //$MainActivity
        this.packageName = packageName
        this.proxyClassName = "${className}$$${PROXY}"


    }

    /***
     * 生成代码所需要的信息：注解使用的类类包名，代理类名，代理接口名，注解使用的类类名全称
     */
    fun generateJavaCode(): String{
        var stringBuidler = StringBuilder()
        stringBuidler.append("// Generated code. Do not modify!\n")
        stringBuidler.append("package ").append(packageName).append(";\n\n")
        stringBuidler.append("import com.wyk.ioc_api.*;\n")
        stringBuidler.append("\n")

        stringBuidler.append("public class ").append(proxyClassName).
                      append(" implements ${ProxyInfo.PROXY}<${typeElement?.qualifiedName}>")
        stringBuidler.append(" {\n")

        generateMethods(stringBuidler)
        stringBuidler.append("\n")

        stringBuidler.append("}\n")
        return stringBuidler.toString()

    }

    /**
     *  生成代码所需要的信息：注解使用的类类名全称、控件ID，控件变量，控件变量名,控件的类型
     */
     fun generateMethods(builder: StringBuilder) {
        builder.append("@Override\n ")
        builder.append("public void inject(${typeElement?.qualifiedName} host, Object source ) {\n")

        for(id in injectVariables.keys){
            val element = injectVariables.get(id)
            val name= element?.simpleName.toString()
            val type = element?.asType().toString()
            builder.append(" if(source instanceof android.app.Activity){\n")
            builder.append("host.${name} = ")
            builder.append("(${type})(((android.app.Activity)source).findViewById(${id}));\n")
            builder.append("\n}else{\n")
            builder.append("host.${name} = ")
            builder.append("(${type})(((android.view.View)source).findViewById(${id}));\n")
            builder.append("\n};")
        }
        builder.append("  }\n")
    }

    /**
     * 假设注解使用类的类名是MainActivity，会生成一个 " 包名.MainActivity$$ViewInjector "
     */
    fun getProxyClassFullName(): String {
        val name =  "$packageName.$proxyClassName"
        return name
    }

    fun getTypeElement(): TypeElement? {
        return typeElement
    }
}