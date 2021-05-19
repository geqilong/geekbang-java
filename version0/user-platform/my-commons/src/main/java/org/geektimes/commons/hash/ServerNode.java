package org.geektimes.commons.hash;

import java.util.List;

public class ServerNode extends HashNode{
    private List<VirtualNode> virtualNodes;

    public ServerNode(String nodeName,String serverIp, int port) {
        super(nodeName, serverIp, port);
    }

    public List<VirtualNode> getVirtualNodes() {
        return virtualNodes;
    }

    public void setVirtualNodes(List<VirtualNode> virtualNodes) {
        this.virtualNodes = virtualNodes;
    }
}
