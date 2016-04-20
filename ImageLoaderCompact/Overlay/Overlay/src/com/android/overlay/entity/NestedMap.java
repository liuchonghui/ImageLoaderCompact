package com.android.overlay.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Map of map with string value as keys for both maps.
 * 
 * @author liu_chonghui
 * 
 * @param <T>
 */
public class NestedMap<T> implements Iterable<NestedMap.Entry<T>> {

	private final Map<String, Map<String, T>> map;

	public NestedMap() {
		map = new HashMap<String, Map<String, T>>();
	}

	public T get(String first, String second) {
		Map<String, T> nested = map.get(first);
		if (nested == null) {
			return null;
		}
		return nested.get(second);
	}

	synchronized public void put(String first, String second, T value) {
		Map<String, T> nested = map.get(first);
		if (nested == null) {
			nested = new HashMap<String, T>();
			map.put(first, nested);
		}
		nested.put(second, value);
	}

	synchronized public T remove(String first, String second) {
		Map<String, T> nested = map.get(first);
		if (nested == null) {
			return null;
		}
		T value = nested.remove(second);
		if (nested.isEmpty()) {
			map.remove(first);
		}
		return value;
	}

	synchronized public void clear(String first) {
		map.remove(first);
	}

	synchronized public void clear() {
		map.clear();
	}

	synchronized public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Iterator<Entry<T>> iterator() {
		return new EntryIterator();
	}

	public Map<String, T> getNested(String first) {
		Map<String, T> nested = map.get(first);
		if (nested == null) {
			return Collections.emptyMap();
		}
		return Collections.unmodifiableMap(nested);
	}

	public Collection<T> values() {
		return new Values();
	}

	public Collection<String> keySet() {
		return map.keySet();
	}

	public void addAll(NestedMap<T> nestedMap) {
		for (NestedMap.Entry<T> entry : nestedMap) {
			put(entry.getFirst(), entry.getSecond(), entry.getValue());
		}
	}

	public static class Entry<T> {

		private final String first;
		private final String second;
		private final T value;

		public Entry(String first, String second, T value) {
			super();
			this.first = first;
			this.second = second;
			this.value = value;
		}

		public String getFirst() {
			return first;
		}

		public String getSecond() {
			return second;
		}

		public T getValue() {
			return value;
		}

	}

	private class EntryIterator implements Iterator<Entry<T>> {

		private final Iterator<java.util.Map.Entry<String, Map<String, T>>> firstIterator;

		private java.util.Map.Entry<String, Map<String, T>> nested;

		private Iterator<java.util.Map.Entry<String, T>> secondIterator;

		private EntryIterator() {
			firstIterator = map.entrySet().iterator();
			nested = null;
			secondIterator = null;
		}

		@Override
		public boolean hasNext() {
			if (secondIterator != null && secondIterator.hasNext()) {
				return true;
			}
			while (firstIterator.hasNext()) {
				nested = firstIterator.next();
				secondIterator = nested.getValue().entrySet().iterator();
				if (secondIterator.hasNext()) {
					return true;
				}
			}
			return false;
		}

		@Override
		public Entry<T> next() throws NoSuchElementException {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			java.util.Map.Entry<String, T> entry = secondIterator.next();
			return new Entry<T>(nested.getKey(), entry.getKey(),
					entry.getValue());
		}

		@Override
		public void remove() throws IllegalStateException {
			if (secondIterator == null) {
				throw new IllegalStateException();
			}
			secondIterator.remove();
			if (nested.getValue().isEmpty()) {
				firstIterator.remove();
			}
		}

	}

	private class Values implements Collection<T> {

		@Override
		public boolean add(T object) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends T> arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(Object object) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isEmpty() {
			return iterator().hasNext();
		}

		@Override
		public Iterator<T> iterator() {
			return new ValuesIterator();
		}

		@Override
		public boolean remove(Object object) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object[] toArray() {
			throw new UnsupportedOperationException();
		}

		@Override
		public <Type> Type[] toArray(Type[] array) {
			throw new UnsupportedOperationException();
		}

		private class ValuesIterator implements Iterator<T> {

			private final Iterator<Entry<T>> iterator;

			private ValuesIterator() {
				this.iterator = NestedMap.this.iterator();
			}

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public T next() {
				return iterator.next().getValue();
			}

			@Override
			public void remove() {
				iterator.remove();
			}
		}
	}
}
