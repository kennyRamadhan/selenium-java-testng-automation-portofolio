package com.kennyramadhan.qa.core.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

/**
 * Auto-applies {@link MobileRetryAnalyzer} to every {@code @Test} method on
 * test classes that register this listener via {@code @Listeners}.
 *
 * <p>
 * Avoids per-method {@code @Test(retryAnalyzer = MobileRetryAnalyzer.class)}
 * boilerplate — one {@code @Listeners(MobileRetryListener.class)} on each
 * mobile test class enables retry across all methods.
 */
public class MobileRetryListener implements IAnnotationTransformer {

	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		annotation.setRetryAnalyzer(MobileRetryAnalyzer.class);
	}
}
