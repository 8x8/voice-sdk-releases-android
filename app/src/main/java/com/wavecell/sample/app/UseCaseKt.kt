package com.wavecell.sample.app

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.io.StringWriter

abstract class UseCaseKt<T>(private val dispatcher: CoroutineDispatcher) {

    suspend fun execute(): ResultWrapper<T> {
        return safeBuild()
    }

    private suspend fun safeBuild(): ResultWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResultWrapper.Success(build())
            } catch (throwable: Throwable) {
                ResultWrapper.Error(UseCaseErrorMapper.mapError(throwable), throwable)
            }
        }
    }

    abstract suspend fun build(): T
}

object UseCaseErrorMapper {
    fun mapError(throwable: Throwable): String {
        val message = throwable.message
        val stringWriter = StringWriter()
        val trace = stringWriter.use { writer ->
            val printWriter = PrintWriter(writer)
            printWriter.use {
                throwable.printStackTrace(it)
                stringWriter.toString()
            }
        }
        return "$message - $trace"
    }
}


sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Error(val message: String = "Unknown", val error: Throwable) : ResultWrapper<Nothing>()
}

