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
package com.holonplatform.reactor.datastore.operation;

import java.util.List;

import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.CountAllProjection;
import com.holonplatform.core.query.PropertySetProjection;
import com.holonplatform.core.query.QueryProjection;
import com.holonplatform.core.query.QueryProjectionOperations;
import com.holonplatform.core.query.QueryResults.QueryNonUniqueResultException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive {@link QueryProjectionOperations} API, which uses {@link Flux} and {@link Mono} to provide and handle the
 * query results.
 * 
 * @since 5.2.0
 * 
 * @see ReactiveQuery
 */
@SuppressWarnings("rawtypes")
public interface ReactiveQueryResults extends QueryProjectionOperations<Flux, Mono, Mono, Mono<Long>> {

	/**
	 * Execute the query and get a {@link Flux} of query results using given <code>projection</code>.
	 * @param <R> Results type
	 * @param projection Query projection (not null)
	 * @return A {@link Flux} of query results
	 * @throws DataAccessException If a query execution error occurred
	 */
	@Override
	<R> Flux<R> stream(QueryProjection<R> projection);

	/**
	 * Convenience method to obtain the query results {@link #stream(QueryProjection)} as a {@link List}, provided by a
	 * {@link Mono}.
	 * @param <R> Results type
	 * @param projection Query projection (not null)
	 * @return A {@link Mono} of the query results list, empty if none
	 * @throws DataAccessException If a query execution error occurred
	 */
	@Override
	default <R> Mono<List<R>> list(QueryProjection<R> projection) {
		return stream(projection).collectList();
	}

	/**
	 * Execute the query and get an expected unique result using given <code>projection</code>.
	 * <p>
	 * If more than one result is returned by the query, a {@link QueryNonUniqueResultException} is thrown.
	 * </p>
	 * @param <R> Result type
	 * @param projection Query projection (not null)
	 * @return A {@link Mono} of the unique query result (empty if no result was returned)
	 * @throws QueryNonUniqueResultException Only one result expected but more than one was found
	 * @throws DataAccessException If a query execution error occurred
	 */
	@Override
	default <R> Mono<R> findOne(QueryProjection<R> projection) throws QueryNonUniqueResultException {
		return stream(projection).singleOrEmpty().onErrorMap(e -> {
			if (e instanceof IndexOutOfBoundsException) {
				return new QueryNonUniqueResultException(
						"Expected an unique result, but had more than one [" + e.getMessage() + "]");
			}
			return e;
		});
	}

	/**
	 * Count all the results of a query.
	 * @return A {@link Mono} with the total results count, <code>0</code> if none
	 * @throws DataAccessException If a query execution error occurred
	 */
	@Override
	default Mono<Long> countAll() {
		return findOne(CountAllProjection.create()).defaultIfEmpty(0L);
	}

	/**
	 * Convenience {@link #countAll()} renamed method.
	 * @return A {@link Mono} with the total results count, <code>0</code> if none
	 * @throws DataAccessException If a query execution error occurred
	 */
	default Mono<Long> count() {
		return countAll();
	}

	/**
	 * Execute the query and get a {@link Flux} of query results as {@link PropertyBox} instances, using given
	 * <code>properties</code> as projection.
	 * <p>
	 * The returned {@link PropertyBox} instances will contain the values of the properties specified in given
	 * <code>properties</code> set.
	 * </p>
	 * @param <P> Property type
	 * @param properties Property set to use as projection (not null)
	 * @return A {@link Flux} of the query results
	 * @throws DataAccessException If a query execution error occurred
	 */
	@Override
	default <P extends Property> Flux<PropertyBox> stream(Iterable<P> properties) {
		return stream(PropertySetProjection.of(properties));
	}

	/**
	 * Execute the query and get an expected unique result as a {@link PropertyBox} instance, using given
	 * <code>properties</code> as projection.
	 * <p>
	 * The returned {@link PropertyBox} instance will contain the values of the properties specified in given
	 * <code>properties</code> set.
	 * </p>
	 * @param <P> Property type
	 * @param properties Property set to use as projection (not null)
	 * @return A {@link Mono} of the unique query result (empty if no results)
	 * @throws QueryNonUniqueResultException Only one result expected but more than one was found
	 * @throws DataAccessException If a query execution error occurred
	 */
	@Override
	default <P extends Property> Mono<PropertyBox> findOne(Iterable<P> properties)
			throws QueryNonUniqueResultException {
		return findOne(PropertySetProjection.of(properties));
	}

	/**
	 * Execute the query and get a {@link List} of query results as {@link PropertyBox} instances using a {@link Mono}
	 * representation, with given <code>properties</code> as query projection.
	 * <p>
	 * The returned {@link PropertyBox} instances will contain the values of the properties specified in given
	 * <code>properties</code> set.
	 * </p>
	 * @param <P> Property type
	 * @param properties Property set to use as projection (not null)
	 * @return A {@link Mono} of the query results {@link List}
	 * @throws DataAccessException If a query execution error occurred
	 */
	@Override
	default <P extends Property> Mono<List<PropertyBox>> list(Iterable<P> properties) {
		return list(PropertySetProjection.of(properties));
	}

	/**
	 * Execute the query and get a {@link Flux} of query results as {@link PropertyBox} instances, using given
	 * <code>properties</code> as projection.
	 * <p>
	 * The returned {@link PropertyBox} instances will contain the values of the properties specified in given
	 * <code>properties</code> set.
	 * </p>
	 * @param properties Property set to use as projection (not null)
	 * @return A {@link Flux} of the query results
	 * @throws DataAccessException If a query execution error occurred
	 */
	@Override
	default Flux<PropertyBox> stream(Property... properties) {
		return stream(PropertySet.of(properties));
	}

	/**
	 * Execute the query and get an expected unique result as {@link PropertyBox} instance using the {@link Mono}
	 * representation, with given <code>properties</code> as query projection.
	 * <p>
	 * The returned {@link PropertyBox} instance will contain the values of the properties specified in given
	 * <code>properties</code> set.
	 * </p>
	 * @param properties Property set to use as projection (not null)
	 * @return A {@link Mono} of the unique query result ( empty if no results)
	 * @throws QueryNonUniqueResultException Only one result expected but more than one was found
	 * @throws DataAccessException If a query execution error occurred
	 */
	@Override
	default Mono<PropertyBox> findOne(Property... properties) throws QueryNonUniqueResultException {
		return findOne(PropertySet.of(properties));
	}

	/**
	 * Execute the query and get a {@link Mono} of the results {@link List} as {@link PropertyBox} instances, using
	 * given <code>properties</code> as projection.
	 * <p>
	 * The returned {@link PropertyBox} instances will contain the values of the properties specified in given
	 * <code>properties</code> set.
	 * </p>
	 * @param properties Property set to use as projection (not null)
	 * @return A {@link Mono} of the query results {@link List}
	 * @throws DataAccessException If a query execution error occurred
	 */
	@Override
	default Mono<List<PropertyBox>> list(Property... properties) {
		return list(PropertySet.of(properties));
	}

}
