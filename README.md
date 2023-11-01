# CATPS-Challenge

## Report
### Task 3
FDB server worked great!

### Task 4
Created template class that other classes rely on to abstract operations with FDB server

### Task 5
Generated 10k key value pairs and measured getrange time in miliseconds.

### Task 6
Generated 10k key value pairs and measured thread creation and parallel getrange time in miliseconds. Expected to be faster than task 5 due to parallelism

### Task 7
Simultaneously run two transactions, one which reads some keys and another which modifies the same keys. No transaction conflict since read operations are marked as read-only and operate on a snapshot of the database. 
**1.** T1 commits successfully. Read transactions take place in an instantaneous snapshot when they are committed, so it will not encounter conflicts. Due to starting first, it will also not likely see T2's modified values.
**2.** Similarly, T2 will commit successfully. Read only transactions are not a concern when checking for conflicts, so T2 can run freely without concern. Thus, it will succeed in updating K2 and K4.

### Task 8
Simultaneously run two readwrite transactions. They write on the keys the other is reading. If interleaved, transaction conflict is expected.
**1.** If T1 and T2 have their operations interleaved (which is not a guarantee due to OS scheduling), I expect T2 to fail. This is because the range it is reading conflicts with the keys added by T1. This is a read-write conflict so the transaction aborts. If they are not interleaved, trivially the transaction will succeed.
**2.** If T1 and T2 have their operations interleaved (which is not a guarantee due to OS scheduling), I expect T1 to fail. For similar reasons as above, the range it is reading and writing is touched by T2 as well. My reference for the above 2 questions is the FDB developer guide. Specifically this quote: "if a concurrent transaction happens to insert a new key anywhere in the range, our transaction will conflict with it and fail (resulting in a retry) because seeing the other transaction’s write would change the result of the range read"

## Reflection

Unfortunately, I ran into setup issues that prevented me from running my Java code. However, I spent a lot of time reading documentation to understand and write functions that support the functions asked for. I believe my code is functional and I answered the questions to the best of my ability based on the documentation.

## How To Run  
Open as IntelliJ Maven java project. Make sure to refresh Maven dependencies to load libraries.  
add `DYLD_LIBRARY_PATH=/usr/local/lib` to environment variables for run config and run.

## Setup Issues  
**Non-Critical Challenges (Resolved!)**  
Had trouble with FDB setup because I switched versions for reasons detailed below. Sometimes new installations would break old clusters leading to quorum communication issues. Manual setup and deploying clusters + computer restarts fixed the issues.  
Also had issues due to low storage space on my local macine. fdbcli would constantly tell me my log server had 0 GB free. I found out that FoundationDB has a requirement to not take up the final 5% of storage space. Fixed this with knob_min_available_space_ratio=0.001 to lower that ratio.  

**Critical Challenges**  
I am using the 6.2.30 FoundationDB version. However, I cannot find any client jar with that version available to use as a library, and as such my code is running into UnsatisfiedLinkErrors due to not being able to load the library properly. I've checked https://central.sonatype.com/artifact/org.foundationdb/fdb-java/versions and https://mvnrepository.com/artifact/org.foundationdb/fdb-java/6.2.22, as well as following the link to download jars in the documentation. 
```
Exception in thread "main" java.lang.UnsatisfiedLinkError: Can't load library: /var/folders/dv/l589r_z574q2dpmcl52v_6900000gn/T/fdb_java7245580622193451096.library
at java.lang.ClassLoader.loadLibrary(ClassLoader.java:1823)
at java.lang.Runtime.load0(Runtime.java:782)
at java.lang.System.load(System.java:1098)
at com.apple.foundationdb.JNIUtil.loadLibrary(JNIUtil.java:102)
at com.apple.foundationdb.FDB.<clinit>(FDB.java:101)
at org.fdbChallenge.SingleGetRange.main(SingleGetRange.java:13)
```

I tried running with 6.2.22 (latest 6.2 version I could find) and 6.3.1 (earliest 6.3 version) but they all run into the same library loading error. Intellisense is able to parse these library's to identify imports, classes, and methods, but it seems it is failing to load the library at compile-time.

I also tried running with everything on version 6.3.23 (default MacOS version provided on download page) and still ran into the same linking issues. 

I am running with Oracle SDK 8 (I also tried with java 17) on MacOS 12.6.3. 

I also fully upgraded everything to 7.1 and the Linking errors persisted. :(

*Probable Causes* 
- Java JNI configuration with Intellij: This is my first time working with Maven in IntelliJ so I highly suspect this is the source of the error. I did extensive searching but was not able to resolve. 


