package com.kennyramadhan.qa.core.reporting;

import java.util.UUID;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;

/**
 * <h1>LogHelper</h1> Thin shim over the Allure step API. Public method
 * signatures are preserved from the pre-Allure implementation so existing
 * callers in mobile page objects keep working without modification.
 *
 * <p>
 * <b>Lifecycle model (Phase 6 c2):</b> {@link #step(String)} now opens an
 * ACTIVE Allure step via {@code getLifecycle().startStep} and keeps it open
 * until the next {@code step()} call (which closes the previous one and opens
 * the next) or until {@link #flushOpenStep()} is invoked by the
 * {@link TestListeners} hooks at test end. While a step is active, any
 * subsequent {@link #detail(String)}, {@link #pass(String)},
 * {@link #fail(String)}, {@link #warning(String)} calls — and any
 * {@code Allure.addAttachment} (e.g. per-action screenshots from
 * {@code BaseMobilePage.captureScreenshot}) — nest as children of that step
 * instead of attaching to the test root.
 * </p>
 *
 * <p>
 * <b>Methods:</b>
 * </p>
 * <ul>
 * <li>{@link #resetCounter()} — flush any open step then reset the per-thread
 * STEP-N counter to 1. Called from {@code TestListeners.onTestStart}.</li>
 * <li>{@link #step(String)} — close previous active step (if any), then open a
 * new numbered top-level step ("STEP N: msg") and keep it active.</li>
 * <li>{@link #detail(String)} — record context as a child of the active
 * step.</li>
 * <li>{@link #pass(String)} — record an explicit PASSED child step.</li>
 * <li>{@link #fail(String)} — record an explicit FAILED child step (does not
 * throw).</li>
 * <li>{@link #warning(String)} — alias for {@link #detail(String)}.</li>
 * <li>{@link #flushOpenStep()} — close the currently open step if any. Called
 * from {@code TestListeners.onTestSuccess/Failure/Skipped}.</li>
 * </ul>
 *
 * <p>
 * <b>Thread safety:</b> step counter and active-step UUID are tracked via
 * {@code ThreadLocal}, so parallel TestNG execution is naturally isolated per
 * thread.
 * </p>
 */
public class LogHelper {

	/**
	 * Per-thread step counter. ThreadLocal so parallel test execution is race-free.
	 */
	private static final ThreadLocal<Integer> stepCounter = ThreadLocal.withInitial(() -> 1);

	/** Per-thread UUID of the currently open Allure step (null when none). */
	private static final ThreadLocal<String> activeStepUuid = new ThreadLocal<>();

	/**
	 * Flush any open step then reset the per-thread step counter to 1. Call at the
	 * start of every test method so step numbering restarts cleanly and any leaked
	 * step from a prior test is closed defensively.
	 */
	public static void resetCounter() {
		flushOpenStep();
		stepCounter.set(1);
	}

	/**
	 * Open a new numbered, ACTIVE top-level step in the Allure report. Closes the
	 * previously active step (if any) first so step() calls form a flat top-level
	 * sequence. The opened step remains active — subsequent
	 * detail/pass/fail/warning calls and any {@code Allure.addAttachment}
	 * invocations nest under it until the next {@code step()} call or
	 * {@link #flushOpenStep()}.
	 *
	 * @param message
	 *            human-readable step description
	 */
	public static void step(String message) {
		flushOpenStep();
		int n = stepCounter.get();
		stepCounter.set(n + 1);
		String uuid = UUID.randomUUID().toString();
		StepResult sr = new StepResult().setName("STEP " + n + ": " + message);
		try {
			Allure.getLifecycle().startStep(uuid, sr);
			activeStepUuid.set(uuid);
		} catch (Exception ignored) {
			// Allure lifecycle inaccessible — fail silently; tests must not break.
		}
	}

	/**
	 * Record additional context as a child of the currently active step. If no step
	 * is active, the entry appears at the test root.
	 *
	 * @param message
	 *            detail message
	 */
	public static void detail(String message) {
		Allure.step(message);
	}

	/**
	 * Record a step explicitly marked PASSED. Nests under the active step when one
	 * is open.
	 *
	 * @param message
	 *            pass message
	 */
	public static void pass(String message) {
		emitStep(message, Status.PASSED);
	}

	/**
	 * Record a step explicitly marked FAILED. Does not throw; the test method
	 * decides whether to fail the test (typically via TestNG Assert). Nests under
	 * the active step when one is open.
	 *
	 * @param message
	 *            fail message
	 */
	public static void fail(String message) {
		emitStep(message, Status.FAILED);
	}

	/**
	 * Alias for {@link #detail(String)}.
	 *
	 * @param message
	 *            warning message
	 */
	public static void warning(String message) {
		Allure.step(message);
	}

	/**
	 * Close the currently open active step if any. Idempotent and safe to call when
	 * no step is open. Invoked from {@code TestListeners} success/failure/ skipped
	 * hooks so unclosed steps never leak across tests.
	 */
	public static void flushOpenStep() {
		String uuid = activeStepUuid.get();
		if (uuid != null) {
			try {
				Allure.getLifecycle().stopStep(uuid);
			} catch (Exception ignored) {
				// Lifecycle may already be torn down; swallow.
			}
			activeStepUuid.remove();
		}
	}

	private static void emitStep(String message, Status status) {
		String uuid = UUID.randomUUID().toString();
		StepResult step = new StepResult().setName(message).setStatus(status);
		Allure.getLifecycle().startStep(uuid, step);
		Allure.getLifecycle().stopStep(uuid);
	}
}
