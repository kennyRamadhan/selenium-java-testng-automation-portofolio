package com.kennyramadhan.qa.core.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Phase 7 — runtime APK provisioning. Downloads a pinned-version APK from a
 * public release URL when the destination file is missing. Idempotent: a
 * subsequent call with the same destination returns immediately without
 * touching the network.
 *
 * <p>
 * Download is written to {@code <dest>.tmp} first, then atomically renamed via
 * {@link Files#move} with {@link StandardCopyOption#ATOMIC_MOVE} so that an
 * interrupted process never leaves a half-written file at the destination path.
 * </p>
 *
 * <p>
 * Retries up to 3 times on {@link IOException} with exponential backoff (1s,
 * 2s, 4s) to ride out transient network failures while keeping CI run time
 * bounded. Final failure throws {@link IllegalStateException} with a
 * remediation hint.
 * </p>
 */
public final class ApkDownloader {

	private static final Logger log = LoggerFactory.getLogger(ApkDownloader.class);

	private static final int MAX_RETRIES = 3;
	private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(30);
	private static final Duration READ_TIMEOUT = Duration.ofMinutes(5);

	private ApkDownloader() {
	}

	/**
	 * Ensure the APK file exists at {@code dest}. If the file is already present
	 * and non-empty, returns immediately. Otherwise downloads from {@code url} to a
	 * sibling {@code .tmp} file and atomically renames it into place.
	 *
	 * @param url
	 *            public release URL serving the APK binary
	 * @param dest
	 *            destination path (parent directories created on demand)
	 * @return the destination path, for chaining
	 * @throws IllegalStateException
	 *             when all retries are exhausted
	 */
	public static Path ensureApk(String url, Path dest) {
		if (Files.exists(dest)) {
			try {
				long size = Files.size(dest);
				if (size > 0) {
					log.info("[ApkDownloader] APK already present at {} ({} bytes) — skipping download", dest, size);
					return dest;
				}
			} catch (IOException e) {
				log.warn("[ApkDownloader] Could not stat existing file {}: {} — re-downloading", dest, e.getMessage());
			}
		}

		Path parent = dest.toAbsolutePath().getParent();
		if (parent != null) {
			try {
				Files.createDirectories(parent);
			} catch (IOException e) {
				throw new IllegalStateException("Failed to create APK destination directory " + parent, e);
			}
		}

		Path tmp = dest.resolveSibling(dest.getFileName() + ".tmp");
		HttpClient client = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT)
				.followRedirects(HttpClient.Redirect.NORMAL).build();
		HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(READ_TIMEOUT).GET().build();

		IOException lastFailure = null;
		for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
			try {
				log.info("[ApkDownloader] Downloading APK from {} (attempt {}/{})", url, attempt, MAX_RETRIES);
				HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(tmp));
				if (response.statusCode() != 200) {
					throw new IOException("HTTP " + response.statusCode() + " from " + url);
				}
				long size = Files.size(tmp);
				if (size <= 0) {
					throw new IOException("Downloaded file is empty");
				}
				Files.move(tmp, dest, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
				log.info("[ApkDownloader] APK downloaded to {} ({} bytes)", dest, size);
				return dest;
			} catch (IOException e) {
				lastFailure = e;
				log.warn("[ApkDownloader] Attempt {} failed: {}", attempt, e.getMessage());
				cleanupTmp(tmp);
				if (attempt < MAX_RETRIES) {
					sleepBackoff(attempt);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				cleanupTmp(tmp);
				throw new IllegalStateException("APK download interrupted", e);
			}
		}

		throw new IllegalStateException("Failed to download APK after " + MAX_RETRIES + " attempts from " + url
				+ ". Check network connectivity or download manually to " + dest, lastFailure);
	}

	private static void cleanupTmp(Path tmp) {
		try {
			Files.deleteIfExists(tmp);
		} catch (IOException ignored) {
			// Best-effort cleanup; next attempt will overwrite.
		}
	}

	private static void sleepBackoff(int attempt) {
		long delayMs = 1000L * (1L << (attempt - 1));
		try {
			Thread.sleep(delayMs);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}
}
