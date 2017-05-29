# Java Garbage Collection Preventer
 <b>TL;DR:</b> Using this code can easily cause the JVM to stop running! Don't use this unless you really know what you are doing!
 <br><br>This class contains methods that are experimental and will often in many cases cause the JVM to freeze and or crash and or cause other unexpected things. 
 <br><br>This is Java code that will disable (or at least prevent) the Garbage Collector in Java from running! It does so by locking an object that the GC needs to be able to perform the GC.
 <br><br>This obviously has some bad side effects. But if you know what you are doing, how much memory you have and what your Java program will need in the future, this might be a useful program for you. <br>Obviously by preventing the GC from running memory will never get released and much [free] memory won't even be able to be used! So make sure you know what you are doing before using this. 
 <br><br>This is probably <b>NOT</b> safe for production code, but can be used to play around with and experiment with. <br>You can for instance use it to prevent the GC from running during critical sections (sections you need to run quickly without any interruptions from the GC) giving you a more exact run time. <br>You should make sure to enable the GC again before too much happens and the JVM stops running! 
 <br><br>This code will probably run on most platforms (Windows, Linux etc.) with OpenJDK and Oracle's JDK versions 8 & 9. However since this program is based on a specific object that the JVM needs to run the GC it might not work on all implementations since they might implement the GC differently or in future version which might change this. This code would probably even run on OpenJDK or Oracle's JDK versions 5-7 if you get rid of the use of Functional Interfaces. 
 <br><br>Using this program can easily cause a JVM to crash - or at least stop running!
 <br><br><b>Note:</b> New objects are allocated to the Eden part of the heap, so even if you set your heap to be 8 gigabytes you can only use the Eden part which is by default considerbly smaller
<br><br><br>Example: <br>
src>java -Xms8g -Xmx8g example.tmr.utils.UtilsExamples 2
<br>Before Memory Allocation -JVM Status-
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Total Memory: 7851 MB
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Used Memory: 81 MB
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Free Memory: 7769 MB
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Max Memory: 7851 MB
<br>
<br>After Memory Allocation -JVM Status-
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Total Memory: 8191 MB
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Used Memory: 6994 MB
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Free Memory: 1196 MB
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Max Memory: 8191 MB
<br>
<br>1)      -----
<br>No GC took 916 milliseconds
<br>No GC took 763 milliseconds
<br>With GC took 758 milliseconds
<br>With GC took 1506 milliseconds
<br>
<br>2)      -----
<br>No GC took 787 milliseconds
<br>No GC took 728 milliseconds
<br>With GC took 729 milliseconds
<br>With GC took 999 milliseconds
<br>
<br>3)      -----
<br>No GC took 745 milliseconds
<br>No GC took 768 milliseconds
<br>With GC took 755 milliseconds
<br>With GC took 937 milliseconds
<br>
<br>4)      -----
<br>No GC took 723 milliseconds
<br>No GC took 725 milliseconds
<br>With GC took 727 milliseconds
<br>With GC took 967 milliseconds
<br>
<br>5)      -----
<br>No GC took 732 milliseconds
<br>No GC took 731 milliseconds
<br>With GC took 731 milliseconds
<br>With GC took 933 milliseconds
<br>
<br>-JVM Status-
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Total Memory: 8116 MB
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Used Memory: 2566 MB
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Free Memory: 5549 MB
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Max Memory: 8116 MB
