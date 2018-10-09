/*
 * Copyright 2016-2018 Axioma srl.
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
package com.holonplatform.reactor.spring.internal;

import java.util.Optional;

import org.springframework.web.reactive.function.client.WebClient;

import com.holonplatform.core.Context;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.http.exceptions.RestClientCreationException;
import com.holonplatform.reactor.http.ReactiveRestClient;
import com.holonplatform.reactor.http.ReactiveRestClientFactory;
import com.holonplatform.reactor.spring.SpringReactiveRestClient;
import com.holonplatform.spring.internal.SpringLogger;

/**
 * A {@link ReactiveRestClientFactory} to provide {@link SpringReactiveRestClient} instances.
 *
 * @since 5.2.0
 */
public class WebClientReactiveRestClientFactory implements ReactiveRestClientFactory {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = SpringLogger.create();

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.ReactiveRestClientFactory#getRestClientImplementationClass()
	 */
	@Override
	public Class<?> getRestClientImplementationClass() {
		return SpringReactiveRestClient.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.ReactiveRestClientFactory#create(java.lang.ClassLoader)
	 */
	@Override
	public ReactiveRestClient create(ClassLoader classLoader) throws RestClientCreationException {
		Optional<WebClient> webClient = Context.get().resource("webClient", WebClient.class, classLoader);
		if (webClient.isPresent()) {
			return SpringReactiveRestClient.create(webClient.get());
		}
		LOGGER.debug(() -> "No WebClient type Context resource available - ReactiveRestClient creation skipped");
		return null;
	}

}
