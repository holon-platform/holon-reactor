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
package com.holonplatform.reactor.http.internal;

import com.holonplatform.http.HttpMethod;
import com.holonplatform.http.exceptions.UnsuccessfulResponseException;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.http.rest.ResponseType;
import com.holonplatform.reactor.http.ReactiveResponseEntity;
import com.holonplatform.reactor.http.ReactiveRestClient.ReactiveRequestDefinition;

import reactor.core.publisher.Mono;

/**
 * Invoker to asynchronously perform a client request in order to consume a response using a
 * {@link ReactiveRequestDefinition}.
 * 
 * @since 5.2.0
 */
public interface ReactiveInvoker {

	/**
	 * Invoke a request asynchronously using current given <code>request</code> definition.
	 * @param <T> Response type
	 * @param <R> Request entity type
	 * @param requestDefinition Request definition
	 * @param method Request method
	 * @param requestEntity Request message payload
	 * @param responseType Expected response payload type
	 * @param onlySuccessfulStatusCode <code>true</code> to return only <code>2xx</code> status code response and throw
	 *        an {@link UnsuccessfulResponseException} otherwise, <code>false</code> to return any status code responses
	 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as the result of the request
	 *         invocation
	 */
	<T, R> Mono<ReactiveResponseEntity<T>> invoke(ReactiveRequestDefinition requestDefinition, HttpMethod method,
			RequestEntity<R> requestEntity, ResponseType<T> responseType, boolean onlySuccessfulStatusCode);

}
