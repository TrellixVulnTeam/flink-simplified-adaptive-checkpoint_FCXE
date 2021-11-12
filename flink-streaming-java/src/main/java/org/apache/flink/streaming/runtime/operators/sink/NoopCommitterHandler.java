/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.streaming.runtime.operators.sink;

import org.apache.flink.util.function.SupplierWithException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/** Swallows all committables and emits nothing. */
enum NoopCommitterHandler implements CommitterHandler<Object, Object> {
    INSTANCE;

    @SuppressWarnings("unchecked")
    static <InputT, OutputT> CommitterHandler<InputT, OutputT> getInstance() {
        return (CommitterHandler<InputT, OutputT>) NoopCommitterHandler.INSTANCE;
    }

    @Override
    public List<Object> processCommittables(
            SupplierWithException<List<Object>, Exception> committableSupplier) {
        return Collections.emptyList();
    }

    @Override
    public void close() throws Exception {}

    @Override
    public boolean needsRetry() {
        return false;
    }

    @Override
    public void retry() throws IOException, InterruptedException {}
}
