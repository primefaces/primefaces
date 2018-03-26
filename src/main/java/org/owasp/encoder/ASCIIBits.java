// Copyright (c) 2012 Jeff Ichnowski
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
//
//     * Redistributions of source code must retain the above
//       copyright notice, this list of conditions and the following
//       disclaimer.
//
//     * Redistributions in binary form must reproduce the above
//       copyright notice, this list of conditions and the following
//       disclaimer in the documentation and/or other materials
//       provided with the distribution.
//
//     * Neither the name of the OWASP nor the names of its
//       contributors may be used to endorse or promote products
//       derived from this software without specific prior written
//       permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
// FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
// COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
// STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGE.

package org.owasp.encoder;

/**
 * ASCIIBits - Small helper class for building up 128-bit bit-mask (2 longs)
 * to be used testing lower-ASCII characters.  It helps make some other code
 * easier to read.  It is not intended to be public.
 */
class ASCIIBits {
    /** Lower 64 bits. */
    long _lowerMask;
    /** Upper 64 bits. */
    long _upperMask;

    /**
     * Sets a bit to 1 for each character in the argument string.  No checking
     * is performed to see if characters are in the valid range 0..127.
     *
     * @param chars the characters to set to 1.
     * @return {@code this}
     */
    ASCIIBits set(String chars) {
        for (int i=0, n=chars.length() ; i<n ; ++i) {
            char ch = chars.charAt(i);
            if (ch < 64) {
                _lowerMask |= (1L << ch);
            } else {
                _upperMask |= (1L << ch);
            }
        }
        return this;
    }

    /**
     * Clears the bit (sets to 0) for each character in the argument string.
     *
     * @param chars the characters to clear.
     * @return {@code this}
     */
    ASCIIBits clear(String chars) {
        for (int i=0, n=chars.length() ; i<n ; ++i) {
            char ch = chars.charAt(i);
            if (ch < 64) {
                _lowerMask &= ~(1L << ch);
            } else {
                _upperMask &= ~(1L << ch);
            }
        }
        return this;
    }

    /**
     * Sets a range of characters to 1s in the masks.
     *
     * @param min the minimum (inclusive).
     * @param max the maximum (inclusive).
     * @return {@code this}
     */
    ASCIIBits set(int min, int max) {
        // There are faster bit-twiddling tricks to accomplish the same
        // thing as below.  Minor optimization for later.
        int i=min;
        for (int n = Math.min(max,63) ; i<=n ; ++i) {
            _lowerMask |= (1L << i);
        }
        for ( ; i<=max ; ++i) {
            _upperMask |= (1L << i);
        }
        return this;
    }

    /**
     * Sets a range of characters to 0s in the masks.
     *
     * @param min the minimum (inclusive).
     * @param max the maximum (inclusive).
     * @return {@code this}
     */
    ASCIIBits clear(char min, char max) {
        // There are faster bit-twiddling tricks to accomplish the same
        // thing as below.  Minor optimization for later.
        int i=min;
        for (int n = Math.min(max,63) ; i<=n ; ++i) {
            _lowerMask &= ~(1L << i);
        }
        for ( ; i<=max ; ++i) {
            _upperMask &= ~(1L << i);
        }
        return this;
    }
}
