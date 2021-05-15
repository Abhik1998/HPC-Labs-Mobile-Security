import java.util.Arrays;

public class Block {
    private int previousHash;
    private String data;
    private int hash;

    public Block(String data, int previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.hash  = Arrays.hashCode(new Integer[]{data.hashCode(), previousHash});
    }
}