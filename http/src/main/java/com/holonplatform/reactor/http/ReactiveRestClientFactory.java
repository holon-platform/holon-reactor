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
package com.holonplatform.reactor.http;

import java.util.ServiceLoader;

import javax.annotation.Priority;

import com.holonplatform.http.exceptions.RestClientCreationException;

/**
 * Factory to create {@link ReactiveRestClient} instances.
 * 
 * <p>
 * {@link ReactiveRestClientFactory} registration can be performed using default Java extension through
 * {@link ServiceLoader}, providing a <code>com.holonplatform.reactor.http.ReactiveRestClientFactory</code> file in
 * <code>META-INF/services</code> containing the ReactiveRestClientFactory concrete class names to register.
 * </p>
 * 
 * <p>
 * The {@link ReactiveRestClientFactory} priority declaration is supported using the {@link Priority} annotation on
 * factory class (where less priority value means higher priority order).
 * </p>
 * 
 * @since 5.2.0
 */
public interface ReactiveRestClientFactory {

	/**
	 * Default factory priority
	 */
	public static final int DEFAULT_PRIORITY = 100;

	/**
	 * Get the {@link ReactiveRestClient} implementation class returned by this factory.
	 * @return the {@link ReactiveRestClient} implementation class
	 */
	Class<?> getRestClientImplementationClass();

	/**
	 * Create a new {@link ReactiveRestClient} instance.
	 * @param classLoader The {@link ClassLoader} for which the instance creation is requested
	 * @return A new {@link ReactiveRestClient} instance
	 * @throws RestClientCreationException If an error occurred
	 */
	ReactiveRestClient create(ClassLoader classLoader) throws RestClientCreationException;

}
