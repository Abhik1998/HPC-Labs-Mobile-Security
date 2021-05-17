# Mobiblock - BTP

## Prerquisites
Please install the android sdk (from android-studio downloads page) and set the ANDROID_SDK path

## Running the code:
```
javac BlockChain.java
java BlockChain
```
## To run the updated feature extraction version:
```
javac blockchain.java
java blockchain
```

### Theoretical Time Complexity:
```
The precise theoretical time complexity can be given as: O(N*(mlog(m)+C)+Sum(Ti)),  
where, N = number of apk files to be analyzed,  
       m = number of permissions alloted to an apk,  
       C = Constant time taken to parse atmost 35 strings each with a maximum chahracter count of 82,  
       Sum(Ti) = represents sum of Time intervals over N apk files,  
       Ti = represents the time to recompile the apk bundle and generate the manifest.xml file.  
The number of permissions in an average comes out to be 10 for apk files and considering the bundle recompilation time to be constant,  
we arrive at a theoretical time complexity to be O(N).
```
### Query Time Complexity:
```
Suppose we make a query using the app name, then:  
Fetching app block address: O(1)-avg case | O(N)-worst case ** 
Querying the permissions:   O(logm)-avg case | worst case  
So, total complexity: O(logm) - avg case | O(N+logm)-worst case  

Other method which can be implemented to reduce the worst case time complexity:  
Implement a AVL Tree based Map rather than HashMap,  
Fetching app block address: O(logN)-avg case | worst case  
Querying the permissions:   O(logm)-avg case | worst case  
So, total complexity: O(logm+logN) - avg case | worst case (O(logmN) = O(logm + logN))  

where, N = number of apk files to be analyzed,  
       m = number of permissions given to an apk file. 

** JDK8+ has improved it, whenever there are 8+ elements in a bucket, rather than using a linkedList,   
   a tree is used reducing the worst case time complexity to O(logN)
```
