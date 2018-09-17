/*
 * Copyright 2016-2017 Axioma srl.
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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.http.HttpMethod;
import com.holonplatform.http.exceptions.HttpClientInvocationException;
import com.holonplatform.http.exceptions.UnsuccessfulResponseException;
import com.holonplatform.http.internal.rest.AbstractRequestDefinition;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.http.rest.ResponseEntity;
import com.holonplatform.http.rest.ResponseType;
import com.holonplatform.reactor.http.ReactiveRestClient.ReactiveRequestDefinition;

import reactor.core.publisher.Mono;

/**
 * Default {@link ReactiveRequestDefinition} implementation.
 *
 * @since 5.2.0
 */
public class DefaultReactiveRequestDefinition extends AbstractRequestDefinition<ReactiveRequestDefinition>
		implements ReactiveRequestDefinition {

	/**
	 * Invoker
	 */
	protected final ReactiveInvoker invoker;

	/**
	 * Constructor.
	 * @param invoker Invoker to use to invoke for response
	 */
	public DefaultReactiveRequestDefinition(ReactiveInvoker invoker) {
		super();
		ObjectUtils.argumentNotNull(invoker, "Invoker must be not null");
		this.invoker = invoker;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.internal.rest.AbstractRequestDefinition#getActualDefinition()
	 */
	@Override
	protected ReactiveRequestDefinition getActualDefinition() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.reactor.http.ReactiveRestClient.ReactiveInvocation#invoke(com.holonplatform.http.HttpMethod,
	 * com.holonplatform.http.rest.RequestEntity, com.holonplatform.http.rest.ResponseType)
	 */
	@Override
	public <T, R> Mono<ResponseEntity<T>> invoke(HttpMethod method, RequestEntity<R> requestEntity,
			ResponseType<T> responseType) {
		return invoker.invoke(this, method, requestEntity, responseType, false);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.reactor.http.ReactiveRestClient.ReactiveInvocation#invokeForSuccess(com.holonplatform.http.
	 * HttpMethod, com.holonplatform.http.rest.RequestEntity, com.holonplatform.http.rest.ResponseType)
	 */
	@Override
	public <T, R> Mono<ResponseEntity<T>> invokeForSuccess(HttpMethod method, RequestEntity<R> requestEntity,
			ResponseType<T> responseType) {
		return invoker.invoke(this, method, requestEntity, responseType, true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.ReactiveRestClient.ReactiveInvocation#invokeForEntity(com.holonplatform.http.
	 * HttpMethod, com.holonplatform.http.rest.RequestEntity, com.holonplatform.http.rest.ResponseType)
	 */
	@Override
	public <T, R> Mono<T> invokeForEntity(HttpMethod method, RequestEntity<R> requestEntity,
			ResponseType<T> responseType) {
		return invoker.invoke(this, method, requestEntity, responseType, true).onErrorMap(error -> {
			if (error instanceof UnsuccessfulResponseException) {
				return error;
			}
			return new HttpClientInvocationException(error);
		}).flatMap(response -> Mono.using(() -> response,
				r -> r.getPayload().map(p -> Mono.just(p)).orElse(Mono.empty()), r -> r.close()));
	}

}
