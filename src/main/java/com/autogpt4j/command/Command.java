package com.autogpt4j.command;

import java.util.Map;

public interface Command {

    String getName();

    String getDescription();

    // String getMethod();
    //
    // String getSignature();
    //
    // boolean isEnabled();
    //
    // String getDisabledReason();

    String execute(Map<String, Object> params);

}
