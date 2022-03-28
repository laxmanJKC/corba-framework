package com.onevue.spring.configuration;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;

public class AnnotationCorbaConfigurationSource {

	private static final String BASE_PACKAGES = "basePackages";
	private static final String BASE_PACKAGE_CLASSES = "basePackageClasses";

	private final AnnotationMetadata configMetadata;
	private final AnnotationMetadata enableAnnotationMetadata;
	private final AnnotationAttributes attributes;

	public AnnotationCorbaConfigurationSource(AnnotationMetadata metadata, Class<? extends Annotation> annotation,
			Environment environment, BeanDefinitionRegistry registry) {

		Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(annotation.getName());

		if (annotationAttributes == null) {
			throw new IllegalStateException(
					String.format("Unable to obtain annotation attributes for %s!", annotation));
		}

		this.attributes = new AnnotationAttributes(annotationAttributes);
		this.enableAnnotationMetadata = AnnotationMetadata.introspect(annotation);
		this.configMetadata = metadata;
	}

	public Streamable<String> getBasePackages() {

		String[] value = attributes.getStringArray("value");
		String[] basePackages = attributes.getStringArray(BASE_PACKAGES);
		Class<?>[] basePackageClasses = attributes.getClassArray(BASE_PACKAGE_CLASSES);

		// Default configuration - return package of annotated class
		if (value.length == 0 && basePackages.length == 0 && basePackageClasses.length == 0) {

			String className = configMetadata.getClassName();
			return Streamable.of(ClassUtils.getPackageName(className));
		}

		Set<String> packages = new HashSet<>();
		packages.addAll(Arrays.asList(value));
		packages.addAll(Arrays.asList(basePackages));

		Arrays.stream(basePackageClasses)//
				.map(ClassUtils::getPackageName)//
				.forEach(it -> packages.add(it));

		return Streamable.of(packages);
	}

	public AnnotationMetadata getEnableAnnotationMetadata() {
		return enableAnnotationMetadata;
	}

	public AnnotationAttributes getAttributes() {
		return attributes;
	}

	@NonNull
	public Object getSource() {
		return configMetadata;
	}
}
