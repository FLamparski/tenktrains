package com.filipwieland.tenktrains.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import kotlin.reflect.full.companionObject

interface Slf4j {}

inline fun <T : Any> getClassForLogging(javaClass: Class<T>): Class<*> {
    return javaClass.enclosingClass?.takeIf {
        it.kotlin.companionObject?.java == javaClass
    } ?: javaClass
}

inline fun <reified T : Slf4j> T.logger(): Logger
        = getLogger(getClassForLogging(T::class.java))