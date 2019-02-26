/* 
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.context;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author  Kyle Stiemann
 */
public class ThreadSafeLazyAccessorTest {

    // Private Constants
    private static final long MAX_WAIT_TIME_IN_SECONDS = 3;
    private static final OnDemandTestResult STATIC_ON_DEMAND_TEST_RESULT = new OnDemandTestResult();
    private static final long WAIT_INTERVAL_IN_MILLIS = 10;

    // Private Final Data Members
    private final OnDemandTestResult onDemandTestResult = new OnDemandTestResult();

    private static void assertTrue(OnDemandTestResult onDemandTestResult) {
        Assert.assertTrue("More than " + MAX_WAIT_TIME_IN_SECONDS +
            " seconds elapsed waiting for the thread to be blocked.", onDemandTestResult.get());
    }

    private static void testThreadSafe(final OnDemandTestResult ON_DEMAND_TEST_RESULT) {

        // Create and start a thread that will get stuck in the while loop in TestResultInitializer until the current
        // thread requests the test result value.
        Thread waitingForWhileLoopThread = new Thread() {
                @Override
                public void run() {
                    assertTrue(ON_DEMAND_TEST_RESULT);
                }
            };

        waitingForWhileLoopThread.start();

        // Wait for waitingForWhileLoopThread to enter the while loop in TestResultInitializer.
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            // Do nothing.
        }

        // Attempt to obtain the test result. If the current thread is blocked, waitingForWhileLoopThread will exit the
        // while loop in ThreadSafeAccessor and the current thread will be unblocked and ThreadSafeAccessor.get() will
        // return true.
        assertTrue(ON_DEMAND_TEST_RESULT);
    }

    @Test
    public void testStaticThreadSafe() {
        testThreadSafe(STATIC_ON_DEMAND_TEST_RESULT);
    }

    @Test
    public void testThreadSafe() {
        testThreadSafe(onDemandTestResult);
    }

    private static final class OnDemandTestResult extends ThreadSafeLazyAccessor<Boolean> {

		private final Thread thread;

		public OnDemandTestResult() {
			this.thread = Thread.currentThread();
		}

        @Override
        protected Boolean computeValue() {

            int waitTime = 0;

            // Wait until the passed thread is blocked to prove that at most only 1 thread can access the
            // computeValue() method.
            while (!Thread.State.BLOCKED.equals(thread.getState())) {

                Assert.assertFalse(Thread.currentThread().equals(thread));

                // Sleep
                try {
                    Thread.sleep(WAIT_INTERVAL_IN_MILLIS);
                    waitTime += WAIT_INTERVAL_IN_MILLIS;
                }
                catch (InterruptedException e) {
                    // Do nothing.
                }

                // Fail the test (break the infinite loop) if the max wait time has been exceeded.
                if (waitTime > ((MAX_WAIT_TIME_IN_SECONDS + 1) * 1000)) {
                    return false;
                }
            }

            return true;
        }
    }
}
