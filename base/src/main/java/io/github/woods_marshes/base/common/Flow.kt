package io.github.woods_marshes.base.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import io.github.woods_marshes.base.common.data.Result
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun <T> loadingResultFlowOf(flow: Flow<Result<T>>): Flow<Result<T>> {
    return flow {
        emit(Result.Loading)
        emitAll(flow)
    }
}

fun <T> Flow<T>.asResult(): Flow<Result<T>> = map<T, Result<T>> { Result.Success(it) }
    .onStart { emit(Result.Loading) }
    .catch { emit(Result.Error(null, it)) }