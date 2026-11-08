package com.almotawaj.wallet.identity.common.model;

import org.springframework.core.io.Resource;

public record DocumentContent(Resource resource, String contentType) {
}
