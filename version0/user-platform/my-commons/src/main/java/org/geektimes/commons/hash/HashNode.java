package org.geektimes.commons.hash;

public class HashNode {
    private final String nodeName;
    private final String serverIp;
    private final int port;

    public HashNode(String nodeName, String serverId, int port) {
        this.nodeName = nodeName;
        this.serverIp = serverId;
        this.port = port;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getPort() {
        return port;
    }

    public String getNodeName() {
        return nodeName;
    }

    @Override
    public String toString() {
        return "HashNode{" +
                "nodeName='" + nodeName + '\'' +
                ", serverIp='" + serverIp + '\'' +
                ", port=" + port +
                '}';
    }
}
