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

import com.holonplatform.core.datastore.operation.commons.ExecutablePropertyBoxOperation;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.reactor.datastore.ReactiveDatastore;

import reactor.core.publisher.Mono;

/**
 * Reactive <em>refresh</em> {@link ExecutablePropertyBoxOperation} operation, using a {@link Mono} to represent and
 * handle the operation result.
 *
 * @since 5.2.0
 * 
 * @see ReactiveDatastore
 */
public interface ReactiveRefresh extends ExecutablePropertyBoxOperation<Mono<PropertyBox>, ReactiveRefresh> {

}
