/*
 * Copyright 2000-2017 Holon TDCN.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.reactor.http.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.WeakHashMap;

import javax.annotation.Priority;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.http.exceptions.RestClientCreationException;
import com.holonplatform.http.internal.HttpLogger;
import com.holonplatform.reactor.http.ReactiveRestClient;
import com.holonplatform.reactor.http.ReactiveRestClientFactory;

/**
 * {@link ReactiveRestClientFactory} registry.
 *
 * @since 5.2.0
 */
public enum ReactiveRestClientFactoryRegistry {

	INSTANCE;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = HttpLogger.create();

	/**
	 * {@link Priority} based comparator.
	 */
	private static final Comparator<ReactiveRestClientFactory> PRIORITY_COMPARATOR = Comparator.comparingInt(
			p -> p.getClass().isAnnotationPresent(Priority.class) ? p.getClass().getAnnotation(Priority.class).value()
					: ReactiveRestClientFactory.DEFAULT_PRIORITY);

	/**
	 * The {@link ReactiveRestClientFactory}s by class name organized by the {@link ClassLoader} was used to load them.
	 */
	private final WeakHashMap<ClassLoader, List<ReactiveRestClientFactory>> factories;

	private ReactiveRestClientFactoryRegistry() {
		factories = new WeakHashMap<>();
	}

	/**
	 * Get a {@link ReactiveRestClient} instance for given <code>fullyQualifiedClassName</code> and {@link ClassLoader}
	 * using a suitable {@link ReactiveRestClientFactory}.
	 * @param fullyQualifiedClassName {@link ReactiveRestClient} class name to obtain, or <code>null</code> for the
	 *        default one
	 * @param classLoader ClassLoader for which to obtain the {@link ReactiveRestClient}
	 * @return A new {@link ReactiveRestClient} instance for given <code>fullyQualifiedClassName</code> (or the default
	 *         one if <code>null</code>) and {@link ClassLoader}
	 * @throws RestClientCreationException If no {@link ReactiveRestClientFactory} available, or none of the available
	 *         factories returned a not <code>null</code> instance or a creation error occurred
	 */
	public ReactiveRestClient createRestClient(String fullyQualifiedClassName, ClassLoader classLoader) {
		ClassLoader cl = classLoader == null ? ClassUtils.getDefaultClassLoader() : classLoader;
		List<ReactiveRestClientFactory> restClientFactories = getRestClientFactories(fullyQualifiedClassName, cl);
		if (restClientFactories.isEmpty()) {
			throw new RestClientCreationException("No ReactiveRestClientFactory available for ClassLoader [" + cl + "]"
					+ ((fullyQualifiedClassName != null)
							? " and ReactiveRestClient implementation class name [" + fullyQualifiedClassName + "]"
							: ""));
		}
		ReactiveRestClient restClient = null;
		for (ReactiveRestClientFactory factory : restClientFactories) {
			restClient = factory.create(cl);
			if (restClient != null) {
				break;
			}
		}
		if (restClient == null) {
			throw new RestClientCreationException("No ReactiveRestClient available for ClassLoader [" + cl + "]"
					+ ((fullyQualifiedClassName != null)
							? " and ReactiveRestClient implementation class name [" + fullyQualifiedClassName + "]"
							: ""));
		}
		return restClient;
	}

	/**
	 * Get the {@link ReactiveRestClientFactory} for given <code>fullyQualifiedClassName</code>
	 * {@link ReactiveRestClient} instance and {@link ClassLoader}.
	 * @param fullyQualifiedClassName {@link ReactiveRestClientFactory} class name to obtain, or <code>null</code> for
	 *        the default one
	 * @param classLoader ClassLoader for which to obtain the factory
	 * @return {@link ReactiveRestClientFactory} for given <code>fullyQualifiedClassName</code> (or the default one if
	 *         <code>null</code>) and {@link ClassLoader}
	 * @throws RestClientCreationException If no {@link ReactiveRestClientFactory} available or a creation error
	 *         occurred
	 */
	public List<ReactiveRestClientFactory> getRestClientFactories(String fullyQualifiedClassName,
			ClassLoader classLoader) {
		ClassLoader serviceClassLoader = classLoader == null ? ClassUtils.getDefaultClassLoader() : classLoader;
		List<ReactiveRestClientFactory> restClientFactories = getRestClientFactories(serviceClassLoader);
		if (fullyQualifiedClassName != null) {
			List<ReactiveRestClientFactory> byName = new LinkedList<>();
			for (ReactiveRestClientFactory factory : restClientFactories) {
				Class<?> cls = factory.getRestClientImplementationClass();
				if (cls != null && cls.getName().equals(fullyQualifiedClassName)) {
					byName.add(factory);
				}
			}
			return byName;
		}
		return restClientFactories;
	}

	/**
	 * Obtain the {@link ReactiveRestClientFactory}s that are available via the specified {@link ClassLoader}.
	 * @param classLoader the {@link ClassLoader} of the returned {@link ReactiveRestClientFactory}s
	 * @return an list of {@link ReactiveRestClientFactory}s loaded by the specified {@link ClassLoader}
	 */
	private synchronized List<ReactiveRestClientFactory> getRestClientFactories(ClassLoader classLoader) {

		final ClassLoader serviceClassLoader = classLoader == null ? ClassUtils.getDefaultClassLoader() : classLoader;
		List<ReactiveRestClientFactory> restClientFactories = factories.get(serviceClassLoader);

		if (restClientFactories == null) {
			restClientFactories = AccessController
					.doPrivileged(new PrivilegedAction<List<ReactiveRestClientFactory>>() {
						@Override
						public List<ReactiveRestClientFactory> run() {
							LinkedList<ReactiveRestClientFactory> result = new LinkedList<>();
							ServiceLoader<ReactiveRestClientFactory> serviceLoader = ServiceLoader
									.load(ReactiveRestClientFactory.class, serviceClassLoader);
							for (ReactiveRestClientFactory factory : serviceLoader) {
								result.add(factory);
								LOGGER.debug(() -> "Loaded and registered ReactiveRestClientFactory ["
										+ factory.getClass().getName() + "]");
							}
							// sort
							Collections.sort(result, PRIORITY_COMPARATOR);
							return result;
						}
					});
			factories.put(serviceClassLoader, restClientFactories);
		}
		return restClientFactories;
	}

}
