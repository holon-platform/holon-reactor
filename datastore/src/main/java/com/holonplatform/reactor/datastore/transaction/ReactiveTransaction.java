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

import com.holonplatform.core.datastore.transaction.TransactionStatus;

import reactor.core.publisher.Mono;

/**
 * Represents an reactive Datastore <em>transaction</em>, providing methods to <em>commit</em> and <em>rollback</em> the
 * transaction.
 *
 * @since 5.2.0
 */
public interface ReactiveTransaction extends TransactionStatus {

	/**
	 * Commit the transaction.
	 * <p>
	 * If the transaction has been marked as rollback-only, a rollback action is performed.
	 * </p>
	 * @return A {@link Mono} which can be used to handle the operation outcome. The result will be <code>true</code> if
	 *         the transaction was actually committed, or <code>false</code> if it was rolled back because the the
	 *         transaction has been marked as rollback-only
	 * @throws IllegalTransactionStatusException If the transaction is already completed (that is, committed or rolled
	 *         back)
	 * @throws TransactionException If an error occurred during transaction commit
	 */
	Mono<Boolean> commit();

	/**
	 * Rollback the transaction.
	 * @return A {@link Mono} which can be used to handle the operation outcome. No result value is expected.
	 * @throws IllegalTransactionStatusException If the transaction is already completed (that is, committed or rolled
	 *         back)
	 * @throws TransactionException If an error occurred during transaction rollback
	 */
	Mono<Void> rollback();

}
