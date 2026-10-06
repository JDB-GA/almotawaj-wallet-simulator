package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.GatewayMessages;
import com.almotawaj.wallet.config.constants.GatewayConstants;
import com.almotawaj.wallet.exception.BusinessRuleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;

@Component
public class WebhookUrlPolicy {
    private static final int CARRIER_NAT_FIRST_OCTET = 100;
    private static final int CARRIER_NAT_SECOND_OCTET_MASK = 0xC0;
    private static final int CARRIER_NAT_SECOND_OCTET = 0x40;
    private static final int UNIQUE_LOCAL_MASK = 0xFE;
    private static final int UNIQUE_LOCAL_PREFIX = 0xFC;

    private final boolean allowPrivateHosts;

    public WebhookUrlPolicy(@Value(GatewayConstants.WEBHOOK_ALLOW_PRIVATE_HOSTS_PROPERTY) boolean allowPrivateHosts) {
        this.allowPrivateHosts = allowPrivateHosts;
    }

    public URI requireAllowed(String url) {
        URI uri = parse(url);
        if (allowPrivateHosts) {
            return uri;
        }
        if (!GatewayConstants.WEBHOOK_SECURE_SCHEME.equalsIgnoreCase(uri.getScheme()) || !resolvesToPublicAddresses(uri.getHost())) {
            throw notAllowed();
        }
        return uri;
    }

    private static URI parse(String url) {
        try {
            URI uri = URI.create(url);
            if (uri.getHost() == null || uri.getUserInfo() != null) {
                throw notAllowed();
            }
            return uri;
        } catch (IllegalArgumentException e) {
            throw notAllowed();
        }
    }

    private static boolean resolvesToPublicAddresses(String host) {
        try {
            return Arrays.stream(InetAddress.getAllByName(host)).allMatch(WebhookUrlPolicy::isPublic);
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private static boolean isPublic(InetAddress address) {
        if (address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress()
                || address.isSiteLocalAddress() || address.isMulticastAddress()) {
            return false;
        }
        byte[] bytes = address.getAddress();
        if (address instanceof Inet4Address) {
            boolean isCarrierNat = (bytes[0] & 0xFF) == CARRIER_NAT_FIRST_OCTET
                    && (bytes[1] & CARRIER_NAT_SECOND_OCTET_MASK) == CARRIER_NAT_SECOND_OCTET;
            return !isCarrierNat;
        }
        return !(address instanceof Inet6Address) || (bytes[0] & UNIQUE_LOCAL_MASK) != UNIQUE_LOCAL_PREFIX;
    }

    private static BusinessRuleException notAllowed() {
        return new BusinessRuleException(GatewayMessages.WEBHOOK_URL_NOT_ALLOWED, ErrorCodes.WEBHOOK_URL_NOT_ALLOWED);
    }
}
