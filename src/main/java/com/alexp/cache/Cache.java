package com.alexp.cache;

import java.util.function.Function;

public interface Cache<K, V> {
  V get(K key);

  V put(K key, V value);

  default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    V value = this.get(key);
    if (value != null) return value;
    return this.put(key, mappingFunction.apply(key));
  }
}
