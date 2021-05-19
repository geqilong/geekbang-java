package org.geektimes.commons.hash;

public class VirtualNode extends HashNode {
    private final ServerNode serverNode;


    public VirtualNode(ServerNode serverNode) {
        super(serverNode.getNodeName(), serverNode.getServerIp(), serverNode.getPort());
        this.serverNode = serverNode;
    }

    public VirtualNode(String nodeName, String serverIp, int port) {
        super(nodeName, serverIp, port);
        this.serverNode = new ServerNode(nodeName.substring(0, 8), serverIp, port);
    }

    public ServerNode getServerNode() {
        return serverNode;
    }
}
