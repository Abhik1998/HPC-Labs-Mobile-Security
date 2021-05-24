import java.util.SortedSet;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.security.NoSuchAlgorithmException;
import java.lang.instrument.Instrumentation;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;




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
        fetch();
        File[] files = new File("malapp").listFiles();
        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());

            }
        }
        int id=0;
        for(String name: results)
        {
            ProcessBuilder fb=new ProcessBuilder();
            

            String tool="$ANDROID_SDK/build-tools/30.0.3/aapt2 d xmltree --file AndroidManifest.xml $HOME/class/BTP/malapp/"+name+"> temp/"+id+".txt";
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
                    {
                        continue;
                    }
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
                sc.close();
            }
        }
        db();
    }
    private static void add(String app, Static data) throws NoSuchAlgorithmException{
        if(cache.containsKey(app))
        {
            if(cache.get(app).data.permission.equals(data.permission))
                System.out.println("The app is the same or is already present in device");
            else{
                System.out.println(app+" is not good as permissions differ");
                System.out.println("Old permissions: "+cache.get(app).data.permission);
                System.out.println("New Permissions: "+data.permission);
            }
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
    public static boolean permissionSearch(String app, String feature){
        if(cache.containsKey(app) && cache.get(app).data.permission.contains(feature))
            return true;
        return false;
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
    public static void db()throws IOException{
        try {
            FileWriter myWriter = new FileWriter("db.txt");
            for(Map.Entry<String, Block> x: cache.entrySet()){
                myWriter.write(x.getKey()+" ");
                for(String st: x.getValue().data.permission){
                    myWriter.write(st+" ");
                }
                myWriter.write(System.lineSeparator());
            }
            myWriter.close();
            //System.out.println("Successfully wrote to the file.");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        
    }
    public static void fetch()throws Exception{
        File file=new File("db.txt");
        Scanner sc=new Scanner(file);
        while(sc.hasNextLine()){
            String data=sc.nextLine();
            String str[]=data.trim().split(" ");
            SortedSet<String> permissions=new TreeSet<>();
            for(int i=1;i<str.length;i++)
                permissions.add(str[i]);
            Static appdata=new Static();
            appdata.permission=Collections.unmodifiableSortedSet(permissions);
            add(str[0], appdata);
        }
        sc.close();
    }
    public static int sizeof(SortedSet<String> h){
        int size=0;
        for(String x: h){
            size+=x.length()*2;
        }
        return size+12;
    }
}