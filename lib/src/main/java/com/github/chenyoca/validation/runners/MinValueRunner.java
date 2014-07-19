package com.github.chenyoca.validation.runners;

import com.github.chenyoca.validation.Type;

/**
 * User: chenyoca@gmail.com
 * Date: 2014-06-26
 * Value in max
 */
class MinValueRunner extends ValueTestRunner{

    public MinValueRunner(Type testType, String message){
        super(testType,message);
    }

    @Override
    protected boolean withExtraLong(double inputValue) {
        return inputValue >= extraLong[0];
    }

    @Override
    protected boolean withExtraFloat(double inputValue) {
        return inputValue >= extraFloat[0];
    }
}
