package org.geektimes.commons.hash;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;

public class ConsistentHashingWithVirtualNodes extends AbstractConsistentHashing {
    private static final int virtualNodeNum = 8;

    protected ConsistentHashingWithVirtualNodes(int nodeNum) {
        super(nodeNum * virtualNodeNum);
    }

    @Override
    protected HashNode doCreateNode(int seq) {
        int nodeIndex = seq / virtualNodeNum;
        int vitualNodeIndex = (seq - nodeIndex * virtualNodeNum);
        String serverIp = getIpPrefix() + (nodeIndex / 255) + "." + (nodeIndex % 255) + "$$VN" + vitualNodeIndex;
        return new VirtualNode("ServerNode" + nodeIndex + "$$VN" + vitualNodeIndex, serverIp, getPort());
    }

    @Override
    public String getIpPrefix() {
        return "127.0.";
    }

    @Override
    public int getPort() {
        return 2021;
    }

    @Override
    protected String getServer(String key) {
        String serverIp = super.getServer(key);
        if (StringUtils.isNotBlank(serverIp)) {
            return serverIp.substring(0, serverIp.indexOf("$$"));
        }
        return null;
    }

    public static void main(String[] args) {
        ConsistentHashingWithVirtualNodes chvn = new ConsistentHashingWithVirtualNodes(8);
        Map<String, Integer> keyNumMap = new HashMap<>();
        //初始化
        SortedMap<Integer, HashNode> nodeMap = chvn.getNodeMap();
        String serverIp;
        for (Map.Entry<Integer, HashNode> entry : nodeMap.entrySet()) {
            serverIp = entry.getValue().getServerIp().substring(0, entry.getValue().getServerIp().indexOf("$$"));
            keyNumMap.put(serverIp, 0);
        }
        String server;
        int num;
        for (int i = 0; i < 10000000; i++) {
            server = chvn.getServer(UUID.randomUUID().toString());
            num = keyNumMap.get(server);
            keyNumMap.put(server, num + 1);
        }
        System.out.println("节点Hash分布情况:");
        System.out.println(keyNumMap);
    }
}
