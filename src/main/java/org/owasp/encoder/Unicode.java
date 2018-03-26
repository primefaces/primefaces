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
 * ASCII and Unicode constants.
 *
 * @author jeffi
 */
final class Unicode {

    /**
     * Highest ASCII (usually) valid ASCII char.
     */
    static final char MAX_ASCII = '~';

    /**
     * ASCII "DEL" character.
     */
    static final char DEL = 0x7f;

    /**
     * "Next Line" C1 control character.
     */
    static final char NEL = 0x85;

    /**
     * Highest C1 control character.
     */
    static final char MAX_C1_CTRL_CHAR = 0x9f;

    /**
     * Unicode line separator character, must be encoded in some contexts.
     */
    static final char LINE_SEPARATOR = '\u2028';

    /**
     * Unicode paragraph separator character, must be encoded in some contexts.
     */
    static final char PARAGRAPH_SEPARATOR = '\u2029';

    /**
     * Bit-mask for Unicode non-characaters (XXfffe and XXffff).
     */
    static final int NON_CHAR_MASK = 0xfffe;

    /**
     * Returns true if the argument is not a character according to the Unicode
     * standard. Non-characters have the format XXfffe and XXffff, where XX is
     * any code plane, and "fffe/ffff" is the low 16-bits in hex.
     *
     * @param cp the unicode code-point to check
     * @return true if {@code cp} is not a character.
     */
    static boolean isNonCharacter(int cp) {
        return (cp & NON_CHAR_MASK) == NON_CHAR_MASK;
    }

    /**
     * No instances.
     */
    private Unicode() {
    }
}
