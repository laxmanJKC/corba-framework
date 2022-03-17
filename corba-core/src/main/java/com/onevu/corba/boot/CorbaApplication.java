package com.onevu.corba.boot;

import static com.onevu.corba.constants.CorbaConstants.ENVIRONMENT_KEY;
import static com.onevu.corba.constants.CorbaConstants.CORBA_ROOT_POA;
import static com.onevu.corba.constants.CorbaConstants.CORBA_ONEVU;
import static com.onevu.corba.constants.CorbaConstants.CORBA_PROPERTIES;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContext;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

import com.onevu.corba.beans.domain.AbstractBeanDefinition;
import com.onevu.corba.beans.domain.BeanDefinition;
import com.onevu.corba.beans.domain.RootBeanDefinition;
import com.onevu.corba.exception.BeanDefinitionStoreException;
import com.onevu.corba.exception.BeanInstantiationException;
import com.onevu.corba.exception.NoSuchBeanDefinitionException;
import com.onevu.corba.exception.OnevuCorbaException;
import com.onevu.corba.factory.annotation.ClassPathScanningCandidateComponentProvider;
import com.onevu.corba.factory.support.BeanDefinitionRegistry;
import com.onevu.corba.io.PropertySources;
import com.onevu.corba.util.Assert;
import com.onevu.corba.util.CorbaUtils;

public class CorbaApplication {
	
	private PropertySources doMain(Class<?> clazz, String ...args) {
		PropertySources propertySources = new PropertySources();
		if (args == null || args.length ==0) {
			return propertySources;
		}
		Properties properties = new Properties();
		for (String arg: args) {
			if (arg.startsWith(CORBA_PROPERTIES) || arg.startsWith("-D" + CORBA_PROPERTIES)) {
				String []value = arg.split("=");
				InputStream inputStream;
				try {
					inputStream = new FileInputStream(value[1]);
					properties.load(inputStream);
				} catch (FileNotFoundException e) {
					throw new OnevuCorbaException("No configuration file");
				} catch (IOException e) {
					throw new OnevuCorbaException("Incorrect Configuration File");
				}
				Set<Entry<Object, Object>> property = properties.entrySet();
				for (Entry<Object, Object> entry: property) {
					propertySources.addProperty((String)entry.getKey(), (String)entry.getValue());
				}
			}
		}
		return propertySources;
	}
	
	public static BeanDefinitionRegistry run(Class<?> clazz, String ...args) throws Exception {
		Assert.notNull(clazz, "Class Name not found");
		CorbaApplication corbaApplication = new CorbaApplication();
		PropertySources propertySources = corbaApplication.doMain(clazz, args);
		String packageName = clazz.getPackage().getName();
		ClassPathScanningCandidateComponentProvider candidateComponentProvider = new ClassPathScanningCandidateComponentProvider();
		BeanDefinitionRegistry beanDefinitionRegistry = candidateComponentProvider.getRegistry();
		AbstractBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(propertySources.getClass());
		beanDefinition.setSource(propertySources);
		beanDefinitionRegistry.registerBeanDefinition(ENVIRONMENT_KEY, beanDefinition);
		try {
			candidateComponentProvider.scan(packageName);
			candidateComponentProvider.refresh();
			corbaApplication.configureCorbaServer(beanDefinitionRegistry, args);
		} catch (ClassNotFoundException | IOException | BeanDefinitionStoreException e) {
			throw e;
		} catch (BeanInstantiationException e) {
			throw e;
		} catch (NoSuchBeanDefinitionException e) {
			throw e;
		}
		return beanDefinitionRegistry;
	}
	
	private void configureCorbaServer(BeanDefinitionRegistry registry, String ...args) throws NoSuchBeanDefinitionException, BeanDefinitionStoreException {
		BeanDefinition propertySourcesBeanDefinition = registry.getBeanDefinition(ENVIRONMENT_KEY);
		PropertySources propertySources = propertySourcesBeanDefinition.getBean(PropertySources.class);
		Properties properties = propertySources.toProperties();
		String serverName = properties.getProperty("corba.server.name");
		String flow = properties.getProperty("corba.server."+serverName+".flow");
		String []configFlows = flow.split(",");
		for(String configFlow: configFlows) {
			switch (configFlow) {
			case "initializeCorba":
				ORB orb = CorbaUtils.initializeCorba(properties, args);
				RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(orb.getClass());
				rootBeanDefinition.setSource(orb);
				registry.registerBeanDefinition("ORB", rootBeanDefinition);
				break;
			
			case "rootPOA":
				BeanDefinition orbBeanDefinition = registry.getBeanDefinition("ORB");
				POA rootPOA = CorbaUtils.rootPOA(orbBeanDefinition.getBean(ORB.class));
				RootBeanDefinition rootPOABeanDefinition = new RootBeanDefinition(rootPOA.getClass());
				rootPOABeanDefinition.setSource(rootPOA);
				registry.registerBeanDefinition(CORBA_ROOT_POA, rootPOABeanDefinition);
				break;
			case "servantToReference":
				BeanDefinition rootPOAbBeanDefinition = registry.getBeanDefinition(CORBA_ROOT_POA);
				BeanDefinition servantToReferenceBean = registry.getBeanDefinition(properties.getProperty("corba.server.hello.poa"));
				org.omg.CORBA.Object servantToReference = CorbaUtils.servantToReference(rootPOAbBeanDefinition.getBean(POA.class), servantToReferenceBean.getBean(Servant.class));
				RootBeanDefinition servantBeanDefinition = new RootBeanDefinition(servantToReference.getClass());
				servantBeanDefinition.setSource(servantToReference);
				registry.registerBeanDefinition("serverCorbaObjRef", servantBeanDefinition);
				break;
			case "fetchNSRootContext":
				BeanDefinition orbbBeanDefinition = registry.getBeanDefinition("ORB");
				NamingContext namingContext = CorbaUtils.fetchNSRootContext(orbbBeanDefinition.getBean(ORB.class), null, false);
				RootBeanDefinition namingContextBeanDefinition = new RootBeanDefinition(namingContext.getClass());
				namingContextBeanDefinition.setSource(namingContext);
				registry.registerBeanDefinition("namingContext", namingContextBeanDefinition);
				break;
			case "registerName":
				BeanDefinition orbBBeanDefinition = registry.getBeanDefinition("ORB");
				BeanDefinition obhRefBeanDefinition = registry.getBeanDefinition("serverCorbaObjRef");
				CorbaUtils.registerName(orbBBeanDefinition.getBean(ORB.class), CORBA_ONEVU+ serverName, obhRefBeanDefinition.getBean(org.omg.CORBA.Object.class), false);
				break;
			default:
				break;
			}
		}
		BeanDefinition orbBBeanDefinition = registry.getBeanDefinition("ORB");
		ORB orb = orbBBeanDefinition.getBean(ORB.class);
		CorbaUtils.startCorbaServer(orb);
	}
}
