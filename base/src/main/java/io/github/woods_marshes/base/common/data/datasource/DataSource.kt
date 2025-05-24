package io.github.woods_marshes.base.common.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import io.github.woods_marshes.base.common.data.Result

abstract class DataSource<W> {
    private val triggerFlow = MutableStateFlow(0)

    val flow: Flow<Result<W>> = triggerFlow
        .flatMapLatest {
            flow {
                emit(Result.Loading)
                emit(fetchData())
            }
        }

    abstract suspend fun fetchData(): Result<W>

    fun refresh() {
        triggerFlow.tryEmit(triggerFlow.value + 1)
    }
}