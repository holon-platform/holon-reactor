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
package com.holonplatform.reactor.datastore;

import java.io.Serializable;

import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.ExpressionResolver.ExpressionResolverSupport;
import com.holonplatform.core.datastore.ConfigurableDatastore;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.DatastoreCommodity;
import com.holonplatform.core.datastore.DatastoreCommodityHandler;
import com.holonplatform.core.datastore.DatastoreOperations;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.reactor.datastore.operation.ReactiveBulkDelete;
import com.holonplatform.reactor.datastore.operation.ReactiveBulkInsert;
import com.holonplatform.reactor.datastore.operation.ReactiveBulkUpdate;
import com.holonplatform.reactor.datastore.operation.ReactiveDelete;
import com.holonplatform.reactor.datastore.operation.ReactiveInsert;
import com.holonplatform.reactor.datastore.operation.ReactiveQuery;
import com.holonplatform.reactor.datastore.operation.ReactiveRefresh;
import com.holonplatform.reactor.datastore.operation.ReactiveSave;
import com.holonplatform.reactor.datastore.operation.ReactiveUpdate;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive {@link DatastoreOperations} API, which can be used to execute the Datastore operations and obtain the the
 * results asynchronously, using {@link Flux} and {@link Mono} to represent and handle the operations outcome.
 * <p>
 * Extends {@link DatastoreCommodityHandler} to support {@link DatastoreCommodity} creation by type.
 * </p>
 * <p>
 * Extends {@link ExpressionResolverSupport} to allow {@link ExpressionResolver}s registration, which can be used to
 * extend and/or customize the datastore operations.
 * </p>
 * 
 * @see Datastore
 *
 * @since 5.2.0
 */
public interface ReactiveDatastore extends
		DatastoreOperations<Mono<OperationResult>, Mono<PropertyBox>, ReactiveBulkInsert, ReactiveBulkUpdate, ReactiveBulkDelete, ReactiveQuery>,
		ConfigurableDatastore, Serializable {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.DatastoreOperations#refresh(com.holonplatform.core.datastore.DataTarget,
	 * com.holonplatform.core.property.PropertyBox)
	 */
	@Override
	default Mono<PropertyBox> refresh(DataTarget<?> target, PropertyBox propertyBox) {
		try {
			return create(ReactiveRefresh.class).target(target).value(propertyBox).execute();
		} catch (Exception e) {
			throw new DataAccessException("REFRESH operation failed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.DatastoreOperations#insert(com.holonplatform.core.datastore.DataTarget,
	 * com.holonplatform.core.property.PropertyBox, com.holonplatform.core.datastore.DatastoreOperations.WriteOption[])
	 */
	@Override
	default Mono<OperationResult> insert(DataTarget<?> target, PropertyBox propertyBox, WriteOption... options) {
		try {
			return create(ReactiveInsert.class).target(target).value(propertyBox).withWriteOptions(options).execute();
		} catch (Exception e) {
			throw new DataAccessException("INSERT operation failed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.DatastoreOperations#update(com.holonplatform.core.datastore.DataTarget,
	 * com.holonplatform.core.property.PropertyBox, com.holonplatform.core.datastore.DatastoreOperations.WriteOption[])
	 */
	@Override
	default Mono<OperationResult> update(DataTarget<?> target, PropertyBox propertyBox, WriteOption... options) {
		try {
			return create(ReactiveUpdate.class).target(target).value(propertyBox).withWriteOptions(options).execute();
		} catch (Exception e) {
			throw new DataAccessException("UPDATE operation failed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.DatastoreOperations#save(com.holonplatform.core.datastore.DataTarget,
	 * com.holonplatform.core.property.PropertyBox, com.holonplatform.core.datastore.DatastoreOperations.WriteOption[])
	 */
	@Override
	default Mono<OperationResult> save(DataTarget<?> target, PropertyBox propertyBox, WriteOption... options) {
		try {
			return create(ReactiveSave.class).target(target).value(propertyBox).withWriteOptions(options).execute();
		} catch (Exception e) {
			throw new DataAccessException("SAVE operation failed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.DatastoreOperations#delete(com.holonplatform.core.datastore.DataTarget,
	 * com.holonplatform.core.property.PropertyBox, com.holonplatform.core.datastore.DatastoreOperations.WriteOption[])
	 */
	@Override
	default Mono<OperationResult> delete(DataTarget<?> target, PropertyBox propertyBox, WriteOption... options) {
		try {
			return create(ReactiveDelete.class).target(target).value(propertyBox).withWriteOptions(options).execute();
		} catch (Exception e) {
			throw new DataAccessException("DELETE operation failed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.DatastoreOperations#bulkInsert(com.holonplatform.core.datastore.DataTarget,
	 * com.holonplatform.core.property.PropertySet, com.holonplatform.core.datastore.DatastoreOperations.WriteOption[])
	 */
	@Override
	default ReactiveBulkInsert bulkInsert(DataTarget<?> target, PropertySet<?> propertySet, WriteOption... options) {
		return create(ReactiveBulkInsert.class).target(target).propertySet(propertySet).withWriteOptions(options);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.DatastoreOperations#bulkUpdate(com.holonplatform.core.datastore.DataTarget,
	 * com.holonplatform.core.datastore.DatastoreOperations.WriteOption[])
	 */
	@Override
	default ReactiveBulkUpdate bulkUpdate(DataTarget<?> target, WriteOption... options) {
		return create(ReactiveBulkUpdate.class).target(target).withWriteOptions(options);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.DatastoreOperations#bulkDelete(com.holonplatform.core.datastore.DataTarget,
	 * com.holonplatform.core.datastore.DatastoreOperations.WriteOption[])
	 */
	@Override
	default ReactiveBulkDelete bulkDelete(DataTarget<?> target, WriteOption... options) {
		return create(ReactiveBulkDelete.class).target(target).withWriteOptions(options);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.DatastoreOperations#query()
	 */
	@Override
	default ReactiveQuery query() {
		return create(ReactiveQuery.class);
	}

	/**
	 * Create a {@link ReactiveQuery} commodity, setting given <code>target</code> as query data target.
	 * @param target Query data target (not null)
	 * @return A new {@link ReactiveQuery} instance, which can be used to configure and execute an asynchronous query
	 */
	default ReactiveQuery query(DataTarget<?> target) {
		ObjectUtils.argumentNotNull(target, "Query target must be not null");
		return query().target(target);
	}

}
