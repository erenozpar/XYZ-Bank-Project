package com.globalsqa.bank.utils;
import com.globalsqa.bank.utils.RetryAnalyzer;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.IAnnotation;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AnnotationTransformer implements IAnnotationTransformer {
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod){
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
