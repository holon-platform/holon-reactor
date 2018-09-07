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
package com.holonplatform.reactor.datastore.internal.operation;

import com.holonplatform.core.internal.query.QueryDefinition;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryOperation;
import com.holonplatform.core.query.QueryProjection;
import com.holonplatform.reactor.datastore.operation.ReactiveQuery;
import com.holonplatform.reactor.datastore.operation.ReactiveQueryAdapter;

import reactor.core.publisher.Flux;

/**
 * {@link ReactiveQuery} implementation that uses a {@link ReactiveQueryAdapter} to connect concrete query execution
 * environment to the generic query results methods.
 * 
 * @param <D> Query definition type
 * 
 * @since 5.2.0
 */
public class ReactiveQueryAdapterQuery<D extends QueryDefinition> extends AbstractReactiveQuery<D> {

	private static final long serialVersionUID = -6703574673891951627L;

	/*
	 * Adapter (immutable)
	 */
	private final ReactiveQueryAdapter<? super D> queryAdapter;

	/**
	 * Constructor.
	 * @param queryAdapter Query adapter (not null)
	 * @param queryDefinition Query definition (not null)
	 */
	public ReactiveQueryAdapterQuery(ReactiveQueryAdapter<? super D> queryAdapter, D queryDefinition) {
		super(queryDefinition);
		ObjectUtils.argumentNotNull(queryAdapter, "The query adapter must be not null");
		this.queryAdapter = queryAdapter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.AbstractQueryBuilder#getActualBuilder()
	 */
	@Override
	protected ReactiveQuery getActualBuilder() {
		return this;
	}

	/**
	 * Get the query adapter.
	 * @return Query adapter
	 */
	protected ReactiveQueryAdapter<? super D> getQueryAdapter() {
		return queryAdapter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.datastore.operation.ReactiveQueryResults#stream(com.holonplatform.core.query.
	 * QueryProjection)
	 */
	@Override
	public <R> Flux<R> stream(QueryProjection<R> projection) {
		return getQueryAdapter().stream(QueryOperation.create(getQueryDefinition(), projection));
	}

}
