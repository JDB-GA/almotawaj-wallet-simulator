package com.almotawaj.wallet.wallet.model;

import org.springframework.core.io.Resource;

public record DocumentContent(Resource resource, String contentType) {
}
