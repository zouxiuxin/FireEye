package com.github.chenyoca.validation.runners;

import com.github.chenyoca.validation.Type;
import com.github.chenyoca.validation.supports.TestRunner;

/**
 * User: chenyoca@gmail.com
 * Date: 2014-06-26
 * Host
 */
class HostRunner extends TestRunner {

    static final String HOST_REGEX = "^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,65}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$";

    public HostRunner(Type testType, String message){
        super(testType, message);
    }

    @Override
    public boolean test(String inputValue) {
        return isMatched(IPv4Runner.IPV4_REGEX, inputValue) || isMatched(HOST_REGEX, inputValue);
    }

}
