package com.onevue.spring.beans.scanner;

import java.util.Set;

import org.omg.PortableServer.Servant;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

public class POABeanScanner {

	public void scanPOA(String packages) {
		ClassPathScanningCandidateComponentProvider candidateComponentProvider = new ClassPathScanningCandidateComponentProvider(
				false);
		candidateComponentProvider.addIncludeFilter(new AssignableTypeFilter(Servant.class));
		Set<BeanDefinition> beanDefinitions = candidateComponentProvider.findCandidateComponents(packages);
	}
}
