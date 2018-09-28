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

import com.holonplatform.http.rest.ResponseEntity;
import com.holonplatform.http.rest.ResponseType;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A reactive {@link ResponseEntity}.
 * 
 * @param <T> Response entity type
 * 
 * @since 5.2.0
 */
public interface ReactiveResponseEntity<T> extends ResponseEntity<T> {

	/**
	 * Read the message entity as a {@link Mono}.
	 * @return A {@link Mono} containing containing the message entity
	 */
	Mono<T> asMono();

	/**
	 * Read the message entity as a {@link Mono} of the specified type.
	 * @param <E> Entity instance type
	 * @param entityType Entity type (not null)
	 * @return A {@link Mono} containing containing the message entity
	 */
	<E> Mono<E> asMono(Class<E> entityType);

	/**
	 * Read the message entity as a {@link Mono} of the specified type., using a {@link ResponseType} representation to
	 * allow generic types support.
	 * @param <E> Entity instance type
	 * @param entityType Entity response type (not null)
	 * @return A {@link Mono} containing containing the message entity
	 */
	<E> Mono<E> asMono(ResponseType<E> entityType);

	/**
	 * Read the message entity as a {@link Flux} of the specified type.
	 * @param <E> Entity instance type
	 * @param entityType Entity type (not null)
	 * @return A {@link Flux} containing containing the message entity
	 */
	<E> Flux<E> asFlux(Class<E> entityType);

	/**
	 * Read the message entity as a {@link Flux} of the specified type., using a {@link ResponseType} representation to
	 * allow generic types support.
	 * @param <E> Entity instance type
	 * @param entityType Entity response type (not null)
	 * @return A {@link Flux} containing containing the message entity
	 */
	<E> Flux<E> asFlux(ResponseType<E> entityType);

}
