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

import com.holonplatform.core.datastore.DatastoreCommodity;
import com.holonplatform.core.query.QueryBuilder;
import com.holonplatform.core.query.QueryProjection;
import com.holonplatform.reactor.datastore.ReactiveDatastore;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Represents a reactive <em>query</em>, used by the {@link ReactiveDatastore} API to configure and execute a query and
 * handle the query results through the {@link Flux} and {@link Mono} abstractions.
 * <p>
 * The query is configured through the {@link QueryBuilder} API and executed using the {@link ReactiveQueryResults} API,
 * which provides methods to execute the query and obtain the results, using a {@link QueryProjection} to specify the
 * expected result type.
 * </p>
 * 
 * @since 5.2.0
 * 
 * @see ReactiveDatastore
 */
public interface ReactiveQuery extends QueryBuilder<ReactiveQuery>, ReactiveQueryResults, DatastoreCommodity {

}
