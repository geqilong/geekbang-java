package org.geektimes.commons.hash;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class AbstractConsistentHashing {
    private final int nodeNum;
    private final SortedMap<Integer, HashNode> nodeMap;


    protected AbstractConsistentHashing(int nodeNum) {
        if (nodeNum <= 0)
            throw new RuntimeException("nodeNum must be greater than 0");
        this.nodeNum = nodeNum;
        this.nodeMap = new TreeMap<>();
        initNodeMap();
    }

    private void initNodeMap() {
        int hash;
        int nodeNum = getNodeNum();
        for (int i = 0; i < nodeNum; i++) {
            //serverIp = getIpPrefix() + (i / 255) + "." + (i % 255);
            HashNode hashNode = doCreateNode(i);
            hash = doGetHash(hashNode);
            nodeMap.put(hash, hashNode);
        }
        System.out.println("初始化HashNode完成:");
        for (Map.Entry<Integer, HashNode> entry : nodeMap.entrySet()) {
            System.out.println(entry.getValue() + ": " + entry.getKey());
        }
    }

    protected abstract HashNode doCreateNode(int seq);

    public int getNodeNum() {
        return nodeNum;
    }

    public SortedMap<Integer, HashNode> getNodeMap() {
        return nodeMap;
    }

    protected int doGetHash(HashNode hashNode) {
        String str = hashNode.getServerIp() + ":" + hashNode.getPort();
        return doGetHash(str);
    }

    /**
     * 使用FNV1_32_HASH算法计算服务器的Hash值
     *
     * @param str 原始值
     * @return 哈希值
     */
    protected int doGetHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

    //得到应当路由到的结点
    protected String getServer(String key) {
        //得到该key的hash值
        int hash = doGetHash(key);
        return doGetServer(hash).getServerIp();
    }

    protected HashNode doGetServer(int hash){
        //得到大于该Hash值的所有Map
        SortedMap<Integer, HashNode> nodeMap = getNodeMap();
        SortedMap<Integer, HashNode> subMap = nodeMap.tailMap(hash);
        if (subMap.isEmpty()) {
            //如果没有比该key的hash值大的，则从第一个node开始-----为啥不取最后一个呢？不是距离最近的吗？
            Integer i = nodeMap.firstKey();
            //返回对应的服务器
            return nodeMap.get(i);
        } else {
            //第一个Key就是顺时针过去离node最近的那个结点
            Integer i = subMap.firstKey();
            //返回对应的服务器
            return subMap.get(i);
        }
    }

    public abstract String getIpPrefix();

    public abstract int getPort();
}
