package Extent.Listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;


/**
 * <h1>LogHelper</h1>
 * Kelas utilitas untuk membantu penulisan log ke dalam Extent Report.
 * 
 * <p><b>Fungsi utama:</b></p>
 * <ul>
 *   <li>Mencatat langkah (step) dengan nomor urut otomatis.</li>
 *   <li>Mencatat detail hasil eksekusi (PASS / FAIL).</li>
 *   <li>Mencatat warning atau informasi tambahan.</li>
 * </ul>
 *
 * <p><b>Contoh Penggunaan:</b></p>
 * <pre>
 * LogHelper.resetCounter();
 * LogHelper.step("Buka halaman login");
 * LogHelper.detail("Input username berhasil");
 * LogHelper.pass("Login berhasil");
 * </pre>
 *
 * <p>Kelas ini bekerja bersama {@link ExtentNode} untuk mencatat log dalam report.
 * Jika tidak ada step aktif, log akan ditulis langsung pada parent test.</p>
 *
 * @author Kenny Ramadhan
 * @version 1.0
 */


public class LogHelper {
	
	 /** Counter otomatis untuk penomoran step */
    private static int stepCounter = 1;
    
    /** Menyimpan node step yang sedang aktif */
    private static ExtentTest currentStepNode;
    

    /**
     * Reset counter step ke 1.
     * 
     * <p>Gunakan di awal test case untuk memastikan step dimulai dari STEP 1.</p>
     */
    public static void resetCounter() {
        stepCounter = 1;
    }
    
    
    /**
     * Membuat step baru pada report.
     *
     * @param message Deskripsi step.
     */
    public static void step(String message) {
        String stepMessage = "STEP " + stepCounter++ + ": " + message;
        //currentStepNode = ExtentNode.getTest().createNode(stepMessage);
        currentStepNode = ExtentNode.createNode(stepMessage);
        //currentStepNode.log(Status.INFO,stepMessage);
    }
    

    
    
    /**
     * Menambahkan detail (log PASS) ke step aktif.
     * Jika step belum ada, maka akan mencatat log FAIL di parent test.
     *
     * @param message Pesan detail yang akan ditampilkan di report.
     */
    public static void detail(String message) {
        if (currentStepNode != null) {
            currentStepNode.log(Status.INFO, message);
        } else {
            ExtentNode.getTest().log(Status.FAIL, message);
        }
    }

    
    
    
    /**
     * Menandai step atau test sebagai PASS.
     *
     * @param message Pesan keberhasilan.
     */
    public static void pass(String message) {
    	if (currentStepNode != null) {
            currentStepNode.log(Status.PASS, message);
        } else {
            ExtentNode.getTest().log(Status.FAIL, message);
        }
    }
    
    
    /**
     * Menandai step atau test sebagai FAIL.
     *
     * @param message Pesan kegagalan.
     */

    public static void fail(String message) {
    	if (currentStepNode != null) {
            currentStepNode.log(Status.FAIL, message);
        } else {
            ExtentNode.getTest().log(Status.FAIL, message);
        }
    }

    
    
    /**
     * Menambahkan log warning atau informasi tambahan ke node yang sedang aktif.
     *
     * @param message Pesan peringatan atau informasi.
     */
    public static void warning(String message) {
        ExtentNode.getNode().info(message);
    }

}
