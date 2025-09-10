package Extent.Listeners;

import java.util.HashMap;
import java.util.Map;

import com.aventstack.extentreports.ExtentTest;

/**
 * <h1>ExtentNode</h1>
 * Kelas ini berfungsi sebagai utilitas untuk membuat, menyimpan, dan mengelola instance
 * {@link ExtentTest} (parent test dan node test) yang digunakan dalam pembuatan report Extent.
 *
 * <p><b>Usage:</b></p>
 * <pre>
 * ExtentTest test = ExtentNode.createTest("Nama Test");
 * ExtentNode.createNode("Step 1").info("Step berhasil dijalankan");
 * ExtentNode.addScreenshot("path/to/screenshot.png");
 * </pre>
 *
 * <p>Kelas ini memanfaatkan {@code ThreadLocal} berbasis {@code Thread ID} untuk memastikan 
 * bahwa report berjalan dengan aman secara paralel.</p>
 *
 * @author Kenny Ramadhan
 * @version 1.0
 */

public class ExtentNode {
	

    /** Menyimpan parent test berdasarkan Thread ID */
    private static Map<Long, ExtentTest> parentTestMap = new HashMap<>();
    
    /** Menyimpan node test berdasarkan Thread ID */
    private static Map<Long, ExtentTest> nodeTestMap = new HashMap<>();
    
    
    /**
     * Membuat parent test baru dan menyimpannya berdasarkan Thread ID.
     *
     * @param testName Nama dari test yang akan dibuat.
     * @return {@link ExtentTest} instance yang baru dibuat.
     */
    public static synchronized ExtentTest createTest(String testName) {
        ExtentTest test = ExtentReportsManager.getExtentReports().createTest(testName);
        parentTestMap.put(Thread.currentThread().getId(), test);
        return test;
    }

    
    
    
    /**
     * Mengambil parent test berdasarkan Thread ID.
     *
     * @return {@link ExtentTest} instance parent test saat ini.
     */
    public static synchronized ExtentTest getTest() {
        return parentTestMap.get(Thread.currentThread().getId());
    }
    
    
    /**
     * Membuat node baru di bawah parent test dan menyimpannya berdasarkan Thread ID.
     *
     * @param stepName Nama step yang akan digunakan sebagai node.
     * @return {@link ExtentTest} instance node yang baru dibuat.
     */

    public static synchronized ExtentTest createNode(String stepName) {
    	
    	 ExtentTest parent = getTest();

    	    //  Jika parent test belum ada, buat otomatis
    	    if (parent == null) {
    	        parent = ExtentReportsManager.getExtentReports()
    	                   .createTest("Unnamed Test (Auto Created)");
    	        parentTestMap.put(Thread.currentThread().getId(), parent);
    	    }
        ExtentTest node = getTest().createNode(stepName);
        nodeTestMap.put(Thread.currentThread().getId(), node);
        return node;
    }

    
    
    
    /**
     * Mengambil node test yang sedang aktif berdasarkan Thread ID.
     * Jika node tidak ditemukan, akan otomatis membuat parent test baru.
     *
     * @return {@link ExtentTest} instance node atau parent test jika node tidak ditemukan.
     */
    public static synchronized ExtentTest getNode() {
    	 ExtentTest node = nodeTestMap.get(Thread.currentThread().getId());
    	 if (node == null) {
    	        ExtentTest parent = getTest();
    	        if (parent == null) {
    	            // create parent test otomatis jika hilang
    	            parent = ExtentReportsManager.getExtentReports().createTest("Unnamed Test");
    	            parentTestMap.put(Thread.currentThread().getId(), parent);
    	        }
    	        return parent;
    	    }
    	    return node;
    }
    
    
    
    /**
     * Menambahkan informasi (log info) ke node test yang sedang aktif.
     *
     * @param message Pesan yang akan ditampilkan di report.
     */
    public static synchronized void addInfo(String message) {
        getNode().info(message);
    }

    
    
    /**
     * Menambahkan screenshot ke node test yang sedang aktif.
     *
     * @param path Path file screenshot.
     */
    public static synchronized void addScreenshot(String path) {
        try {
            getNode().addScreenCaptureFromPath(path);
        } catch (Exception e) {
            getNode().warning("Failed to attach screenshot: " + e.getMessage());
        }
    }

}
