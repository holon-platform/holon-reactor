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
package com.holonplatform.reactor.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.http.HttpMethod;
import com.holonplatform.http.exceptions.HttpClientInvocationException;
import com.holonplatform.http.exceptions.RestClientCreationException;
import com.holonplatform.http.exceptions.UnsuccessfulResponseException;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.http.rest.ResponseType;
import com.holonplatform.http.rest.RestClientOperations;
import com.holonplatform.http.rest.RestClientOperations.InvocationOperations;
import com.holonplatform.http.rest.RestClientOperations.RequestConfiguration;
import com.holonplatform.reactor.http.ReactiveRestClient.ReactiveRequestDefinition;
import com.holonplatform.reactor.http.internal.ReactiveRestClientFactoryRegistry;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive HTTP REST client, using project reactor.
 *
 * @since 5.2.0
 */
public interface ReactiveRestClient extends RestClientOperations<ReactiveRestClient, ReactiveRequestDefinition> {

	/**
	 * Invocation operations
	 */
	public interface ReactiveInvocation
			extends InvocationOperations<Mono<?>, Mono<?>, Mono<InputStream>, Flux<?>, Mono<URI>> {

		/**
		 * Invoke the request and asynchronously receive a response back.
		 * <p>
		 * The response payload is processed and possibly converted by concrete client implementation.
		 * </p>
		 * @param <T> Response type
		 * @param <R> Request entity type
		 * @param method Request method
		 * @param requestEntity Request entity
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as the result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		<T, R> Mono<ReactiveResponseEntity<T>> invoke(HttpMethod method, RequestEntity<R> requestEntity,
				ResponseType<T> responseType);

		/**
		 * Invoke the request and asynchronously receive a response back only if the response has a <em>success</em>
		 * (<code>2xx</code>) status code.
		 * @param <T> Response type
		 * @param <R> Request entity type
		 * @param method Request method
		 * @param requestEntity Request entity
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		<T, R> Mono<ReactiveResponseEntity<T>> invokeForSuccess(HttpMethod method, RequestEntity<R> requestEntity,
				ResponseType<T> responseType);

		/**
		 * Invoke the request and asynchronously receive back the response content entity.
		 * @param <T> Response type
		 * @param <R> Request entity type
		 * @param method Request method
		 * @param requestEntity Request entity
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty Optional if not present (empty response entity)
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		<T, R> Mono<T> invokeForEntity(HttpMethod method, RequestEntity<R> requestEntity, ResponseType<T> responseType);

		// GET

		/**
		 * Invoke the request using <code>GET</code> method and asynchronously receive a response back.
		 * @param <T> Response type
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default <T> Mono<ReactiveResponseEntity<T>> get(Class<T> responseType) {
			return invoke(HttpMethod.GET, null, ResponseType.of(responseType));
		}

		/**
		 * Invoke the request using <code>GET</code> method and asynchronously receive a response back.
		 * @param <T> Response type
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default <T> Mono<ReactiveResponseEntity<T>> get(ResponseType<T> responseType) {
			return invoke(HttpMethod.GET, null, responseType);
		}

		/**
		 * Invoke the request using <code>GET</code> method and asynchronously receive the response entity payload back.
		 * @param <T> Response entity type
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty Optional if not present (empty response entity)
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<T> getForEntity(Class<T> responseType) {
			return invokeForEntity(HttpMethod.GET, null, ResponseType.of(responseType));
		}

		/**
		 * Invoke the request using <code>GET</code> method and asynchronously receive the response entity payload of
		 * given generic type back.
		 * @param <T> Response entity type
		 * @param responseType Response payload generic type representation
		 * @return A {@link Mono} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty Optional if not present (empty response entity)
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<T> getForEntity(ResponseType<T> responseType) {
			return invokeForEntity(HttpMethod.GET, null, responseType);
		}

		/**
		 * Invoke the request using <code>GET</code> method and asynchronously receive the response entity
		 * {@link InputStream} back.
		 * @return A {@link Mono} to handle the response payload {@link InputStream}, or an empty stream for empty
		 *         responses
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default Mono<InputStream> getForStream() {
			return invokeForEntity(HttpMethod.GET, null, ResponseType.of(InputStream.class))
					.defaultIfEmpty(new ByteArrayInputStream(new byte[0]));
		}

		/**
		 * Convenience method to invoke the request using <code>GET</code> method and asynchronously receive a response
		 * entity payload of {@link List} type back.
		 * @param <T> Response entity type
		 * @param responseType Expected {@link List} response type
		 * @return A {@link Flux} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty flux if not present
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Flux<T> getAsList(Class<T> responseType) {
			final ResponseType<List<T>> rt = ResponseType.of(responseType, List.class);
			return getForEntity(rt).defaultIfEmpty(Collections.emptyList()).flatMapIterable(l -> l);
		}

		// POST

		/**
		 * Invoke the request using <code>POST</code> method with given <code>entity</code> request payload and
		 * asynchronously receive a response back.
		 * <p>
		 * The response type is conventionally of {@link Void} type, because no response paylod is expected from this
		 * invocation. Refer to the other <code>post</code> methods to obtain a response payload.
		 * </p>
		 * @param entity Request payload
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default Mono<ReactiveResponseEntity<Void>> post(RequestEntity<?> entity) {
			return invoke(HttpMethod.POST, entity, ResponseType.of(Void.class));
		}

		/**
		 * Invoke the request using <code>POST</code> method with given <code>entity</code> request payload and
		 * asynchronously receive a response back.
		 * @param <T> Response type
		 * @param entity Request payload
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default <T> Mono<ReactiveResponseEntity<T>> post(RequestEntity<?> entity, Class<T> responseType) {
			return invoke(HttpMethod.POST, entity, ResponseType.of(responseType));
		}

		/**
		 * Invoke the request using <code>POST</code> method with given <code>entity</code> request payload and
		 * asynchronously receive a response back.
		 * @param <T> Response type
		 * @param entity Request payload
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default <T> Mono<ReactiveResponseEntity<T>> post(RequestEntity<?> entity, ResponseType<T> responseType) {
			return invoke(HttpMethod.POST, entity, responseType);
		}

		/**
		 * Invoke the request using <code>POST</code> method with given <code>entity</code> request payload and
		 * asynchronously receive the response payload back.
		 * @param <T> Response type
		 * @param entity Request payload
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty Optional if not present (empty response entity)
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<T> postForEntity(RequestEntity<?> entity, Class<T> responseType) {
			return invokeForEntity(HttpMethod.POST, entity, ResponseType.of(responseType));
		}

		/**
		 * Invoke the request using <code>POST</code> method with given <code>entity</code> request payload and
		 * asynchronously receive the response payload back.
		 * @param <T> Response type
		 * @param entity Request payload
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty Optional if not present (empty response entity)
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<T> postForEntity(RequestEntity<?> entity, ResponseType<T> responseType) {
			return invokeForEntity(HttpMethod.POST, entity, responseType);
		}

		/**
		 * Invoke the request using <code>POST</code> method with given <code>entity</code> request payload and
		 * asynchronously receive the value of the <code>LOCATION</code> header back, if present.
		 * @param entity Request payload
		 * @return A {@link Mono} to handle the value of the <code>LOCATION</code> header back, if present
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default Mono<URI> postForLocation(RequestEntity<?> entity) {
			return invokeForSuccess(HttpMethod.POST, entity, ResponseType.of(Void.class))
					.map(r -> r.getLocation().orElse(null));
		}

		// PUT

		/**
		 * Invoke the request using <code>PUT</code> method with given <code>entity</code> request payload and
		 * asynchronously receive a response back.
		 * <p>
		 * The response type is conventionally of {@link Void} type, because no response paylod is expected from this
		 * invocation. Refer to the other <code>post</code> methods to obtain a response payload.
		 * </p>
		 * @param entity Request payload
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default Mono<ReactiveResponseEntity<Void>> put(RequestEntity<?> entity) {
			return invoke(HttpMethod.PUT, entity, ResponseType.of(Void.class));
		}

		/**
		 * Invoke the request using <code>PUT</code> method with given <code>entity</code> request payload and
		 * asynchronously receive a response back.
		 * @param <T> Response type
		 * @param entity Request payload
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default <T> Mono<ReactiveResponseEntity<T>> put(RequestEntity<?> entity, Class<T> responseType) {
			return invoke(HttpMethod.PUT, entity, ResponseType.of(responseType));
		}

		/**
		 * Invoke the request using <code>PUT</code> method with given <code>entity</code> request payload and
		 * asynchronously receive a response back.
		 * @param <T> Response type
		 * @param entity Request payload
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default <T> Mono<ReactiveResponseEntity<T>> put(RequestEntity<?> entity, ResponseType<T> responseType) {
			return invoke(HttpMethod.PUT, entity, responseType);
		}

		/**
		 * Invoke the request using <code>PUT</code> method with given <code>entity</code> request payload and
		 * asynchronously receive the response entity payload back.
		 * @param <T> Response entity type
		 * @param entity Request payload
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty Optional if not present (empty response entity)
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<T> putForEntity(RequestEntity<?> entity, Class<T> responseType) {
			return invokeForEntity(HttpMethod.PUT, entity, ResponseType.of(responseType));
		}

		/**
		 * Invoke the request using <code>PUT</code> method with given <code>entity</code> request entity payload and
		 * asynchronously receive the response payload back.
		 * @param <T> Response entity type
		 * @param entity Request payload
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty Optional if not present (empty response entity)
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<T> putForEntity(RequestEntity<?> entity, ResponseType<T> responseType) {
			return invokeForEntity(HttpMethod.PUT, entity, responseType);
		}

		// PATCH

		/**
		 * Invoke the request using <code>PATCH</code> method with given <code>entity</code> request payload and
		 * asynchronously receive a response back.
		 * <p>
		 * The response type is conventionally of {@link Void} type, because no response paylod is expected from this
		 * invocation. Refer to the other <code>post</code> methods to obtain a response payload.
		 * </p>
		 * @param entity Request payload
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default Mono<ReactiveResponseEntity<Void>> patch(RequestEntity<?> entity) {
			return invoke(HttpMethod.PATCH, entity, ResponseType.of(Void.class));
		}

		/**
		 * Invoke the request using <code>PATCH</code> method with given <code>entity</code> request payload and
		 * asynchronously receive a response back.
		 * @param <T> Response type
		 * @param entity Request payload
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<ReactiveResponseEntity<T>> patch(RequestEntity<?> entity, Class<T> responseType) {
			return invoke(HttpMethod.PATCH, entity, ResponseType.of(responseType));
		}

		/**
		 * Invoke the request using <code>PATCH</code> method with given <code>entity</code> request payload and
		 * asynchronously receive a response back.
		 * @param <T> Response type
		 * @param entity Request payload
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<ReactiveResponseEntity<T>> patch(RequestEntity<?> entity, ResponseType<T> responseType) {
			return invoke(HttpMethod.PATCH, entity, responseType);
		}

		/**
		 * Invoke the request using <code>PATCH</code> method with given <code>entity</code> request payload and
		 * asynchronously receive the response entity payload back.
		 * @param <T> Response entity type
		 * @param entity Request payload
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty Optional if not present (empty response entity)
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<T> patchForEntity(RequestEntity<?> entity, Class<T> responseType) {
			return invokeForEntity(HttpMethod.PATCH, entity, ResponseType.of(responseType));
		}

		/**
		 * Invoke the request using <code>PATCH</code> method with given <code>entity</code> request payload and
		 * asynchronously receive the response entity payload back.
		 * @param <T> Response entity type
		 * @param entity Request payload
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty Optional if not present (empty response entity)
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<T> patchForEntity(RequestEntity<?> entity, ResponseType<T> responseType) {
			return invokeForEntity(HttpMethod.PATCH, entity, responseType);
		}

		// DELETE

		/**
		 * Invoke the request using <code>DELETE</code> method and asynchronously receive a response back.
		 * <p>
		 * The response type is conventionally of {@link Void} type, because no response paylod is expected from this
		 * invocation. Refer to the other <code>post</code> methods to obtain a response payload.
		 * </p>
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel)
		 */
		@Override
		default Mono<ReactiveResponseEntity<Void>> delete() {
			return invoke(HttpMethod.DELETE, null, ResponseType.of(Void.class));
		}

		/**
		 * Invoke the request asynchronously using the <code>DELETE</code> method. If the returned response is not a
		 * <em>success</em> response (i.e. with a <code>2xx</code> status code), a {@link UnsuccessfulResponseException}
		 * is thrown.
		 * @return A {@link Mono} to handle the response
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		default Mono<Void> deleteOrFail() {
			return invokeForSuccess(HttpMethod.DELETE, null, ResponseType.of(Void.class)).flatMap(r -> Mono.empty());
		}

		/**
		 * Invoke the request asynchronously using the <code>DELETE</code> method and receive a response back.
		 * @param <T> Response type
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default <T> Mono<ReactiveResponseEntity<T>> delete(Class<T> responseType) {
			return invoke(HttpMethod.DELETE, null, ResponseType.of(responseType));
		}

		/**
		 * Invoke the request using <code>DELETE</code> method and asynchronously receive a response back.
		 * @param <T> Response type
		 * @param responseType Response payload generic type representation
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default <T> Mono<ReactiveResponseEntity<T>> delete(ResponseType<T> responseType) {
			return invoke(HttpMethod.DELETE, null, responseType);
		}

		/**
		 * Invoke the request using <code>DELETE</code> method and asynchronously receive the response entity payload
		 * back.
		 * @param <T> Response entity type
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty Optional if not present (empty response entity)
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<T> deleteForEntity(Class<T> responseType) {
			return invokeForEntity(HttpMethod.DELETE, null, ResponseType.of(responseType));
		}

		/**
		 * Invoke the request using <code>DELETE</code> method and asynchronously receive the response entity payload of
		 * given generic type back.
		 * @param <T> Response entity type
		 * @param responseType Response payload generic type representation
		 * @return A {@link Mono} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty Optional if not present (empty response entity)
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<T> deleteForEntity(ResponseType<T> responseType) {
			return invokeForEntity(HttpMethod.DELETE, null, responseType);
		}

		// OPTIONS

		/**
		 * Invoke the request using <code>OPTIONS</code> method and asynchronously receive a response back.
		 * <p>
		 * The response type is conventionally of {@link Void} type, because no response paylod is expected from this
		 * invocation. Refer to the other <code>post</code> methods to obtain a response payload.
		 * </p>
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel)
		 */
		@Override
		default Mono<ReactiveResponseEntity<Void>> options() {
			return invoke(HttpMethod.OPTIONS, null, ResponseType.of(Void.class));
		}

		/**
		 * Invoke the request using <code>OPTIONS</code> method and asynchronously receive a response back.
		 * @param <T> Response type
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default <T> Mono<ReactiveResponseEntity<T>> options(Class<T> responseType) {
			return invoke(HttpMethod.OPTIONS, null, ResponseType.of(responseType));
		}

		/**
		 * Invoke the request using <code>OPTIONS</code> method and asynchronously receive a response back.
		 * @param <T> Response type
		 * @param responseType Response payload generic type representation
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default <T> Mono<ReactiveResponseEntity<T>> options(ResponseType<T> responseType) {
			return invoke(HttpMethod.OPTIONS, null, responseType);
		}

		/**
		 * Invoke the request using <code>OPTIONS</code> method and asynchronously receive the response entity payload
		 * back.
		 * @param <T> Response entity type
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty Optional if not present (empty response entity)
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<T> optionsForEntity(Class<T> responseType) {
			return invokeForEntity(HttpMethod.OPTIONS, null, ResponseType.of(responseType));
		}

		/**
		 * Invoke the request using <code>OPTIONS</code> method and asynchronously receive the response entity payload
		 * of given generic type back.
		 * @param <T> Response entity type
		 * @param responseType Response payload generic type representation
		 * @return A {@link Mono} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty Optional if not present (empty response entity)
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<T> optionsForEntity(ResponseType<T> responseType) {
			return invokeForEntity(HttpMethod.OPTIONS, null, responseType);
		}

		// TRACE

		/**
		 * Invoke the request using <code>TRACE</code> method and asynchronously receive a response back.
		 * <p>
		 * The response type is conventionally of {@link Void} type, because no response paylod is expected from this
		 * invocation. Refer to the other <code>post</code> methods to obtain a response payload.
		 * </p>
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default Mono<ReactiveResponseEntity<Void>> trace() {
			return invoke(HttpMethod.TRACE, null, ResponseType.of(Void.class));
		}

		/**
		 * Invoke the request using <code>TRACE</code> method and asynchronously receive a response back.
		 * @param <T> Response type
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default <T> Mono<ReactiveResponseEntity<T>> trace(Class<T> responseType) {
			return invoke(HttpMethod.TRACE, null, ResponseType.of(responseType));
		}

		/**
		 * Invoke the request using <code>TRACE</code> method and asynchronously receive a response back.
		 * @param <T> Response type
		 * @param responseType Response payload generic type representation
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default <T> Mono<ReactiveResponseEntity<T>> trace(ResponseType<T> responseType) {
			return invoke(HttpMethod.TRACE, null, responseType);
		}

		/**
		 * Invoke the request using <code>TRACE</code> method and asynchronously receive the response entity payload
		 * back.
		 * @param <T> Response entity type
		 * @param responseType Expected response payload type
		 * @return A {@link Mono} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty Optional if not present (empty response entity)
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<T> traceForEntity(Class<T> responseType) {
			return invokeForEntity(HttpMethod.TRACE, null, ResponseType.of(responseType));
		}

		/**
		 * Invoke the request using <code>TRACE</code> method and asynchronously receive the response entity payload of
		 * given generic type back.
		 * @param <T> Response entity type
		 * @param responseType Response payload generic type representation
		 * @return A {@link Mono} to handle the response payload of expected type as the result of the request
		 *         invocation, or an empty Optional if not present (empty response entity)
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 * @throws UnsuccessfulResponseException In case the status code of the response returned by the server is not a
		 *         successful type status code, i.e. it is not a <code>2xx</code> status code
		 */
		@Override
		default <T> Mono<T> traceForEntity(ResponseType<T> responseType) {
			return invokeForEntity(HttpMethod.TRACE, null, responseType);
		}

		// HEAD

		/**
		 * Invoke the request using <code>HEAD</code> method and asynchronously receive a response back.
		 * @return A {@link Mono} to handle the {@link ReactiveResponseEntity} object as a result of the request invocation
		 * @throws HttpClientInvocationException Internal invocation failure (for example, an I/O error on communication
		 *         channel or expected and actual payload type mismatch)
		 */
		@Override
		default Mono<ReactiveResponseEntity<Void>> head() {
			return invoke(HttpMethod.HEAD, null, ResponseType.of(Void.class));
		}

	}

	/**
	 * Request message definition and response invocation.
	 */
	public interface ReactiveRequestDefinition
			extends RequestConfiguration<ReactiveRequestDefinition>, ReactiveInvocation {

	}

	// Builders

	/**
	 * Create a new {@link ReactiveRestClient} instance using default {@link ClassLoader} and default implementation,
	 * setting given <code>baseUri</code> as default {@link ReactiveRestClient} target, which will be used as base URI
	 * for every request configured using {@link #request()}, if not overridden using
	 * {@link ReactiveRequestDefinition#target(URI)}.
	 * @param baseUri The base target URI of the returned {@link ReactiveRestClient}
	 * @return A new {@link ReactiveRestClient} instance
	 */
	static ReactiveRestClient forTarget(String baseUri) {
		return create().defaultTarget(URI.create(baseUri));
	}

	/**
	 * Create a new {@link ReactiveRestClient} instance using default {@link ClassLoader} and default implementation,
	 * setting given <code>baseUri</code> as default {@link ReactiveRestClient} target, which will be used as base URI
	 * for every request configured using {@link #request()}, if not overridden using
	 * {@link ReactiveRequestDefinition#target(URI)}.
	 * @param baseUri The base target URI of the returned {@link ReactiveRestClient}
	 * @return A new {@link ReactiveRestClient} instance
	 */
	static ReactiveRestClient forTarget(URI baseUri) {
		return create().defaultTarget(baseUri);
	}

	/**
	 * Create a new {@link ReactiveRestClient} instance using default {@link ClassLoader} and default implementation, if
	 * available. If more than one {@link ReactiveRestClient} implementation is found using given ClassLoader, the one
	 * returned by the {@link ReactiveRestClientFactory} with the higher priority is returned.
	 * @return A new {@link ReactiveRestClient} instance
	 * @throws RestClientCreationException If a {@link ReactiveRestClient} implementation is not available or a instance
	 *         creation error occurred
	 */
	static ReactiveRestClient create() {
		return create(null, ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Create a new {@link ReactiveRestClient} instance using given <code>classLoder</code> and default implementation,
	 * if available. If more than one {@link ReactiveRestClient} implementation is found using given ClassLoader, the
	 * one returned by the {@link ReactiveRestClientFactory} with the higher priority is returned.
	 * @param classLoader The {@link ClassLoader} to use
	 * @return A new {@link ReactiveRestClient} instance
	 * @throws RestClientCreationException If a {@link ReactiveRestClient} implementation is not available or a instance
	 *         creation error occurred
	 */
	static ReactiveRestClient create(ClassLoader classLoader) {
		return create(null, classLoader);
	}

	/**
	 * Create a new {@link ReactiveRestClient} instance using default {@link ClassLoader} and the implementation whith
	 * given fully qualified class name.
	 * @param fullyQualifiedClassName The {@link ReactiveRestClient} implementation fully qualified class name to obtain
	 * @return A new {@link ReactiveRestClient} instance
	 * @throws RestClientCreationException If the implementation which corresponds to given fully qualified class name
	 *         is not available or a instance creation error occurred
	 */
	static ReactiveRestClient create(String fullyQualifiedClassName) {
		return create(fullyQualifiedClassName, ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Create a new {@link ReactiveRestClient} instance using given <code>classLoder</code> and the implementation whith
	 * given fully qualified class name.
	 * @param fullyQualifiedClassName The {@link ReactiveRestClient} implementation fully qualified class name to obtain
	 * @param classLoader The {@link ClassLoader} to use
	 * @return A new {@link ReactiveRestClient} instance
	 * @throws RestClientCreationException If the implementation which corresponds to given fully qualified class name
	 *         is not available or a instance creation error occurred
	 */
	static ReactiveRestClient create(String fullyQualifiedClassName, ClassLoader classLoader) {
		return ReactiveRestClientFactoryRegistry.INSTANCE.createRestClient(fullyQualifiedClassName, classLoader);
	}

}
