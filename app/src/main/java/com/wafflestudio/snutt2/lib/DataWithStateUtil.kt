package com.wafflestudio.snutt2.lib

fun <TData : Any, TState : Any> TData.toDataWithState(state: TState): DataWithState<TData, TState> {
    return DataWithState.ofState(this, state)
}

data class DataWithState<TData : Any, TState : Any>(
    val item: TData,
    val state: TState
) {

    companion object {
        fun <TData : Any, TState : Any> ofState(
            item: TData,
            state: TState
        ): DataWithState<TData, TState> =
            DataWithState(item, state)
    }
}


typealias Selectable<T> = DataWithState<T, Boolean>
