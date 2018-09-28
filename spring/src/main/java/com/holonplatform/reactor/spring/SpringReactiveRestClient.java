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
package com.holonplatform.reactor.spring;

import org.springframework.web.reactive.function.client.WebClient;

import com.holonplatform.reactor.http.ReactiveRestClient;
import com.holonplatform.reactor.spring.internal.WebClientReactiveRestClient;

/**
 * A {@link ReactiveRestClient} using Spring {@link WebClient} to perform invocations.
 *
 * @since 5.2.0
 */
public interface SpringReactiveRestClient extends ReactiveRestClient {

	/**
	 * Get the {@link WebClient} bound to this RestClient.
	 * @return The {@link WebClient} reference
	 */
	WebClient getClient();

	/**
	 * Create a {@link ReactiveRestClient} using given <code>webClient</code>.
	 * @param webClient {@link WebClient} to use to perform invocations (not null)
	 * @return A new {@link ReactiveRestClient} instance
	 */
	static ReactiveRestClient create(WebClient webClient) {
		return new WebClientReactiveRestClient(webClient);
	}

}
