package com.kennyramadhan.qa.core.reporting;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;

import java.util.UUID;

/**
 * <h1>LogHelper</h1>
 * Thin shim over the Allure step API. Public method signatures are preserved
 * from the pre-Allure implementation so existing callers in mobile page
 * objects keep working without modification.
 *
 * <p><b>Methods:</b></p>
 * <ul>
 *   <li>{@link #resetCounter()} — reset the per-thread STEP-N counter to 1.</li>
 *   <li>{@link #step(String)} — record a numbered top-level step ("STEP N: msg").</li>
 *   <li>{@link #detail(String)} — record additional context as a sibling step.</li>
 *   <li>{@link #pass(String)} — record a step explicitly marked PASSED.</li>
 *   <li>{@link #fail(String)} — record a step explicitly marked FAILED (does not throw).</li>
 *   <li>{@link #warning(String)} — alias for {@link #detail(String)}.</li>
 * </ul>
 *
 * <p><b>Example:</b></p>
 * <pre>
 * LogHelper.resetCounter();
 * LogHelper.step("Open login page");
 * LogHelper.detail("Username entered");
 * LogHelper.pass("Login succeeded");
 * </pre>
 *
 * <p>Phase 3.4 will rename this class to AllureLogger and align method names
 * with Allure conventions; this shim keeps the migration ABI-compatible until
 * then.</p>
 */
public class LogHelper {

    /** Per-thread step counter. ThreadLocal so parallel test execution is race-free. */
    private static final ThreadLocal<Integer> stepCounter = ThreadLocal.withInitial(() -> 1);

    /**
     * Reset the per-thread step counter to 1. Call at the start of every test method
     * so step numbering restarts cleanly.
     */
    public static void resetCounter() {
        stepCounter.set(1);
    }

    /**
     * Record a numbered top-level step in the Allure report.
     *
     * @param message human-readable step description
     */
    public static void step(String message) {
        int n = stepCounter.get();
        stepCounter.set(n + 1);
        Allure.step("STEP " + n + ": " + message);
    }

    /**
     * Record additional context as a sibling step in the Allure report.
     *
     * <p><b>Note:</b> Allure does not support free-form info logs nested inside an
     * already-completed step. This shim records details as <em>sibling</em> steps
     * (flat in the timeline) rather than children of the most recent step. Phase 3.4
     * will redesign the API to use {@code Allure.step(String, ThrowableRunnable)}
     * lambdas and proper nesting.</p>
     *
     * @param message detail message
     */
    public static void detail(String message) {
        Allure.step(message);
    }

    /**
     * Record a step explicitly marked PASSED.
     *
     * @param message pass message
     */
    public static void pass(String message) {
        emitStep(message, Status.PASSED);
    }

    /**
     * Record a step explicitly marked FAILED. Does not throw; the test method
     * decides whether to fail the test (typically via TestNG Assert).
     *
     * @param message fail message
     */
    public static void fail(String message) {
        emitStep(message, Status.FAILED);
    }

    /**
     * Alias for {@link #detail(String)}.
     *
     * @param message warning message
     */
    public static void warning(String message) {
        Allure.step(message);
    }

    private static void emitStep(String message, Status status) {
        String uuid = UUID.randomUUID().toString();
        StepResult step = new StepResult().setName(message).setStatus(status);
        Allure.getLifecycle().startStep(uuid, step);
        Allure.getLifecycle().stopStep(uuid);
    }
}
