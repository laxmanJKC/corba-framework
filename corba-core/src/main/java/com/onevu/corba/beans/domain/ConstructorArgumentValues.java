package com.onevu.corba.beans.domain;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.onevu.corba.util.Assert;
import com.onevu.corba.util.ClassUtils;
import com.onevu.corba.util.ObjectUtils;

public class ConstructorArgumentValues implements Comparator<ConstructorArgumentValues> {
	
	private Constructor ctor;

	private final Map<Integer, ValueHolder> indexedArgumentValues = new LinkedHashMap<Integer, ValueHolder>();

	private final List<ValueHolder> genericArgumentValues = new ArrayList<ValueHolder>();

	/**
	 * Create a new empty ConstructorArgumentValues object.
	 */
	public ConstructorArgumentValues(Constructor ctor) {
		this.ctor = ctor;
	}

	/**
	 * Add an argument value for the given index in the constructor argument list.
	 * 
	 * @param index the index in the constructor argument list
	 * @param value the argument value
	 */
	public void addIndexedArgumentValue(int index, Object value) {
		addIndexedArgumentValue(index, new ValueHolder(value));
	}

	/**
	 * Add an argument value for the given index in the constructor argument list.
	 * 
	 * @param index the index in the constructor argument list
	 * @param value the argument value
	 * @param type  the type of the constructor argument
	 */
	public void addIndexedArgumentValue(int index, Object value, String type) {
		addIndexedArgumentValue(index, new ValueHolder(value, type));
	}
	
	/**
	 * Check whether an argument value has been registered for the given index.
	 * 
	 * @param index the index in the constructor argument list
	 */
	public boolean hasIndexedArgumentValue(int index) {
		return this.indexedArgumentValues.containsKey(index);
	}
	
	public void addIndexedArgumentValue(int index, ValueHolder valueHolder) {
		this.indexedArgumentValues.put(index, valueHolder);
	}

	/**
	 * Get argument value for the given index in the constructor argument list.
	 * 
	 * @param index        the index in the constructor argument list
	 * @param requiredType the type to match (can be {@code null} to match untyped
	 *                     values only)
	 * @return the ValueHolder for the argument, or {@code null} if none set
	 */
	public ValueHolder getIndexedArgumentValue(int index, Class<?> requiredType) {
		return getIndexedArgumentValue(index, requiredType, null);
	}

	/**
	 * Get argument value for the given index in the constructor argument list.
	 * 
	 * @param index        the index in the constructor argument list
	 * @param requiredType the type to match (can be {@code null} to match untyped
	 *                     values only)
	 * @param requiredName the type to match (can be {@code null} to match unnamed
	 *                     values only, or empty String to match any name)
	 * @return the ValueHolder for the argument, or {@code null} if none set
	 */
	public ValueHolder getIndexedArgumentValue(int index, Class<?> requiredType, String requiredName) {
		Assert.isTrue(index >= 0, "Index must not be negative");
		ValueHolder valueHolder = this.indexedArgumentValues.get(index);
		if (valueHolder != null
				&& (valueHolder.getType() == null
						|| (requiredType != null && ClassUtils.matchesTypeName(requiredType, valueHolder.getType())))
				&& (valueHolder.getName() == null || (requiredName != null
						&& (requiredName.isEmpty() || requiredName.equals(valueHolder.getName()))))) {
			return valueHolder;
		}
		return null;
	}

	/**
	 * Return the map of indexed argument values.
	 * 
	 * @return unmodifiable Map with Integer index as key and ValueHolder as value
	 * @see ValueHolder
	 */
	public Map<Integer, ValueHolder> getIndexedArgumentValues() {
		return Collections.unmodifiableMap(this.indexedArgumentValues);
	}

	/**
	 * Add a generic argument value to be matched by type.
	 * <p>
	 * Note: A single generic argument value will just be used once, rather than
	 * matched multiple times.
	 * 
	 * @param value the argument value
	 */
	public void addGenericArgumentValue(Object value) {
		this.genericArgumentValues.add(new ValueHolder(value));
	}

	/**
	 * Add a generic argument value to be matched by type.
	 * <p>
	 * Note: A single generic argument value will just be used once, rather than
	 * matched multiple times.
	 * 
	 * @param value the argument value
	 * @param type  the type of the constructor argument
	 */
	public void addGenericArgumentValue(Object value, String type) {
		this.genericArgumentValues.add(new ValueHolder(value, type));
	}





	/**
	 * Look for a generic argument value that matches the given type.
	 * 
	 * @param requiredType the type to match
	 * @return the ValueHolder for the argument, or {@code null} if none set
	 */
	public ValueHolder getGenericArgumentValue(Class<?> requiredType) {
		return getGenericArgumentValue(requiredType, null, null);
	}

	/**
	 * Look for a generic argument value that matches the given type.
	 * 
	 * @param requiredType the type to match
	 * @param requiredName the name to match
	 * @return the ValueHolder for the argument, or {@code null} if none set
	 */
	public ValueHolder getGenericArgumentValue(Class<?> requiredType, String requiredName) {
		return getGenericArgumentValue(requiredType, requiredName, null);
	}

	/**
	 * Look for the next generic argument value that matches the given type,
	 * ignoring argument values that have already been used in the current
	 * resolution process.
	 * 
	 * @param requiredType     the type to match (can be {@code null} to find an
	 *                         arbitrary next generic argument value)
	 * @param requiredName     the name to match (can be {@code null} to not match
	 *                         argument values by name, or empty String to match any
	 *                         name)
	 * @param usedValueHolders a Set of ValueHolder objects that have already been
	 *                         used in the current resolution process and should
	 *                         therefore not be returned again
	 * @return the ValueHolder for the argument, or {@code null} if none found
	 */
	public ValueHolder getGenericArgumentValue(Class<?> requiredType, String requiredName,
			Set<ValueHolder> usedValueHolders) {

		for (ValueHolder valueHolder : this.genericArgumentValues) {
			if (usedValueHolders != null && usedValueHolders.contains(valueHolder)) {
				continue;
			}
			if (valueHolder.getName() != null && (requiredName == null
					|| (!requiredName.isEmpty() && !requiredName.equals(valueHolder.getName())))) {
				continue;
			}
			if (valueHolder.getType() != null
					&& (requiredType == null || !ClassUtils.matchesTypeName(requiredType, valueHolder.getType()))) {
				continue;
			}
			if (requiredType != null && valueHolder.getType() == null && valueHolder.getName() == null
					&& !ClassUtils.isAssignableValue(requiredType, valueHolder.getValue())) {
				continue;
			}
			return valueHolder;
		}
		return null;
	}

	/**
	 * Return the list of generic argument values.
	 * 
	 * @return unmodifiable List of ValueHolders
	 * @see ValueHolder
	 */
	public List<ValueHolder> getGenericArgumentValues() {
		return Collections.unmodifiableList(this.genericArgumentValues);
	}

	/**
	 * Look for an argument value that either corresponds to the given index in the
	 * constructor argument list or generically matches by type.
	 * 
	 * @param index        the index in the constructor argument list
	 * @param requiredType the parameter type to match
	 * @return the ValueHolder for the argument, or {@code null} if none set
	 */
	public ValueHolder getArgumentValue(int index, Class<?> requiredType) {
		return getArgumentValue(index, requiredType, null, null);
	}

	/**
	 * Look for an argument value that either corresponds to the given index in the
	 * constructor argument list or generically matches by type.
	 * 
	 * @param index        the index in the constructor argument list
	 * @param requiredType the parameter type to match
	 * @param requiredName the parameter name to match
	 * @return the ValueHolder for the argument, or {@code null} if none set
	 */
	public ValueHolder getArgumentValue(int index, Class<?> requiredType, String requiredName) {
		return getArgumentValue(index, requiredType, requiredName, null);
	}

	/**
	 * Look for an argument value that either corresponds to the given index in the
	 * constructor argument list or generically matches by type.
	 * 
	 * @param index            the index in the constructor argument list
	 * @param requiredType     the parameter type to match (can be {@code null} to
	 *                         find an untyped argument value)
	 * @param requiredName     the parameter name to match (can be {@code null} to
	 *                         find an unnamed argument value, or empty String to
	 *                         match any name)
	 * @param usedValueHolders a Set of ValueHolder objects that have already been
	 *                         used in the current resolution process and should
	 *                         therefore not be returned again (allowing to return
	 *                         the next generic argument match in case of multiple
	 *                         generic argument values of the same type)
	 * @return the ValueHolder for the argument, or {@code null} if none set
	 */
	public ValueHolder getArgumentValue(int index, Class<?> requiredType, String requiredName,
			Set<ValueHolder> usedValueHolders) {
		Assert.isTrue(index >= 0, "Index must not be negative");
		ValueHolder valueHolder = getIndexedArgumentValue(index, requiredType, requiredName);
		if (valueHolder == null) {
			valueHolder = getGenericArgumentValue(requiredType, requiredName, usedValueHolders);
		}
		return valueHolder;
	}

	/**
	 * Return the number of argument values held in this instance, counting both
	 * indexed and generic argument values.
	 */
	public int getArgumentCount() {
		return (this.indexedArgumentValues.size() + this.genericArgumentValues.size());
	}

	/**
	 * Return if this holder does not contain any argument values, neither indexed
	 * ones nor generic ones.
	 */
	public boolean isEmpty() {
		return (this.indexedArgumentValues.isEmpty() && this.genericArgumentValues.isEmpty());
	}

	/**
	 * Clear this holder, removing all argument values.
	 */
	public void clear() {
		this.indexedArgumentValues.clear();
		this.genericArgumentValues.clear();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ConstructorArgumentValues)) {
			return false;
		}
		ConstructorArgumentValues that = (ConstructorArgumentValues) other;
		if (this.genericArgumentValues.size() != that.genericArgumentValues.size()
				|| this.indexedArgumentValues.size() != that.indexedArgumentValues.size()) {
			return false;
		}
		Iterator<ValueHolder> it1 = this.genericArgumentValues.iterator();
		Iterator<ValueHolder> it2 = that.genericArgumentValues.iterator();
		while (it1.hasNext() && it2.hasNext()) {
			ValueHolder vh1 = it1.next();
			ValueHolder vh2 = it2.next();
			if (!vh1.contentEquals(vh2)) {
				return false;
			}
		}
		for (Map.Entry<Integer, ValueHolder> entry : this.indexedArgumentValues.entrySet()) {
			ValueHolder vh1 = entry.getValue();
			ValueHolder vh2 = that.indexedArgumentValues.get(entry.getKey());
			if (vh2 == null || !vh1.contentEquals(vh2)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hashCode = 7;
		for (ValueHolder valueHolder : this.genericArgumentValues) {
			hashCode = 31 * hashCode + valueHolder.contentHashCode();
		}
		hashCode = 29 * hashCode;
		for (Map.Entry<Integer, ValueHolder> entry : this.indexedArgumentValues.entrySet()) {
			hashCode = 31 * hashCode + (entry.getValue().contentHashCode() ^ entry.getKey().hashCode());
		}
		return hashCode;
	}

	public Constructor getCtor() {
		return ctor;
	}

	public static class ValueHolder {

		private Object value;

		private String type;

		private String name;

		private Object source;

		private boolean converted = false;

		private Object convertedValue;

		/**
		 * Create a new ValueHolder for the given value.
		 * 
		 * @param value the argument value
		 */
		public ValueHolder(Object value) {
			this.value = value;
		}

		/**
		 * Create a new ValueHolder for the given value and type.
		 * 
		 * @param value the argument value
		 * @param type  the type of the constructor argument
		 */
		public ValueHolder(Object value, String type) {
			this.value = value;
			this.type = type;
		}

		/**
		 * Create a new ValueHolder for the given value, type and name.
		 * 
		 * @param value the argument value
		 * @param type  the type of the constructor argument
		 * @param name  the name of the constructor argument
		 */
		public ValueHolder(Object value, String type, String name) {
			this.value = value;
			this.type = type;
			this.name = name;
		}

		/**
		 * Set the value for the constructor argument.
		 */
		public void setValue(Object value) {
			this.value = value;
		}

		/**
		 * Return the value for the constructor argument.
		 */
		public Object getValue() {
			return this.value;
		}

		/**
		 * Set the type of the constructor argument.
		 */
		public void setType(String type) {
			this.type = type;
		}

		/**
		 * Return the type of the constructor argument.
		 */
		public String getType() {
			return this.type;
		}

		/**
		 * Set the name of the constructor argument.
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Return the name of the constructor argument.
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Set the configuration source {@code Object} for this metadata element.
		 * <p>
		 * The exact type of the object will depend on the configuration mechanism used.
		 */
		public void setSource(Object source) {
			this.source = source;
		}

		public Object getSource() {
			return this.source;
		}

		/**
		 * Return whether this holder contains a converted value already ({@code true}),
		 * or whether the value still needs to be converted ({@code false}).
		 */
		public synchronized boolean isConverted() {
			return this.converted;
		}

		/**
		 * Set the converted value of the constructor argument, after processed type
		 * conversion.
		 */
		public synchronized void setConvertedValue(Object value) {
			this.converted = (value != null);
			this.convertedValue = value;
		}

		/**
		 * Return the converted value of the constructor argument, after processed type
		 * conversion.
		 */
		public synchronized Object getConvertedValue() {
			return this.convertedValue;
		}

		/**
		 * Determine whether the content of this ValueHolder is equal to the content of
		 * the given other ValueHolder.
		 * <p>
		 * Note that ValueHolder does not implement {@code equals} directly, to allow
		 * for multiple ValueHolder instances with the same content to reside in the
		 * same Set.
		 */
		private boolean contentEquals(ValueHolder other) {
			return (this == other || (ObjectUtils.nullSafeEquals(this.value, other.value)
					&& ObjectUtils.nullSafeEquals(this.type, other.type)));
		}

		/**
		 * Determine whether the hash code of the content of this ValueHolder.
		 * <p>
		 * Note that ValueHolder does not implement {@code hashCode} directly, to allow
		 * for multiple ValueHolder instances with the same content to reside in the
		 * same Set.
		 */
		private int contentHashCode() {
			return ObjectUtils.nullSafeHashCode(this.value) * 29 + ObjectUtils.nullSafeHashCode(this.type);
		}

		/**
		 * Create a copy of this ValueHolder: that is, an independent ValueHolder
		 * instance with the same contents.
		 */
		public ValueHolder copy() {
			ValueHolder copy = new ValueHolder(this.value, this.type, this.name);
			copy.setSource(this.source);
			return copy;
		}
	}

	@Override
	public int compare(ConstructorArgumentValues o1, ConstructorArgumentValues o2) {
		if (o1.getArgumentCount() - o2.getArgumentCount() == 0) {
			return 0;
		}
		return o1.getArgumentCount() - o2.getArgumentCount() > 0 ? 1: -1;
	}
}
