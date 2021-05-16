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
