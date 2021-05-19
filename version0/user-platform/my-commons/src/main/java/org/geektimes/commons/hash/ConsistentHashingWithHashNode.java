package org.geektimes.commons.hash;

import java.util.*;

public class ConsistentHashingWithHashNode extends AbstractConsistentHashing {
    public ConsistentHashingWithHashNode(int nodeNum) {
        super(nodeNum);
    }

    @Override
    protected HashNode doCreateNode(int seq) {
        String serverIp = getIpPrefix() + (seq / 255) + "." + (seq % 255);
        return new HashNode("HashNode" + seq, serverIp, getPort());
    }

    @Override
    public String getIpPrefix() {
        return "192.168.";
    }

    @Override
    public int getPort() {
        return 1949;
    }

    public static void main(String[] args) {
        ConsistentHashingWithHashNode chhn = new ConsistentHashingWithHashNode(10);
        Map<String, Integer> keyNumMap = new HashMap<>();
        //初始化
        SortedMap<Integer, HashNode> nodeMap = chhn.getNodeMap();
        for (Map.Entry<Integer, HashNode> entry : nodeMap.entrySet()) {
            keyNumMap.put(entry.getValue().getServerIp(), 0);
        }
        String server;
        int num;
        for (int i = 0; i < 10000000; i++) {
            server = chhn.getServer(UUID.randomUUID().toString());
            num = keyNumMap.get(server);
            keyNumMap.put(server, num + 1);
        }
        System.out.println("节点Hash分布情况:");
        System.out.println(keyNumMap);
    }
}
