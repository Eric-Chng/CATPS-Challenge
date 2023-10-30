# CATPS-Challenge
I ran into on setup. I will detail them as follows before diving into the subtasks.

## How To Run  
Open as IntelliJ Maven java project. Make sure to refresh Maven dependencies to load libraries.

## Setup Issues  
**Non-Critical Challenges**  
Had trouble with FDB setup because I switched versions for reasons detailed below. Sometimes new installations would break old clusters leading to quorum communication issues. Manual setup and deploying clusters + computer restarts fixed the issues.  
Also had issues due to low storage space on my local macine. fdbcli would constantly tell me my log server had 0 GB free. I found out that FoundationDB has a requirement to not take up the final 5% of storage space. Fixed this with knob_min_available_space_ratio=0.001 to lower that ratio.  

**Critical Challenges**  
I am using the 6.2.30 FoundationDB version. However, I cannot find any client jar with that version available to use as a library, and as such my code is running into UnsatisfiedLinkErrors due to not being able to load the library properly. I've checked https://central.sonatype.com/artifact/org.foundationdb/fdb-java/versions and https://mvnrepository.com/artifact/org.foundationdb/fdb-java/6.2.22, as well as following the link to download jars in the documentation. 

I tried running with 6.2.22 (latest 6.2 version I could find) and 6.3.1 (earliest 6.3 version) but they all run into the same library loading error. Intellisense is able to parse these library's to identify imports, classes, and methods, but it seems it is failing to load the library at compile-time.

I also tried running with everything on version 6.3.23 (default MacOS version provided on download page) and still ran into the same linking issues. 

I am running with Oracle SDK 8 (I also tried with java 17) on MacOS 12.6.3. 

*Potential Causes* 
- Mismatched FDB versions
- Java JNI configuration with Intellij: This is my first time working with Maven in IntelliJ so I highly suspect this is the source of the error. I did extensive searching 
