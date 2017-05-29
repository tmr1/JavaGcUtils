/*
Copyright 2017 tmr
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission. 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

package tmr.utils;

import java.lang.reflect.Field;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <b>TL;DR:</b> Using this code can easily cause the JVM to stop running! Don't use this unless you really know what you are doing!
 * <br><br>This class contains methods that are experimental and will often in many cases cause the JVM to freeze and or crash and or cause other unexpected things. 
 * <br><br>This is Java code that will disable (or at least prevent) the Garbage Collector in Java from running! It does so by locking an object that the GC needs to be able to perform the GC.
 * <br><br>This obviously has some bad side effects. But if you know what you are doing, how much memory you have and what your Java program will need in the future, this might be a useful program for you. <br>Obviously by preventing the GC from running memory will never get released and much [free] memory won't even be able to be used! So make sure you know what you are doing before using this. 
 * <br><br>This is probably <b>NOT</b> safe for production code, but can be used to play around with and experiment with. <br>You can for instance use it to prevent the GC from running during critical sections (sections you need to run quickly without any interruptions from the GC) giving you a more exact run time. <br>You should make sure to enable the GC again before too much happens and the JVM stops running! 
 * <br><br>This code will probably run on most platforms (Windows, Linux etc.) with OpenJDK and Oracle's JDK versions 8 & 9. However since this program is based on a specific object that the JVM needs to run the GC it might not work on all implementations since they might implement the GC differently or in future version which might change this. This code would probably even run on OpenJDK or Oracle's JDK versions 5-7 if you get rid of the use of Functional Interfaces. 
 * <br><br>Using this program can easily cause a JVM to crash - or at least stop running!
 * <br><br><b>Note:</b> New objects are allocated to the Eden part of the heap, so even if you set your heap to be 8 gigabytes you can only use the Eden part which is by default considerbly smaller
 * 
 * @author tmr
 * @version 1.0
 */
public final class GcUtils {
	/**
	 * Possible statuses that can be returned when disabling/enabling the Garbage Collection
	 * @author tmr1
	 * @version 1.0
	 */
	public enum Status { STARTED, IS_ALREADY_RUNNING, IS_NOT_RUNNING, STOPPED, FAILED_TO_DISABLE_GC_DUE_TO_ERROR }
	
	/**
	 * This lock makes sure that {@link #enableGc} and {@link #disableGc} won't be called multiple times at the same time
	 */
	private static final Lock enableDisableGcLock = new ReentrantLock();
	
	/**
	 * The lock used to create a condition to wait on until it is released - the Garbage Collection is enabled again
	 */
	private static final Lock waitingLock = new ReentrantLock();
	/**
	 * The condition to wait on until it is released - the Garbage Collection is enabled again
	 */
	private static final Condition waitingLockCondition = GcUtils.waitingLock.newCondition();
	
	/**
	 * The thread that keeps the synchronization on {@link #gcLock}
	 */
	private static volatile Thread thread = null;
	
	/**
	 * The object that the JVM tries to synchronize on in order to run garbage collection, 
	 * which is what this feature synchronizes on in order to prevent the Garbage Collection
	 */
	private static volatile Object gcLock = null;
	
	/**
	 * "Disables" the JVM's Garbage Collection
	 * This method basically synchronizes on an object used by the JRE's Garbage Collection which thereby prevents the Garbage Collection from running. 
	 * <br><br><b>NOTE:</b> This method can cause the JVM to freeze and/or crash or not function well in other ways. 
	 * This Function is inherently unstable and more of an experimental feature than a production feature. 
	 * This method may or may not function, depending on on which JVM, JVM version and platform. 
	 * @return the result from attempting to disable the Garbage Collection
	 */
	public static Status disableGc() {
		GcUtils.enableDisableGcLock.lock();
		try {
			if(GcUtils.gcLock == null) {
				final Field gcLockField;
				try {
					gcLockField = java.lang.ref.Reference.class.getDeclaredField("lock");
				} catch (final NoSuchFieldException e) {
					e.printStackTrace();
					return Status.FAILED_TO_DISABLE_GC_DUE_TO_ERROR;
				} catch (final SecurityException e) {
					e.printStackTrace();
					return Status.FAILED_TO_DISABLE_GC_DUE_TO_ERROR;
				}
				gcLockField.setAccessible(true);
				
				try {
					GcUtils.gcLock = gcLockField.get(null);
				} catch (final IllegalArgumentException e) {
					e.printStackTrace();
					return Status.FAILED_TO_DISABLE_GC_DUE_TO_ERROR;
				} catch (final IllegalAccessException e) {
					e.printStackTrace();
					return Status.FAILED_TO_DISABLE_GC_DUE_TO_ERROR;
				}
			}
			
			if(GcUtils.thread != null) {
				return Status.IS_ALREADY_RUNNING;
			}
			
			System.gc();
			
			GcUtils.thread = new Thread(() -> {
				synchronized (GcUtils.gcLock) {
					GcUtils.waitingLock.lock();
					try {
						GcUtils.waitingLockCondition.awaitUninterruptibly();
					} finally {
						GcUtils.waitingLock.unlock();
					}
				}
			}, "GC Disabler Thread");
			thread.start();
			
			return Status.STARTED;
		} finally {
			GcUtils.enableDisableGcLock.unlock();
		}
	}
	
	/**
	 * "Enables" the JVM's Garbage Collection
	 * This method "enables" the JVM's Garbage Collection again after it has been disabled by {@link #disableGc}
	 * @return the result from attempting to enable the Garbage Collection
	 */
	public static Status enableGc() {
		GcUtils.enableDisableGcLock.lock();
		try {
			if(GcUtils.thread == null || GcUtils.gcLock == null) {
				return Status.IS_NOT_RUNNING;
			}
			
			GcUtils.thread = null;
			GcUtils.waitingLock.lock();
			try {
				GcUtils.waitingLockCondition.signalAll();
			} finally {
				GcUtils.waitingLock.unlock();
			}
			
			return Status.STOPPED;
		} finally {
			System.gc();
			
			GcUtils.enableDisableGcLock.unlock();
		}
	}
}
