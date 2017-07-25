package com.researchspace.api.client.examples;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.client.ApiConnectorImpl;

public abstract class FixedIntervalTest {

	 ApiConnector createApiConnector() throws IOException {
		ApiConnectorImpl apiConnector = new ApiConnectorImpl();
		return (ApiConnector)Proxy
                .newProxyInstance(FixedIntervalTest.class.getClassLoader(),
                		apiConnector.getClass().getInterfaces(), new DelayAPIExecutor<ApiConnectorImpl>(apiConnector));
	}
	
	// proxy object to interpose a delay between requests
	 class DelayAPIExecutor <T> implements InvocationHandler {
			private static final int ONE_SECOND = 1000;
			private T object;
			public DelayAPIExecutor(T object) {
				super();
				this.object = object;
			}
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Thread.currentThread().sleep(ONE_SECOND);
				try {
					return method.invoke(object, args);
				} catch (InvocationTargetException e) {
					throw e.getTargetException();
				}
			}
		}
	 
	 /* returns property value from config file */
	    protected String getConfigProperty(String propertyName) {
	        Properties prop = new Properties();
	        String propertyValue = "";
	    
	        try (InputStream input = new FileInputStream("config.properties")) {
	            prop.load(input);
	            propertyValue = prop.getProperty(propertyName);
	        } catch ( IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IllegalArgumentException(String.format("Property %s not found", propertyName));
			}
	        return propertyValue;
	    }

}
