import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.security.NoSuchAlgorithmException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;




public class BlockChain {
    static class Static{
        HashSet<String> opcode, system_call, api;
        SortedSet<String> permission;
        Static(){
            this.opcode = new HashSet<>();
            this.system_call = new HashSet<>();
            this.api = new HashSet<>();
            this.permission = new TreeSet<>();
        }
        public String getString(){
            StringBuilder sb=new StringBuilder();
            for(String x: opcode)
                sb.append(x);
            for(String x: system_call)
                sb.append(x);
            for(String x: api)
                sb.append(x);
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
    static HashMap<String, Block> cache;
    private static int size;
    static List<Block> blockChainList;
    
    public static void main(String[] args) throws Exception {
        blockChainList =  new ArrayList<>();
        cache = new HashMap<>();
        size = 0;
        extract("test/apktest1-analysis.json");
        extract("test/Lucky_Patcher_v935_apkmodyio-analysis.json");
        //testUpdate();
        testAdd();
        //System.out.println(blockChainList);
        // extract("test/Lucky_Patcher_v935_apkmodyio-analysis.json");
        // extract("test/Meme_Generator_PRO_v45986_apkmodyio-analysis.json");
        // extract("test/Telegram_v742_MOD_apkmodyio-analysis.json");
        // extract("test/Truecaller_v11476_MOD_apkmodyio-analysis.json");

        // Testing *******************************
        // Static vedantu_data = new Static();
        // vedantu_data.permission.add("location");
        // vedantu_data.api.add("system.api.android/call");

        // add("vedantu", vedantu_data);
        // add("dream11", new Static());
        // for(Block b: blockChainList){
        //     System.out.println(b.hash+" "+b.previousHash);
        // }
       // System.out.println(cache); // validate 
        //System.out.println(permissionsearch("vedantu", "location"));
       //System.out.println(cache.get("\"apktest1.apk\"").data.opcode);
        //**************************************** */
    }
    private static void extract(String path)throws NoSuchAlgorithmException { //extract data
        try{
            File file=new File(path);
            Scanner sc=new Scanner(file);
            String name="";
            if(sc.hasNextLine()){
                String data = sc.nextLine();
                data = sc.nextLine().trim();
                if(data.indexOf("Pre_static_analysis")>=0){
                    name = sc.nextLine().trim().split(" ")[1];
                    if(name.indexOf(",")>=0)
                        name = name.substring(0, name.length()-1);
                }
            }
            String data = sc.nextLine().trim();
            while(data.indexOf("Permissions")<0){
                data = sc.nextLine().trim();
            }
            Static appdata = new Static();
            data = sc.nextLine().trim();
            if(data.indexOf(",")>=0)
                    data = data.substring(0, data.length()-1);
            while(!data.equals("]")){
                appdata.permission.add(data);
                data = sc.nextLine().trim();
                if(data.indexOf(",")>=0)
                    data = data.substring(0, data.length()-1);
            }
            data = sc.nextLine().trim();
            if(data.indexOf(",")>=0)
                    data = data.substring(0, data.length()-1);
                data = sc.nextLine().trim();
                if(data.indexOf(",")>=0)
                    data = data.substring(0, data.length()-1);
                while(!data.equals("}"))
                {
                    appdata.opcode.add(data.split(":")[0]);
                   // System.out.println(data.split(":")[0]);
                    data = sc.nextLine().trim();
                    if(data.indexOf(",")>=0)
                        data = data.substring(0, data.length()-1);
                }
                while(data.indexOf("API calls")<0){
                    data = sc.nextLine().trim();
                    if(data.indexOf(",")>=0)
                        data = data.substring(0, data.length()-1);
                }
                data = sc.nextLine().trim();
                if(data.indexOf(",")>=0)
                    data = data.substring(0, data.length()-1);
                while(!data.equals("}")){
                    appdata.api.add(data.split(":")[0]);
                    data = sc.nextLine().trim();
                    if(data.indexOf(",")>=0)
                        data = data.substring(0, data.length()-1);
                }
                while(data.indexOf("System commands")<0)
                {
                    data = sc.nextLine().trim();
                }
                data = sc.nextLine().trim();
                if(data.indexOf(",")>=0)
                    data = data.substring(0, data.length()-1);
                while(!data.equals("}")){
                    appdata.system_call.add(data.split(":")[0]);
                    data = sc.nextLine().trim();
                    if(data.indexOf(",")>=0)
                        data = data.substring(0, data.length()-1);
                }
                appdata.permission=Collections.unmodifiableSortedSet(appdata.permission);
                name=name.substring(1, name.length()-1);
                add(name, appdata);
        }
        catch(FileNotFoundException e){
            System.out.println("An error occured while import");
            e.printStackTrace();
        }
    }

    private static void add(String app, Static data) throws NoSuchAlgorithmException{
        if(cache.containsKey(app))
        {
            System.out.println("Adding an application which is already present is not permitted");
            //System.out.println(cache.get(app).data);
            return;
        }
        String previousblockhash = size>=1?blockChainList.get(size-1).getHash(): "#";
        final Block node = new Block(data, previousblockhash);
        size++;
        if(validate(blockChainList))
        {
            blockChainList.add(node);
            cache.put(app, node);
        }
        else
        {
            System.out.println("Not valid operation");
        }
    }

    // Search for the static features
    public static boolean apisearch(String app, String feature){
        if(cache.containsKey("\""+app+"\"") && cache.get("\""+app+"\"").data.api.contains("\""+feature+"\""))
            return true;
        return false;
    }
    public static boolean permissionsearch(String app, String feature){
        if(cache.containsKey("\""+app+"\"") && cache.get("\""+app+"\"").data.permission.contains("\""+feature+"\""))
            return true;
        return false;
    }
    public static boolean opcodesearch(String app, String feature){
        if(cache.containsKey("\""+app+"\"") && cache.get("\""+app+"\"").data.opcode.contains("\""+feature+"\""))
            return true;
        return false;
    }
    public static boolean system_callsearch(String app, String feature){
        if(cache.containsKey("\""+app+"\"") && cache.get("\""+app+"\"").data.system_call.contains("\""+feature+"\""))
            return true;
        return false;
    }

    private static boolean validate(List<Block> blockChain) {
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
    public static void testUpdate(){
        System.out.println(blockChainList);
        try{
            blockChainList.get(0).data.permission.add("Allo Leelo Meri Jaan");
        }
        catch(UnsupportedOperationException e){
            System.out.println("Hey!! this operation is not permitted");
        }
        System.out.println(blockChainList.get(0).data.permission);
    }
    public static void testAdd()throws NoSuchAlgorithmException{
        System.out.println(cache);
        // try{
            add("apktest1-analysis", new Static());
            add("apktest1.apk", new Static());
       // }
        //catch(NoSuchAlgorithmException e){

        //}
        System.out.println(cache);
        
    }
}