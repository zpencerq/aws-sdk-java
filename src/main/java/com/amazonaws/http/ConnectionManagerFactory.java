/*
 * Copyright 2011-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.http;

import org.apache.http.config.Registry;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.amazonaws.ClientConfiguration;

import java.util.concurrent.TimeUnit;

/** Responsible for creating and configuring instances of Apache HttpClient4's Connection Manager. */
class ConnectionManagerFactory {

    public static PoolingHttpClientConnectionManager createPoolingClientConnManager(ClientConfiguration config, Registry<ConnectionSocketFactory> registry) {
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setDefaultMaxPerRoute(config.getMaxConnections());
        connectionManager.setMaxTotal(config.getMaxConnections());
        if (config.useReaper()) {
            IdleConnectionReaper.registerConnectionManager(new ClientConnectionManager() {
                public SchemeRegistry getSchemeRegistry() {
                    throw new UnsupportedOperationException();
                }

                public ClientConnectionRequest requestConnection(HttpRoute httpRoute, Object o) {
                    throw new UnsupportedOperationException();
                }

                public void releaseConnection(ManagedClientConnection managedClientConnection, long l, TimeUnit timeUnit) {
                    throw new UnsupportedOperationException();
                }

                public void closeIdleConnections(long l, TimeUnit timeUnit) {
                    connectionManager.closeIdleConnections(l, timeUnit);
                }

                public void closeExpiredConnections() {
                    connectionManager.closeExpiredConnections();
                }

                public void shutdown() {
                    connectionManager.shutdown();
                }
            });
        }
        return connectionManager;
    }
}