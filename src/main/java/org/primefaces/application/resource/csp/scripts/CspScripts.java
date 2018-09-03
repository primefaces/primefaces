/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.application.resource.csp.scripts;

import java.io.Serializable;
import java.util.Set;

/**
 * Holder for all nonces and hashes that have been generated and are ready to be used for constructing the response header.
 */
public class CspScripts implements Serializable {

    private final Set<String> nonces;
    private final Set<String> sha256Hashes;

    public CspScripts(Set<String> nonces, Set<String> sha256Hashes) {
        this.nonces = nonces;
        this.sha256Hashes = sha256Hashes;
    }

    public Set<String> getNonces() {
        return nonces;
    }

    public Set<String> getSha256Hashes() {
        return sha256Hashes;
    }

}
