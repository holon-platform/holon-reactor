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

import reactor.core.publisher.Flux;

/**
 * Represents an reactive transactional operation execution.
 * 
 * @param <R> Operation result type
 *
 * @since 5.2.0
 */
@FunctionalInterface
public interface ReactiveTransactionalOperation<R> {

	/**
	 * Execute a transactional operation using given {@link ReactiveTransaction} and return a result.
	 * @param transaction The transaction reference, which can be used to perform {@link ReactiveTransaction#commit()}
	 *        and {@link ReactiveTransaction#rollback()} operations
	 * @return A {@link Flux} which can be used to handle the asynchronous operation outcome and the operation results
	 */
	Flux<R> execute(ReactiveTransaction transaction);

}
