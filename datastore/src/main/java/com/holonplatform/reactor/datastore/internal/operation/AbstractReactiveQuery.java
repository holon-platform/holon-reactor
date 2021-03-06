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

import com.holonplatform.core.internal.query.AbstractQueryBuilder;
import com.holonplatform.core.internal.query.QueryDefinition;
import com.holonplatform.reactor.datastore.operation.ReactiveQuery;

/**
 * Base {@link ReactiveQuery} implementation providing support for {@link QueryDefinition} management.
 * 
 * @param <D> Query definition type
 * 
 * @since 5.2.0
 */
public abstract class AbstractReactiveQuery<D extends QueryDefinition> extends AbstractQueryBuilder<ReactiveQuery, D>
		implements ReactiveQuery {

	private static final long serialVersionUID = -3426437143295546886L;

	/**
	 * Constructor
	 * @param queryDefinition Query definition. Must be not <code>null</code>.
	 */
	public AbstractReactiveQuery(D queryDefinition) {
		super(queryDefinition);
	}

}
