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
package com.holonplatform.reactor.datastore.transaction;

import com.holonplatform.core.datastore.transaction.TransactionConfiguration;

import reactor.core.publisher.Flux;

/**
 * Represents an object which is capable to manage reactive transactions, providing methods to execute an operation
 * transactionally and using {@link ReactiveTransaction} as transaction handler.
 *
 * @since 5.2.0
 */
public interface ReactiveTransactional {

	/**
	 * Execute given operation within a transaction and return a result. An {@link ReactiveTransaction} reference is
	 * provided to perform <code>commit</code> and <code>rollback</code> operations.
	 * @param <R> Operation result type
	 * @param operation Operation to execute (not null)
	 * @param transactionConfiguration Transaction configuration
	 * @return A {@link Flux} which can be used to handle the asynchronous operation outcome and the operation results
	 */
	<R> Flux<R> withTransaction(ReactiveTransactionalOperation<R> operation,
			TransactionConfiguration transactionConfiguration);

	/**
	 * Execute given operation within a transaction and return a result. An {@link ReactiveTransaction} reference is
	 * provided to perform <code>commit</code> and <code>rollback</code> operations.
	 * @param <R> Operation result type
	 * @param operation Operation to execute (not null)
	 * @return A {@link Flux} which can be used to handle the asynchronous operation outcome and the operation results
	 */
	default <R> Flux<R> withTransaction(ReactiveTransactionalOperation<R> operation) {
		return withTransaction(operation, TransactionConfiguration.getDefault());
	}

}
