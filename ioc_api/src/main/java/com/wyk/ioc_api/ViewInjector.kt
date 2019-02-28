package com.wyk.ioc_api

import android.app.Activity
import android.view.View

class ViewInjector {


    companion object {

        private val SUFFIX = "\$\$ViewInject"

        fun injectView(activity: Activity){
            val proxyActivity = findProxyActivity(activity)
            proxyActivity.inject(activity, activity)
        }
        fun injectView(any: Any, view: View){
            val proxyActivity = findProxyActivity(any)
            proxyActivity.inject(any, view)
        }
        fun findProxyActivity(activity: Any): ViewInject<Any>{
            try {
                val clazz: Class<Any> = activity::javaClass.get()
                val injectorClazz  = Class.forName("${clazz.name}${SUFFIX}")
                return injectorClazz.newInstance() as ViewInject<Any>
            }catch(e: ClassNotFoundException ){
                e.printStackTrace()
            }catch (e: InstantiationException ){
                e.printStackTrace()
            }catch (e: IllegalAccessException ){
                e.printStackTrace()
            }
            throw RuntimeException(String.format("can not find %s , something when compiler.", activity::class.simpleName + SUFFIX))

        }

    }

}