/// <reference path="./ref.d.ts" />
// Tests whether comments are passed through
/**
 * namespace
 */
declare namespace CommentThroughpass {
    // single class foo
    /**
     * multi class foo
     */
    class Foo {
        // inside foo
        /**
         * @tag a tag
         */
        bar(): void;
    }
    namespace Nest1 {
        namespace Nest2 {
            namespace Nest3 {
                /**
                 * Deeply nested comment 
                 */
                namespace Nest4 {
        
                }
            }
        }
    }
}