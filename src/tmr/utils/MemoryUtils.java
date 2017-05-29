/*
Copyright 2017 tmr
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission. 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

package tmr.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * This class contains some very basic not-always-too-useful methods regarding the memory in the JVM. 
 * 
 * @author tmr
 * @version 1.0
 */
public class MemoryUtils {
	/**
	 * The current JVM runtime
	 */
	private static final Runtime RUNTIME = Runtime.getRuntime();
	/**
	 * The size of a "blob", the amount of memory to allocate in each iteration
	 */
	private static final long BLOB_SIZE = (long) Integer.MAX_VALUE / 2L - 4 * MemoryUtils.MB;
	/**
	 * THe size of the "blob" as an int
	 */
	private static final int BLOB_SIZE_AS_INT = (int) MemoryUtils.BLOB_SIZE;
	
	/**
	 * The new line character in the current system
	 */
	public static final String NEW_LINE = System.getProperty("line.separator", "\r\n");
	
	/**
	 * The size of a Megabyte in bytes
	 */
	public static final int MB = 1024 * 1024;
	
	/**
	 * Gets and returns the current amount of used memory by the JVM in bytes
	 * @return the current amount of used memory by the JVM in bytes
	 */
	public static long getUsedMemory() {
		return MemoryUtils.RUNTIME.totalMemory() - MemoryUtils.RUNTIME.freeMemory();
	}
	
	/**
	 * Retrieves and returns a string containing some information about the JVM's memory
	 * @return a string with information about the JVM's memory information
	 */
	public static String getJvmStats() {
		final String stats = "-JVM Status-" + MemoryUtils.NEW_LINE + 
		"\tTotal Memory: " + (MemoryUtils.RUNTIME.totalMemory() / MemoryUtils.MB) + " MB" + MemoryUtils.NEW_LINE + 
		"\tUsed Memory: " + (MemoryUtils.RUNTIME.totalMemory() - MemoryUtils.RUNTIME.freeMemory()) / MemoryUtils.MB + " MB" + MemoryUtils.NEW_LINE + 
		"\tFree Memory: " + MemoryUtils.RUNTIME.freeMemory() / MemoryUtils.MB + " MB" + MemoryUtils.NEW_LINE + 
		"\tMax Memory: " + MemoryUtils.RUNTIME.maxMemory() / MemoryUtils.MB + " MB";
		return stats;
	}
	
	/**
	 * Allocates(takes up) roughly the given percentage of the memory available to the JVM
	 * <br><br><b>NOTE:</b> This method doesn't always work too well, isn't always needed and can sometimes call the JVM to freeze!
	 * @param maxPercentOfMemoryToUse the percentage of memory available to the JVM to allocate
	 */
	public static void useMemory(final int maxPercentOfMemoryToUse) {
		final int NUMBER_OF_ITERATIONS = 16;
		
		List<byte[]> byteList = new LinkedList<byte[]>();
		
		try {
			for(int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
				final long maxTotalMemory = MemoryUtils.RUNTIME.maxMemory() / 100L * (maxPercentOfMemoryToUse - NUMBER_OF_ITERATIONS + i + 1L);
				final long numberOfBlobsToAllocate = maxTotalMemory / MemoryUtils.BLOB_SIZE;
				final long numberOfMoreBytesToAllocate = (maxTotalMemory % MemoryUtils.BLOB_SIZE) - 8L * MemoryUtils.MB;
				
				byteList.clear();
				byteList = null;
				byteList = new LinkedList<byte[]>();
				for(long l = 0; l < numberOfBlobsToAllocate; l++) {
					byteList.add(new byte[MemoryUtils.BLOB_SIZE_AS_INT]);
				}
				byteList.add(new byte[(int) numberOfMoreBytesToAllocate]);
			}
		} finally {
			System.gc();
		}
	}
}
