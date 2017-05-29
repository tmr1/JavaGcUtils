package example.tmr.utils;

import java.util.LinkedList;
import java.util.List;

import tmr.utils.GcUtils;
import tmr.utils.MemoryUtils;

/**
 * Copyright 2017 tmr
 * <br><br>Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * <br><br>1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * <br><br>2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * <br><br>3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission. 
 * <br><br>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * <br><br><br>Some simple examples of how to enable/disable (prevent) the Garbage Collection
 * 
 * @author tmr
 * @version 1.0
 */
public class UtilsExamples {
	public static void main(final String[] args) {
		if(args.length == 1) {
			if(args[0].equals("1")) {
				//Run example 1
				UtilsExamples.example1();
			} else if(args[0].equals("2")) {
				//Run example 2
				UtilsExamples.example2();
			} else if(args[0].equals("3")) {
				//Run example 3
				System.out.println(MemoryUtils.getJvmStats());
				System.out.println("Roughly were " + UtilsExamples.example3(1024) / MemoryUtils.MB + " megabytes are used");
				System.out.println(MemoryUtils.NEW_LINE + MemoryUtils.getJvmStats());
			} else {
				System.err.println("Which example do you want to run?");
			}
		} else {
			System.err.println("Which example do you want to run?");
		}
	}
	
	/**
	 * The first example
	 * <br>Doesn't always work and can cause the JVM to freeze/hang, especially if there isn't enough memory to perform these actions
	 */
	public static void example1() {
		System.out.println(MemoryUtils.getJvmStats());
		
		for(int i = 0; i < 5; i++) {
			System.out.println();
			System.out.println((i + 1) + ")\t-----");
			System.out.println("No GC took " + UtilsExamples.memoryAllocationTestWithNoGc() + " milliseconds");
			System.out.println("No GC took " + UtilsExamples.memoryAllocationTestWithNoGc() + " milliseconds");
			System.out.println("With GC took " + UtilsExamples.memoryAllocationTestWithGc() + " milliseconds");
			System.out.println("With GC took " + UtilsExamples.memoryAllocationTestWithGc() + " milliseconds");
		}
		
		System.out.println(MemoryUtils.NEW_LINE + MemoryUtils.getJvmStats());
	}
	
	/**
	 * The second example
	 * <br>Doesn't always work and can cause the JVM to freeze/hang, especially if there isn't enough memory to perform these actions
	 */
	public static void example2() {
		System.out.println("Before Memory Allocation " + MemoryUtils.getJvmStats());
		MemoryUtils.useMemory(85);	//This line is the only difference between example 1 and exmaple 2
		System.out.println(MemoryUtils.NEW_LINE + "After Memory Allocation " + MemoryUtils.getJvmStats());
		
		for(int i = 0; i < 5; i++) {
			System.out.println();
			System.out.println((i + 1) + ")\t-----");
			System.out.println("No GC took " + UtilsExamples.memoryAllocationTestWithNoGc() + " milliseconds");
			System.out.println("No GC took " + UtilsExamples.memoryAllocationTestWithNoGc() + " milliseconds");
			System.out.println("With GC took " + UtilsExamples.memoryAllocationTestWithGc() + " milliseconds");
			System.out.println("With GC took " + UtilsExamples.memoryAllocationTestWithGc() + " milliseconds");
		}
		
		System.out.println(MemoryUtils.NEW_LINE + MemoryUtils.getJvmStats());
	}
	
	/**
	 * The third example
	 * <br>Allocates the given number of megabytes
	 * @param megabytes the number of megabytes to allocate
	 * @return the amount of more memory used after running this method
	 */
	public static long example3(final int megabytes) {
		GcUtils.disableGc();
		try {
			long usedMemory = MemoryUtils.getUsedMemory();
			List<byte[]> list = new LinkedList<byte[]>();
			final long bytes = megabytes;
			for(long l = 0; l < bytes; l++) {
				list.add(new byte[MemoryUtils.MB]);
			}
			list.clear();
			list = null;
			usedMemory = MemoryUtils.getUsedMemory() - usedMemory;
			
			System.out.println(MemoryUtils.NEW_LINE + MemoryUtils.getJvmStats() + MemoryUtils.NEW_LINE);
			
			return usedMemory;
		} finally {
			GcUtils.enableGc();
		}
	}
	
	/**
	 * Allocates memory and then immediately lets the Garbage Collector collect it
	 * In this method you can see that iterations of this method will take longer than others since the Garbage Collector kicks in
	 * @return the amount of time it took
	 */
	private static long memoryAllocationTestWithGc() {
		long time = -1L;
		time = System.currentTimeMillis();
		allocateMemory();
		time = System.currentTimeMillis() - time;
		return time;
	}
	
	/**
	 * Allocates memory and then immediately lets the Garbage Collector collect it - except the Garbage Collection is disabled
	 * In this method you can see that iterations of this method will take roughly the same amount of time to execute, spite it doing basically the same thing as in X, since the Garbage Collector is prevented from running
	 * <br><b>Obviously this method can <u>easily</u> cause the JVM to hang/freeze</b>
	 * @return the amount of time it took
	 */
	private static long memoryAllocationTestWithNoGc() {
		long time = -1L;
		GcUtils.disableGc();
		try {
			time = System.currentTimeMillis();
			allocateMemory();
			time = System.currentTimeMillis() - time;
		} finally {
			GcUtils.enableGc();
		}
		return time;
	}
	
	/**
	 * Allocates some memory and then lets it be garbage collected
	 */
	private static void allocateMemory() {
		List<Object> list = new LinkedList<Object>();
		final long M = MemoryUtils.MB * 64L;
		for(long l = 0; l < M; l++) {
			final Object o = new Object();
			list.add(o);
		}
		list.clear();
		list = null;
	}
}