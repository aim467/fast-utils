package org.fast.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class IpUtils {

    /**
     * 从多个网卡中获取正确的IP地址
     * @return 正确的IP地址，例如 /192.168.1.100
     */
    public static InetAddress getLocalHostExactAddress() {
        InetAddress candidateAddress = null;

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                    InetAddress inetAddress = inetAddresses.nextElement();

                    if (!inetAddress.isLoopbackAddress()) {
                        if (inetAddress.isSiteLocalAddress()) {
                            // 返回局域网地址
                            return inetAddress;
                        }
                        // 记录非局域网但非回环的地址
                        if (candidateAddress == null) {
                            candidateAddress = inetAddress;
                        }
                    }
                }
            }
            // 返回找到的候选地址，或者本地主机地址
            return (candidateAddress != null) ? candidateAddress : InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
