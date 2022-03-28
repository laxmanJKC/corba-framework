package com.onevue.spring.configuration;

import java.lang.annotation.Annotation;
import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;

public abstract class AbstractCorbaBeanRegistry
		implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

	private @SuppressWarnings("null") @NonNull ResourceLoader resourceLoader;
	private @SuppressWarnings("null") @NonNull Environment environment;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		if (getIncludeFilter() != null) {
			configureBeanDefinition(metadata, registry);
		}
	}

	public void configureBeanDefinition(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		AnnotationCorbaConfigurationSource corbaConfigurationSource = new AnnotationCorbaConfigurationSource(metadata,
				getAnnotation(), environment, registry);
		Streamable<String> streamable = corbaConfigurationSource.getBasePackages();
		if (streamable.isEmpty()) {
			return;
		}
		List<String> packageList = streamable.toList();
		String[] packages = packageList.toArray(new String[packageList.size()]);
		ClassPathBeanDefinitionScanner classPathBeanDefinitionScanner = new ClassPathBeanDefinitionScanner(registry);
		classPathBeanDefinitionScanner.addIncludeFilter(getIncludeFilter());
		classPathBeanDefinitionScanner.scan(packages);
	}

	protected abstract Class<? extends Annotation> getAnnotation();

	protected abstract TypeFilter getIncludeFilter();

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
