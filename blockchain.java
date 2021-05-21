import java.util.SortedSet;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.security.NoSuchAlgorithmException;
import java.lang.instrument.Instrumentation;



class blockchain{

    //Declare the data structures to be used
    private static ArrayList<Block> blockChainList;
    private static int size;
    static HashMap<String, Block> cache;

    //Inner classes
    static class Static{
        SortedSet<String> permission;
        Static(){
            this.permission = new TreeSet<>();
        }
        public String getString(){
            StringBuilder sb=new StringBuilder();
            for(String x: permission)
                sb.append(x);
            return sb.toString();
        }
    }
    static class Block extends sha256{
        private String previousHash;
        private Static data;
        private String hash;
    
        public Block(Static data, String previousHash) throws NoSuchAlgorithmException {
            this.data = data;
            this.previousHash = previousHash;
            this.hash  = toHexString(getSHA(data.getString()+"#"+previousHash));
        }
        public String getHash(){
            return this.hash;
        }
        public String getPreviousHash(){
            return this.previousHash;
        }
        
    }
    public static void main(String[] args)throws Exception {
        blockChainList =  new ArrayList<>();
        cache = new HashMap<>();
        long start = System.currentTimeMillis();
        ArrayList<String> results = new ArrayList<>();

        File[] files = new File("Dataset_Maldroid_2020/Riskware").listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null. 
        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());

            }
        }
        int id=0;
        System.out.println("Id"+"                     "+"Memory"+"                     "+"Permission Size");
        for(String name: results)
        {
            ProcessBuilder fb=new ProcessBuilder();
            System.gc();
            long init=Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            String tool="$ANDROID_SDK/build-tools/30.0.3/aapt2 d xmltree --file AndroidManifest.xml $HOME/Desktop/MobiBlock/mobiblock-master/Dataset_Maldroid_2020/Riskware/"+name+"> temp/"+id+".txt";
            //String tool="$ANDROID_SDK/build-tools/30.0.3/aapt2 d xmltree --file AndroidManifest.xml $HOME/class/BTP/Dataset_Maldroid_2020/Riskware/"+name+"> temp/"+id+".txt";
            //System.out.println(tool);
            fb.command("bash", "-c", tool);
            Process process = fb.start();
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                File file=new File("temp/"+id+".txt");
                id++;
                Scanner sc=new Scanner(file);
                SortedSet<String> permissions=new TreeSet<>();
                if(sc.hasNextLine()){
                    String data = sc.nextLine().trim();
                    while(data.indexOf("uses-permission")<0){
                        if(!sc.hasNextLine())
                            break;
                        data = sc.nextLine().trim();
                    }
                    if(!sc.hasNextLine())
                        continue;
                    data = sc.nextLine().trim();
                    String p="permission.";
                    int temp=p.length();
                    int count=0;
                    while(sc.hasNextLine()){
                        count++;
                        if(count>=30)
                            break;
                        
                        if(data.indexOf(p)>=0)
                            {
                                
                                int k=data.indexOf(p)+temp;
                                String l=data.substring(k, data.indexOf("\"", k));
                                permissions.add(l);
                                data = sc.nextLine();
                                data=sc.nextLine().trim();
                            }
                        else
                            data = sc.nextLine();
                    }
                }
                Static appdata=new Static();
                appdata.permission=Collections.unmodifiableSortedSet(permissions);
                add(name, appdata);
               // sc.close();
                //System.out.println(id+" "+permissions);
                System.gc();
                long fin=Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                System.out.println(id+"                     "+(fin-init)+"                     "+permissions.size());
            }

            long time = System.currentTimeMillis() - start;
            
        }
        long total= System.currentTimeMillis() - start;
        System.out.println("Total time taken: "+total);
    }
    private static void add(String app, Static data) throws NoSuchAlgorithmException{
        if(cache.containsKey(app))
        {
            System.out.println("Adding an application which is already present is not permitted");
            return;
        }
        String previousblockhash = size>=1?blockChainList.get(size-1).getHash(): "#";
        Block node = new Block(data, previousblockhash);
        size++;
        blockChainList.add(node);
        cache.put(app, node);
        // if(validate(blockChainList))
        // {
        //     blockChainList.add(node);
        //     cache.put(app, node);
        // }
        // else
        // {
        //     System.out.println("Not valid operation");
        // }
    }
    private static boolean validate(ArrayList<Block> blockChain) {
        boolean result = true;

        Block lastBlock = null;
        for(int i = blockChain.size() -1; i >= 0; i--) {
            if(lastBlock == null) {
                lastBlock = blockChain.get(i);
            }
            else {
                Block current = blockChain.get(i);
                if(!lastBlock.getPreviousHash().equals(current.getHash())) {
                    result = false;
                    break;
                }
                lastBlock = current;
            }
        }

        return result;
    }
}