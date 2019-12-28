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

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.http.HttpMethod;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.http.MediaType;
import com.holonplatform.http.exceptions.UnsuccessfulResponseException;
import com.holonplatform.http.internal.HttpUtils;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.http.rest.ResponseType;
import com.holonplatform.reactor.http.ReactiveResponseEntity;
import com.holonplatform.reactor.http.internal.AbstractReactiveRestClient;
import com.holonplatform.reactor.http.internal.DefaultReactiveRequestDefinition;
import com.holonplatform.reactor.spring.SpringReactiveRestClient;

import reactor.core.publisher.Mono;

/**
 * Default {@link SpringReactiveRestClient} implementation.
 *
 * @since 5.2.0
 */
public class WebClientReactiveRestClient extends AbstractReactiveRestClient implements SpringReactiveRestClient {

	/**
	 * Web client
	 */
	private final WebClient client;

	/**
	 * Constructor
	 * @param client Spring {@link WebClient}
	 */
	public WebClientReactiveRestClient(WebClient client) {
		super();
		ObjectUtils.argumentNotNull(client, "Client must be not null");
		this.client = client;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.spring.SpringReactiveRestClient#getClient()
	 */
	@Override
	public WebClient getClient() {
		return client;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.internal.AbstractReactiveRestClient#buildDefinition()
	 */
	@Override
	protected ReactiveRequestDefinition buildDefinition() {
		return new DefaultReactiveRequestDefinition(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.reactor.http.internal.ReactiveInvoker#invoke(com.holonplatform.reactor.http.ReactiveRestClient.
	 * ReactiveRequestDefinition, com.holonplatform.http.HttpMethod, com.holonplatform.http.rest.RequestEntity,
	 * com.holonplatform.http.rest.ResponseType, boolean)
	 */
	@Override
	public <T, R> Mono<ReactiveResponseEntity<T>> invoke(ReactiveRequestDefinition requestDefinition, HttpMethod method,
			RequestEntity<R> requestEntity, ResponseType<T> responseType, boolean onlySuccessfulStatusCode) {
		ObjectUtils.argumentNotNull(method, "HTTP method must be not null");

		// builder
		WebClient.Builder builder = getClient().mutate();

		// Base URI
		final String baseUrl = requestDefinition.getBaseRequestURI().map(uri -> uri.toString()).orElse(null);
		if (baseUrl != null) {
			builder = builder.baseUrl(baseUrl);
		}

		// WebClient
		final WebClient client = builder.build();

		// method
		org.springframework.http.HttpMethod requestMethod = org.springframework.http.HttpMethod
				.resolve(method.getMethodName());
		if (requestMethod == null) {
			throw new RestClientException("Unsupported HTTP method: " + method.getMethodName());
		}

		final RequestBodySpec spec = client.method(requestMethod).uri(ub -> {
			// path
			requestDefinition.getRequestPath().ifPresent(path -> ub.path(path));
			// query parameters
			requestDefinition.getQueryParameters().forEach((n, v) -> ub.queryParam(n, v));
			// uri variables
			return ub.build(requestDefinition.getTemplateParameters());
		}).headers(headers -> {
			// headers
			requestDefinition.getHeaders().forEach((n, v) -> headers.add(n, v));
		});

		// body
		getRequestPayload(requestEntity).ifPresent(payload -> {
			spec.bodyValue(payload);
		});

		// body

		// get response, checking propertySet
		final Mono<ClientResponse> response = requestDefinition.getPropertySet()
				.map(ps -> ps.execute(() -> spec.exchange())).orElseGet(() -> spec.exchange());

		return response.flatMap(r -> {
			final org.springframework.http.HttpStatus status = r.statusCode();
			if (onlySuccessfulStatusCode && !HttpStatus.isSuccessStatusCode(status.value())) {
				return Mono.<ReactiveResponseEntity<T>>error(
						new UnsuccessfulResponseException(new ClientResponseEntity<>(r, responseType)));
			}
			return Mono.just(new ClientResponseEntity<>(r, responseType));
		});
	}

	private static final String APPLICATION_FORM_URLENCODED_MEDIA_TYPE = MediaType.APPLICATION_FORM_URLENCODED
			.toString();

	/**
	 * Get the request entity payload
	 * @param requestEntity RequestEntity
	 * @return Request entity payload, may be null
	 */
	protected Optional<Object> getRequestPayload(RequestEntity<?> requestEntity) {
		if (requestEntity != null) {
			boolean form = requestEntity.getMediaType().map(m -> APPLICATION_FORM_URLENCODED_MEDIA_TYPE.equals(m))
					.orElse(Boolean.FALSE);
			return requestEntity.getPayload()
					.map(p -> form ? new LinkedMultiValueMap<>(HttpUtils.getAsMultiMap(p)) : p);
		}
		return Optional.empty();
	}

}
