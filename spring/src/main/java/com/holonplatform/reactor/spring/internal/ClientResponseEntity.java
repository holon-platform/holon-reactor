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

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientResponse;

import com.holonplatform.http.rest.ResponseEntity;
import com.holonplatform.http.rest.ResponseType;
import com.holonplatform.reactor.http.ReactiveResponseEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring {@link ResponseEntity} implementation using a {@link ClientResponse}.
 * 
 * @param <T> Response entity type
 *
 * @since 5.2.0
 */
public class ClientResponseEntity<T> implements ReactiveResponseEntity<T> {

	private final ClientResponse response;
	private final ResponseType<T> type;

	/**
	 * @param reponse
	 * @param type
	 */
	public ClientResponseEntity(ClientResponse response, ResponseType<T> type) {
		super();
		this.response = response;
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.HttpResponse#getStatusCode()
	 */
	@Override
	public int getStatusCode() {
		return response.statusCode().value();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.messaging.MessageHeaders#getHeaders()
	 */
	@Override
	public Map<String, List<String>> getHeaders() {
		return response.headers().asHttpHeaders();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.messaging.Message#getPayloadType()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends T> getPayloadType() throws UnsupportedOperationException {
		return (Class<? extends T>) (Class<?>) type.getType();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.ReactiveResponseEntity#asMono()
	 */
	@Override
	public Mono<T> asMono() {
		return response.bodyToMono(getPayloadType());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.ReactiveResponseEntity#asMono(java.lang.Class)
	 */
	@Override
	public <E> Mono<E> asMono(Class<E> entityType) {
		return response.bodyToMono(entityType);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.ReactiveResponseEntity#asMono(com.holonplatform.http.rest.ResponseType)
	 */
	@Override
	public <E> Mono<E> asMono(ResponseType<E> entityType) {
		return response.bodyToMono(ParameterizedTypeReference.forType(entityType.getType()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.ReactiveResponseEntity#asFlux(java.lang.Class)
	 */
	@Override
	public <E> Flux<E> asFlux(Class<E> entityType) {
		return response.bodyToFlux(entityType);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.ReactiveResponseEntity#asInputStream()
	 */
	@Override
	public Mono<InputStream> asInputStream() {
		final InputStream initial = new InputStream() {
			@Override
			public int read() {
				return -1;
			}
		};
		return response.body(BodyExtractors.toDataBuffers()).reduce(initial,
				(s, d) -> new SequenceInputStream(s, d.asInputStream()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.messaging.Message#getPayload()
	 */
	@Override
	public Optional<T> getPayload() throws UnsupportedOperationException {
		return asMono().blockOptional();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.rest.ResponseEntity#as(java.lang.Class)
	 */
	@Override
	public <E> Optional<E> as(Class<E> entityType) {
		return asMono(entityType).blockOptional();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.rest.ResponseEntity#as(com.holonplatform.http.rest.ResponseType)
	 */
	@Override
	public <E> Optional<E> as(ResponseType<E> entityType) {
		return asMono(entityType).blockOptional();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.rest.ResponseEntity#close()
	 */
	@Override
	public void close() {
		// noop
	}

}
