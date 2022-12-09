package com.researchspace.api.client.examples;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.client.ApiConnectorImpl;
import com.researchspace.api.client.ConfigPropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FixedIntervalTest {
    ConfigPropertiesReader configReader = new ConfigPropertiesReader();
    String configuredApiKey = configReader.getConfigProperty("apiKey");

    protected ApiConnector createApiConnector() throws IOException {
        ApiConnectorImpl apiConnector = new ApiConnectorImpl();
        return (ApiConnector) Proxy.newProxyInstance(FixedIntervalTest.class.getClassLoader(),
                apiConnector.getClass().getInterfaces(),
                new DelayAPIExecutor<ApiConnectorImpl>(apiConnector));
    }
	
    // proxy object to interpose a delay between requests
    class DelayAPIExecutor<T> implements InvocationHandler {
        private static final int ONE_SECOND = 1000;
        private T object;

        public DelayAPIExecutor(T object) {
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

}
