package com.globalsqa.bank.utils;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.IAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AnnotationTransformer implements IAnnotationTransformer {
    @Override
    public void transform(IAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod){
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
