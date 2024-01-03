/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.selenium.internal.junit;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.primefaces.selenium.internal.ConfigProvider;
import org.primefaces.selenium.spi.DeploymentAdapter;

public class BootstrapExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static final Object SYNCHRONIZER = new Object();

    private static boolean started = false;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        synchronized (SYNCHRONIZER) {
            if (!started) {
                ConfigProvider configProvider = ConfigProvider.getInstance();

                DeploymentAdapter deploymentAdapter = configProvider.getDeploymentAdapter();
                if (deploymentAdapter != null) {
                    deploymentAdapter.startup();
                }

                // The following line registers a callback hook when the root test context is shut down
                context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put(BootstrapExtension.class.getName(), this);

                started = true;
            }
        }
    }

    @Override
    public void close() throws Exception {
        DeploymentAdapter deploymentAdapter = ConfigProvider.getInstance().getDeploymentAdapter();
        if (deploymentAdapter != null) {
            deploymentAdapter.shutdown();
        }
    }

}
