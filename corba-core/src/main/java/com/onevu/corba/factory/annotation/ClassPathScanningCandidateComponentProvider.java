package com.onevu.corba.factory.annotation;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.onevu.corba.beans.domain.AbstractBeanDefinition;
import com.onevu.corba.beans.domain.BeanDefinition;
import com.onevu.corba.beans.domain.BeanDefinitionHolder;
import com.onevu.corba.beans.domain.ConstructorArgumentValues;
import com.onevu.corba.beans.domain.ConstructorArgumentValues.ValueHolder;
import com.onevu.corba.beans.domain.RootBeanDefinition;
import com.onevu.corba.constants.DelimiterConstants;
import com.onevu.corba.exception.BeanDefinitionStoreException;
import com.onevu.corba.exception.BeanInstantiationException;
import com.onevu.corba.exception.NoSuchBeanDefinitionException;
import com.onevu.corba.factory.support.BeanDefinitionReaderUtils;
import com.onevu.corba.factory.support.BeanDefinitionRegistry;
import com.onevu.corba.factory.support.DefaultListableBeanFactory;
import com.onevu.corba.io.FileSystemResource;
import com.onevu.corba.io.Resource;
import com.onevu.corba.stereotype.CorbaComponent;
import com.onevu.corba.stereotype.CorbaServant;
import com.onevu.corba.util.AntPathMatcher;
import com.onevu.corba.util.Assert;
import com.onevu.corba.util.BeanUtils;
import com.onevu.corba.util.ClassUtils;
import com.onevu.corba.util.ResourceUtils;
import com.onevu.corba.util.StringUtils;

public class ClassPathScanningCandidateComponentProvider {
	
	private final BeanDefinitionRegistry registry;
	
	private AntPathMatcher pathMatcher = new AntPathMatcher();
	
	private List<Class<? extends Annotation>> annotations = new ArrayList<>();
	
	public ClassPathScanningCandidateComponentProvider() {
		this(new DefaultListableBeanFactory());
	}
	
	public ClassPathScanningCandidateComponentProvider(BeanDefinitionRegistry registry) {
		this(registry, true);
	}
	
	public ClassPathScanningCandidateComponentProvider(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
		this.registry = registry;
		if (useDefaultFilters) {
			annotations.add(CorbaComponent.class);
			annotations.add(CorbaServant.class);
		}
	}
	
	public void scan(String ...basePackages) throws ClassNotFoundException, IOException, BeanDefinitionStoreException {
		Assert.notNull(basePackages, "Base packages must not null");
		for (String basePackage: basePackages) {
			Set<BeanDefinition> beanDefinitions = findCandidateComponents(basePackage);
			for (BeanDefinition beanDefinition: beanDefinitions) {
				if (!registry.containsBeanDefinition(beanDefinition.getBeanClassName())) {
					BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(beanDefinition, beanDefinition.getBeanClassName()), registry);
				}
			}
		}
	}
	
	public void refresh() throws BeanInstantiationException, NoSuchBeanDefinitionException {
		String []beanNames = this.registry.getBeanDefinitionNames();
		for (String beanName: beanNames) {
			AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) this.registry.getBeanDefinition(beanName);
			if (!beanDefinition.isAbstract() && !beanDefinition.hasConstructorArgumentValues()) {
				beanDefinition.setAutowireCandidate(false);
				Object source = BeanUtils.instantiateClass((Class)beanDefinition.getBeanClass());
				beanDefinition.setSource(source);
			}
		}
		for (String beanName: beanNames) {
			AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) this.registry.getBeanDefinition(beanName);
			if (!beanDefinition.isAbstract() && beanDefinition.getSource() == null) {
				Class clazz = (Class) beanDefinition.getBeanClass();
				List<ConstructorArgumentValues> argumentValues = beanDefinition.fetchConstructorArgument();
				for (ConstructorArgumentValues values: argumentValues) {
					Set<Entry<Integer, ValueHolder>> entry = values.getIndexedArgumentValues().entrySet();
					List<Object> argsObj = new ArrayList<Object>();
					for (Entry<Integer, ValueHolder> agrument: entry) {
						ValueHolder args = agrument.getValue();
						String className = StringUtils.cleanInitialClass(args.getValue().toString());
						AbstractBeanDefinition constructorBean = (AbstractBeanDefinition) this.registry.getInitializedBeanDefinition(className);
						if (constructorBean == null) {
							throw new NoSuchBeanDefinitionException("No Bean Definition found for "+className);
						}
						argsObj.add(constructorBean.getSource());
					}
					Object source = BeanUtils.instantiateClass(values.getCtor(), argsObj.toArray(new Object[argsObj.size()]));
					beanDefinition.setSource(source);
					
				}
			}
		}
	}
	
	public BeanDefinitionRegistry getRegistry() {
		return registry;
	}

	public Set<BeanDefinition> findCandidateComponents(String basePackage) throws IOException, ClassNotFoundException {
		Set<BeanDefinition> candidates = new LinkedHashSet<BeanDefinition>();
		String packageSearchPath = DelimiterConstants.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + "/" + DelimiterConstants.DEFAULT_RESOURCE_PATTERN;
		Resource[] resources = getResources(packageSearchPath);
		for (Resource resource: resources) {
			String filePath = resource.getFile().getAbsolutePath();
			String baseClassName = ClassUtils.convertResourceToPath(basePackage, filePath);
			AbstractBeanDefinition beanDefinition = BeanDefinitionReaderUtils.createBeanDefinition(baseClassName, ClassUtils.getDefaultClassLoader());
			if (hasComponentAnnotation((Class<?>)beanDefinition.getBeanClass())) {
				candidates.add(beanDefinition);
			}
		}
		return candidates;
	}
	
	public boolean hasComponentAnnotation(Class<?> clazz) {
		Assert.notNull(clazz, "Class object must not null");
		boolean isPresent = false;
		for (Class<? extends Annotation> annotation: annotations) {
			if (clazz.isAnnotationPresent(annotation)) {
				isPresent = true;
				break;
			}
		}
		return isPresent;
	}
	
	protected String resolveBasePackage(String basePackage) {
		return ClassUtils.convertClassNameToResourcePath(basePackage);
	}
	
	protected Resource[] findAllClassPathResources(String location) throws IOException {
		String path = location;
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		ClassLoader cl = ClassUtils.getDefaultClassLoader();
		Enumeration<URL> resourceUrls = (cl != null ? cl.getResources(path) : ClassLoader.getSystemResources(path));
		Set<Resource> result = new LinkedHashSet<Resource>(16);
		while (resourceUrls.hasMoreElements()) {
			URL url = resourceUrls.nextElement();
			result.add(ResourceUtils.convertClassLoaderURL(url));
		}
		return result.toArray(new Resource[result.size()]);
	}
	
	protected Resource[] findPathMatchingResources(String locationPattern) throws IOException {
		String rootDirPath = ResourceUtils.determineRootDir(locationPattern);
		String subPattern = locationPattern.substring(rootDirPath.length());
		Resource[] rootDirResources = getResources(rootDirPath);
		Set<Resource> result = new LinkedHashSet<Resource>(16);
		for (Resource rootDirResource : rootDirResources) {
			if (ResourceUtils.isJarURL(rootDirResource.getURL())) {
				result.addAll(doFindPathMatchingJarResources(rootDirResource, subPattern));
			}
			else {
				result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
			}
		}
		return result.toArray(new Resource[result.size()]);
	}
	
	protected Set<Resource> doFindPathMatchingJarResources(Resource rootDirResource, String subPattern)
			throws IOException {

		URLConnection con = rootDirResource.getURL().openConnection();
		JarFile jarFile;
		String jarFileUrl;
		String rootEntryPath;
		boolean newJarFile = false;

		if (con instanceof JarURLConnection) {
			// Should usually be the case for traditional JAR files.
			JarURLConnection jarCon = (JarURLConnection) con;
			ResourceUtils.useCachesIfNecessary(jarCon);
			jarFile = jarCon.getJarFile();
			jarFileUrl = jarCon.getJarFileURL().toExternalForm();
			JarEntry jarEntry = jarCon.getJarEntry();
			rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
		}
		else {
			// No JarURLConnection -> need to resort to URL file parsing.
			// We'll assume URLs of the format "jar:path!/entry", with the protocol
			// being arbitrary as long as following the entry format.
			// We'll also handle paths with and without leading "file:" prefix.
			String urlFile = rootDirResource.getURL().getFile();
			int separatorIndex = urlFile.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
			if (separatorIndex != -1) {
				jarFileUrl = urlFile.substring(0, separatorIndex);
				rootEntryPath = urlFile.substring(separatorIndex + ResourceUtils.JAR_URL_SEPARATOR.length());
				jarFile = getJarFile(jarFileUrl);
			}
			else {
				jarFile = new JarFile(urlFile);
				jarFileUrl = urlFile;
				rootEntryPath = "";
			}
			newJarFile = true;
		}

		try {
			if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
				// Root entry path must end with slash to allow for proper matching.
				// The Sun JRE does not return a slash here, but BEA JRockit does.
				rootEntryPath = rootEntryPath + "/";
			}
			Set<Resource> result = new LinkedHashSet<Resource>(8);
			for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
				JarEntry entry = entries.nextElement();
				String entryPath = entry.getName();
				if (entryPath.startsWith(rootEntryPath)) {
					String relativePath = entryPath.substring(rootEntryPath.length());
					if (getPathMatcher().match(subPattern, relativePath)) {
						result.add(rootDirResource.createRelative(relativePath));
					}
				}
			}
			return result;
		}
		finally {
			// Close jar file, but only if freshly obtained -
			// not from JarURLConnection, which might cache the file reference.
			if (newJarFile) {
				jarFile.close();
			}
		}
	}
	protected JarFile getJarFile(String jarFileUrl) throws IOException {
		if (jarFileUrl.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
			try {
				return new JarFile(ResourceUtils.toURI(jarFileUrl).getSchemeSpecificPart());
			}
			catch (URISyntaxException ex) {
				// Fallback for URLs that are not valid URIs (should hardly ever happen).
				return new JarFile(jarFileUrl.substring(ResourceUtils.FILE_URL_PREFIX.length()));
			}
		}
		else {
			return new JarFile(jarFileUrl);
		}
	}
	
	protected Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String subPattern)
			throws IOException {

		File rootDir;
		try {
			rootDir = rootDirResource.getFile().getAbsoluteFile();
		}
		catch (IOException ex) {
			return Collections.emptySet();
		}
		return doFindMatchingFileSystemResources(rootDir, subPattern);
	}
	
	protected Set<Resource> doFindMatchingFileSystemResources(File rootDir, String subPattern) throws IOException {
		Set<File> matchingFiles = retrieveMatchingFiles(rootDir, subPattern);
		Set<Resource> result = new LinkedHashSet<Resource>(matchingFiles.size());
		for (File file : matchingFiles) {
			result.add(new FileSystemResource(file));
		}
		return result;
	}
	
	public Resource[] getResources(String locationPattern) throws IOException {
		Assert.notNull(locationPattern, "Location pattern must not be null");
		if (locationPattern.startsWith(DelimiterConstants.CLASSPATH_ALL_URL_PREFIX)) {
			// a class path resource (multiple resources for same name possible)
			if (getPathMatcher().isPattern(locationPattern.substring(DelimiterConstants.CLASSPATH_ALL_URL_PREFIX.length()))) {
				// a class path resource pattern
				return findPathMatchingResources(locationPattern);
			}
			else {
				// all class path resources with the given name
				return findAllClassPathResources(locationPattern.substring(DelimiterConstants.CLASSPATH_ALL_URL_PREFIX.length()));
			}
		}
		else {
			// Only look for a pattern after a prefix here
			// (to not get fooled by a pattern symbol in a strange prefix).
			int prefixEnd = locationPattern.indexOf(":") + 1;
			if (getPathMatcher().isPattern(locationPattern.substring(prefixEnd))) {
				// a file pattern
				return findPathMatchingResources(locationPattern);
			}
			else {
				// a single resource with the given name
				return new Resource[0];
			}
		}
	}
	
	protected Set<File> retrieveMatchingFiles(File rootDir, String pattern) throws IOException {
		if (!rootDir.exists()) {
			return Collections.emptySet();
		}
		if (!rootDir.isDirectory()) {
			return Collections.emptySet();
		}
		if (!rootDir.canRead()) {
			return Collections.emptySet();
		}
		String fullPattern = StringUtils.replace(rootDir.getAbsolutePath(), File.separator, "/");
		if (!pattern.startsWith("/")) {
			fullPattern += "/";
		}
		fullPattern = fullPattern + StringUtils.replace(pattern, File.separator, "/");
		Set<File> result = new LinkedHashSet<File>(8);
		doRetrieveMatchingFiles(fullPattern, rootDir, result);
		return result;
	}
	
	protected void doRetrieveMatchingFiles(String fullPattern, File dir, Set<File> result) throws IOException {
		File[] dirContents = dir.listFiles();
		if (dirContents == null) {
			return;
		}
		for (File content : dirContents) {
			String currPath = StringUtils.replace(content.getAbsolutePath(), File.separator, "/");
			if (content.isDirectory() && getPathMatcher().matchStart(fullPattern, currPath + "/")) {
				if (!content.canRead()) {
				}
				else {
					doRetrieveMatchingFiles(fullPattern, content, result);
				}
			}
			if (getPathMatcher().match(fullPattern, currPath)) {
				result.add(content);
			}
		}
	}

	public AntPathMatcher getPathMatcher() {
		return pathMatcher;
	}	
	
}
