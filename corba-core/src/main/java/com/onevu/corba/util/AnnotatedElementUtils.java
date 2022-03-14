package com.onevu.corba.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class AnnotatedElementUtils {

	public static boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
		// Shortcut: directly present on the element, with no merging needed?
		return element.isAnnotationPresent(annotationType);
		// Exhaustive retrieval of merged annotations...
//		return findAnnotations(element).isPresent(annotationType);
	}
}
