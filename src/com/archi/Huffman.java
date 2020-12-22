/*
 * Inspiration
 * https://algs4.cs.princeton.edu/55compression/Huffman.java.html
 * Probably not useful in the project
 */
package com.archi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Huffman {
    // Do not instantiate.
    private Huffman() {
    }

    // Huffman trie node
    private static class Node implements Comparable<Node> {
        private final char ch;
        private final int freq;
        private final Node left, right;

        Node(char ch, int freq, Node left, Node right) {
            this.ch = ch;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        // is the node a leaf node?
        private boolean isLeaf() {
            assert ((left == null) && (right == null)) || ((left != null) && (right != null));
            return left == null;
        }

        // compare, based on frequency
        public int compareTo(Node that) {
            return this.freq - that.freq;
        }
    }

    public static class BitList {
        private final List<Byte> buf;
        private int wCur, rCur;

        public BitList() {
            this.buf = new ArrayList<>();
            this.wCur = 0;
            this.rCur = 0;
        }

        public boolean canRead() {
            return rCur < wCur;
        }
        public int length() {
            return wCur;
        }

        public void write(boolean b) {
            byte x = (byte) (b ? 1 : 0);
            if (wCur % 8 == 0) {
                buf.add(x);
            } else {
                byte last = buf.get(buf.size() - 1);
                last = (byte) (last | (x << wCur % 8));
                buf.set(buf.size() - 1, last);
            }
            wCur++;
        }
        public void write(char x, boolean latin) {
            int n = latin ? 8 : 16;
            for (int i = 0; i < n; i++) {
                write((x >> i & 1) == 1);
            }
        }
        public void write(int x) {
            for (int i = 0; i < 32; i++) {
                write((x >> i & 1) == 1);
            }
        }
        public void write(BitList bitList) {
            if(wCur%8 == 0){
                buf.addAll(bitList.buf);
                wCur += bitList.length();
            }else{
                int tmpCur = bitList.rCur;
                bitList.rCur = 0;
                for (int i = 0; i < bitList.length(); i++) {
                    write(bitList.read());
                }
           }
        }

        public boolean read() {
            boolean b = (buf.get(rCur / 8) >> (rCur % 8) & 1) == 1;
            rCur++;
            return b;
        }

        public char read(boolean latin) {
            char c = 0;
            int n = latin ? 8 : 16;
            for (int i = 0; i < n; i++) {
                c |= (read() ? 1 : 0) << i;
            }
            return c;
        }
        public int readInt() {
            char c = 0;
            for (int i = 0; i < 32; i++) {
                c |= (read() ? 1 : 0) << i;
            }
            return c;
        }

        public Byte[] getByteArray() {
            return buf.toArray(new Byte[0]);
        }

        public String toString() {
            byte[] data = new byte[buf.size()];
            for (int i = 0; i < buf.size(); i++) {
                data[i] = buf.get(i);
            }
            return new String(data, StandardCharsets.UTF_16);
        }

    }

    // build the Huffman trie given frequencies
    private static Node buildTrie(HashMap<Character, Integer> freq) {

        // initialze priority queue with singleton trees
        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        freq.forEach((k, v) -> pq.add(new Node(k, v, null, null)));

        // merge two smallest trees
        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node('\0', left.freq + right.freq, left, right);
            pq.add(parent);
        }
        return pq.poll();
    }

    // make a lookup table from symbols and their encodings
    private static void buildCode(HashMap<Character, String> table, Node x, String s) {
        if (!x.isLeaf()) {
            buildCode(table, x.left, s + '0');
            buildCode(table, x.right, s + '1');
        } else {
            table.put(x.ch, s);
        }
    }
    private static HashMap<Character, String> buildCode(Node trie) {
        HashMap<Character, String> table = new HashMap<>();
        buildCode(table, trie, "");
        return table;
    }

    // write bitstring-encoded trie to standard output
    private static void writeTrie(BitList bitList, Node x) {
        if (x.isLeaf()) {
            bitList.write(true);
            boolean isLatin = StandardCharsets.US_ASCII.newEncoder().canEncode(x.ch);
            bitList.write(isLatin);
            bitList.write(x.ch, isLatin);
            return;
        }
        bitList.write(false);
        writeTrie(bitList, x.left);
        writeTrie(bitList, x.right);
    }

    // read trie
    private static Node readTrie(BitList bitList) {
        boolean isLeaf = bitList.read();
        if (isLeaf) {
            boolean isLatin = bitList.read();
            return new Node(bitList.read(isLatin), -1, null, null);
        } else {
            return new Node('\0', -1, readTrie(bitList), readTrie(bitList));
        }
    }


    private static HashMap<Character, Integer> frequencies(String s) {
        char[] input = s.toCharArray();

        // tabulate frequency counts
        HashMap<Character, Integer> freq = new HashMap<>();
        for (Character c : input) {
            if (freq.containsKey(c)) {
                freq.put(c, freq.get(c) + 1);
            } else {
                freq.put(c, 1);
            }
        }
        return freq;
    }
    private static BitList compress(String s, HashMap<Character, String> table) {
        BitList bitList = new BitList();
        // use Huffman code to encode input
        char[] input = s.toCharArray();
        for (Character c : input) {
            String code = table.get(c);
            for (int i = 0; i < code.length(); i++) {
                bitList.write(code.charAt(i) == '1');
            }
        }
        return bitList;
    }
    public static BitList compress(String s, BitList tableEnc) {
        return compress(s, buildCode( readTrie(tableEnc) ));
    }

    public static String uncompress(BitList bitList) {
        // read in Huffman trie from input stream
        Node root = readTrie(bitList);
        StringBuilder s = new StringBuilder();
        // decode using the Huffman trie
        while (bitList.canRead()) {
            Node x = root;
            while (!x.isLeaf() && bitList.canRead()) {
                if (bitList.read()) x = x.right;
                else x = x.left;
            }
            s.append(x.ch);
        }
        return s.toString();
    }



    /**
     * Test de compression
     */
    public static void main(String[] args) {
        byte b = (byte) (1 << 7);
        Character a = 'a', t = '言';


        String toComp = "言Un chasseur sachant chassé sans son chien est un bon " +
                "chasseur. Les chaussettes de l'archiduchesse sont-elles sèches ? 言 Archi-sèche !";
//        toComp += toComp;
//        toComp += toComp;
//        toComp += toComp;
        StringBuilder stringBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get("dbdata.txt"))) {
            stream.forEach(stringBuilder::append);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String tot = stringBuilder.toString();

        long start = System.currentTimeMillis();

        Node trie = buildTrie(frequencies(tot));
        BitList bitList = new BitList();
        writeTrie(bitList, trie);

        System.out.println(System.currentTimeMillis() - start+" ms");
        System.out.println(bitList.toString());
        HashMap<Character, String> table = buildCode(trie);

        start = System.currentTimeMillis();
        BitList compressed = compress(tot, table);
        System.out.println(System.currentTimeMillis() - start+" ms");
        System.out.println(tot.length()+" "+ compressed.getByteArray().length);


//        BitList c = compress(toComp);
//        System.out.println(toComp.length() + " " + c.getByteArray().length);
//        System.out.println(uncompress(c));
    }

}