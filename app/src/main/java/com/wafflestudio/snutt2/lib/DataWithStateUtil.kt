package com.wafflestudio.snutt2.lib

fun <TData : Any, TState : Any> TData.toDataWithState(state: TState): DataWithState<TData, TState> {
    return DataWithState.ofState(this, state)
}

data class DataWithState<TData : Any, TState : Any>(
    val item: TData,
    val state: TState,
) {

    companion object {
        fun <TData : Any, TState : Any> ofState(
            item: TData,
            state: TState,
        ): DataWithState<TData, TState> =
            DataWithState(item, state)
    }
}

typealias Selectable<T> = DataWithState<T, Boolean>

fun <T : Any> Selectable<T>.isSelected() = state

fun <T : Any> Selectable<T>.toggle(): Selectable<T> {
    return this.copy(state = !state)
}

fun <T : Any> List<Selectable<T>>.selectIndex(index: Int): List<Selectable<T>> {
    return this.mapIndexed { i, data ->
        data.copy(state = i == index)
    }
}

fun <T : Any> List<Selectable<T>>.toggleIndex(index: Int): List<Selectable<T>> {
    return this.mapIndexed { i, data ->
        if (i == index) data.copy(state = !data.state) else data
    }
}

fun <T : Any> List<Selectable<T>>.allSelected(): Boolean {
    return this.all { it -> it.isSelected() }
}

fun <T : Any> List<Selectable<T>>.anySelected(): Boolean {
    return this.any { it -> it.isSelected() }
}

fun <T : Any> List<Selectable<T>>.unselectExcept(index: Int): List<Selectable<T>> {
    return this.mapIndexed { i, data ->
        if (i == index) data else data.copy(state = false)
    }
}
