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

import com.holonplatform.core.query.QueryConfiguration;
import com.holonplatform.core.query.QueryOperation;
import com.holonplatform.core.query.QueryProjection;

import reactor.core.publisher.Flux;

/**
 * Adapter to perform a reactive <em>query</em> execution using a {@link QueryOperation} instance, which provides both
 * the {@link QueryConfiguration} and the {@link QueryProjection}.
 * <p>
 * The query results are provided using a {@link Flux}.
 * </p>
 * 
 * @param <C> Query configuration type
 * 
 * @since 5.2.0
 * 
 * @see ReactiveQuery
 */
public interface ReactiveQueryAdapter<C extends QueryConfiguration> {

	/**
	 * Execute a query using the provided {@link QueryOperation} and return the results as a {@link Flux} of query
	 * results.
	 * @param <R> Query results type
	 * @param queryOperation Query operation (not null)
	 * @return Query results {@link Flux} stream
	 */
	<R> Flux<R> stream(QueryOperation<C, R> queryOperation);

}
