package com.wafflestudio.snutt2.storage.pref

class MutableMultiMap<K, V> {
    private val map: MutableMap<K, MutableSet<V>> = mutableMapOf()

    val size: Int
        get() = map.values.fold(0) { acc, it -> acc + it.size }

    fun isEmpty(): Boolean = map.values.isEmpty()

    fun containsKey(key: K): Boolean = map.containsKey(key)

    fun containsValue(value: V): Boolean = map.values.find { it.contains(value) } != null

    fun containsEntry(key: K, value: V): Boolean = map[key]?.contains(value) ?: false

    fun put(key: K, value: V) {
        val set = map[key]
        if (set != null) {
            set.add(value)
        } else {
            map[key] = mutableSetOf(value)
        }
    }

    fun remove(key: K, value: V) {
        val set = map[key]
        set?.remove(value)
        if (set?.isEmpty() == true) {
            map.remove(key)
        }
    }

    fun removeAll(key: K) {
        map.remove(key)
    }

    fun clear() {
        val keys = keySet
        for (key in keys) {
            map.remove(key)
        }
    }

    operator fun get(key: K): Collection<V> = map[key]?.toSet() ?: setOf()

    val keySet: Set<K>
        get() = map.keys.toSet()

    val values: Collection<V>
        get() {
            val sumSet = mutableSetOf<V>()
            map.values.forEach { sumSet.addAll(it) }
            return sumSet
        }

    val entries: Collection<Entry<K, V>>
        get() = map.flatMap { mapEntry -> mapEntry.value.map { v -> Entry(mapEntry.key, v) } }

    data class Entry<K, V>(val key: K, val value: V)
}

fun <K, V> mutableMultiMapOf(): MutableMultiMap<K, V> = MutableMultiMap()
