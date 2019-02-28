package com.wyk.ioc_compiler

import com.wyk.ioc_annotation.Bind
import com.wyk.ioc_annotation.BindView
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic


/**
 *
    Filer mFileUtils; 跟文件相关的辅助类，生成JavaSourceCode.
    Elements mElementUtils;跟元素相关的辅助类，帮助我们去获取一些元素相关的信息。
    Messager mMessager;跟日志相关的辅助类。
 */
//可以在该模块的src/main/resources/META_INF/services/javax.annotation.processing.Processor 文件里注明"注解处理器"
//也可以使用"auto-service" 库自动生成META_INF目录下的文件
//@AutoService(Processor::class)
//@SupportedAnnotationTypes("BindView")
//@SupportedSourceVersion(SourceVersion.RELEASE_7)
class LocProcessor: AbstractProcessor(){

    lateinit var mFilerUtils: Filer
    lateinit var mElementUtils: Elements
    lateinit var mMessager: Messager

    //var mProxyMap =  mutableMapOf<String, ProxyInfo>()
    var mProxyMap =  hashMapOf<String, ProxyInfo>()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        mFilerUtils = processingEnv.filer
        mElementUtils = processingEnv.elementUtils
        mMessager = processingEnv.messager
    }
    /**返回支持的注解类型*/
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        val set =linkedSetOf<String>()
        //顾名思义: 正规的名字,比如a类有内部类b，通过canonicalName获取到的名字是"包名.a.b"
        set.add(BindView::class.java.canonicalName)
        set.add(Bind::class.java.canonicalName)
        return set
    }
    /**返回支持的源码版本*/
    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

       /**
        *  收集信息
        *  生成代理类（本文把编译时生成的类叫代理类）
        */
    override fun process(p0: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
         processingEnv.messager.printMessage(Diagnostic.Kind.NOTE,"annatation process......")
         mProxyMap.clear()
        /**1.收集信息*/
        //拿到我们通过@BindView注解的元素
        val elements = roundEnv.getElementsAnnotatedWith(BindView::class.java)
        for(ele in elements){
            //检查element类型
            if(!checkAnnotationValid(ele, BindView::class.java)){
                continue
            }
            //强转成成员变量
            val variableElement: VariableElement = ele as VariableElement
            //拿到变量所在类的类信息
            val typeElement = variableElement.enclosingElement as TypeElement
            //qualifiedName返回此类型元素的规范名称(这里应该是注解所在类的名称),例如MainActivity
            val qualifiedName = typeElement.qualifiedName.toString()

            var proxyInfo = mProxyMap.get(qualifiedName)
            if(proxyInfo == null){
                proxyInfo = ProxyInfo(mElementUtils, typeElement)
                mProxyMap.put(qualifiedName, proxyInfo)
            }
            val annotation = variableElement.getAnnotation(BindView::class.java)
            val id= annotation.value as Integer
            proxyInfo.injectVariables.put(id, variableElement)
        }
        /**生成代理类*/
        for(key in mProxyMap.keys){//遍历注解所在的类，生成代理类
            val proxyInfo = mProxyMap.get(key)
            try {
                /**
                 * ProxyClassFullName:  包名.MainActivity$$ViewInjector
                 * TypeElement:         包名.MainActivity 的类信息
                 */
                val javaFileObject = processingEnv.filer.createSourceFile(proxyInfo?.getProxyClassFullName(),
                                                                                            proxyInfo?.getTypeElement())

                val writer = javaFileObject.openWriter()
                writer.write(proxyInfo?.generateJavaCode())
                writer.flush()
                writer.close()
            }catch (e: IOException){
                error(proxyInfo?.getTypeElement(),  "Unable to write injector for type %s: %s",
                        arrayOf(proxyInfo?.getTypeElement(), e.message))
            }

        }
        return true
    }
    /**检查该元素的类型是否满足 " 成员变量" + " 非私有" 这两个条件*/
    fun checkAnnotationValid(annotatedElement: Element, clazz: Class<BindView>): Boolean{
        //判断该元素是否为成员变量
        if(annotatedElement.kind != ElementKind.FIELD){
            error(annotatedElement, "%s must be declared on field.", arrayOf(clazz.simpleName))
            return false
        }
        //判断该元素是否为私有
        if(ClassValidator.isPrivate(annotatedElement)){
            error(annotatedElement, "%s() must can not be private.", arrayOf(annotatedElement.simpleName))
            return false
        }
        return true
    }

    /**输出异常警告信息*/
    private fun error(element: Element?,message: String, args: Array<Any?>){
        var message = message
        if(args.size > 0){
            message = String.format(message, args)
        }
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, message, element)
        //mMessager.printMessage(Diagnostic.Kind.NOTE, message, element)
    }

}