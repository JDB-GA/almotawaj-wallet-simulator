package com.almotawaj.wallet.chatbot.service;

import java.util.List;

public interface ChatStreamListener {
    void onDelta(String text);

    void onDone(List<String> sources);
}
