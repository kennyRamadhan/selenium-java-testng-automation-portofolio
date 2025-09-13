package Selenium.Pages;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import com.google.common.collect.Iterables;

import Appium.Config.DriverManager;
import Extent.Listeners.LogHelper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

import Selenium.CustomHelper.UtilsHelper;

public class ProductsList {
	
 private final UtilsHelper utils;
	
	public ProductsList() {
		
		this.utils = new UtilsHelper();
		 PageFactory.initElements(
			        new AppiumFieldDecorator(DriverManager.getDriver(), Duration.ofSeconds(15)),
			        this   );
	}
	
	@AndroidFindBy(accessibility = "test-ADD TO CART")
	@iOSXCUITFindBy(accessibility = "test-ADD TO CART")
	private WebElement addToCart;
	
	@AndroidFindBy(xpath="//android.widget.TextView[@content-desc='test-Item title']")
	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeStaticText[`name == \"test-Item title\"`]")
	private List<WebElement> listProducts;
	
	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeOther[`name == \"test-ADD TO CART\"`]")
	private List<WebElement> listAddToCart;
	
	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeOther[`name == \"test-Cart\"`]/XCUIElementTypeOther")
	private WebElement cartBtn;
	
	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeStaticText[`name == \"YOUR CART\"`]")
	private WebElement yourCartText;
	
	@AndroidFindBy(accessibility = "test-CHECKOUT")
	@iOSXCUITFindBy(accessibility = "test-CHECKOUT")
	private WebElement checkout;
	
	@AndroidFindBy(accessibility="test-First Name")
	@iOSXCUITFindBy(accessibility = "test-First Name")
	private WebElement firstNameField;
	
	@AndroidFindBy(accessibility = "test-FINISH")
	@iOSXCUITFindBy(accessibility = "test-FINISH")
	private WebElement finishBtn;
	
	@AndroidFindBy(accessibility = "test-CONTINUE")
	@iOSXCUITFindBy(accessibility = "test-CONTINUE")
	private WebElement continueBtn;
	
	@AndroidFindBy(accessibility="test-Zip/Postal Code")
	@iOSXCUITFindBy(accessibility = "test-Zip/Postal Code")
	private WebElement postalCodeField;
	
	@AndroidFindBy(accessibility="test-Last Name")
	@iOSXCUITFindBy(accessibility = "test-Last Name")
	private WebElement lastNameField;
	
	@AndroidFindBy(accessibility = "test-Error message")
	@iOSXCUITFindBy(accessibility = "test-Error message")
	private WebElement errorMessage;
	
	@AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"test-Price\"])[1]/android.widget.TextView")
	@iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@name=\"test-Price\"]")
	private List<WebElement> priceList;
	
	@AndroidFindBy(accessibility="test-Modal Selector Button")
	@iOSXCUITFindBy(accessibility = "test-Modal Selector Button")
	private WebElement filterBtn;
	

	@AndroidFindBy(xpath="//android.widget.TextView[@text='Price (low to high)']")
	@iOSXCUITFindBy(iOSNsPredicate =  "name == 'Price (low to high)'")
	private WebElement lowToHigh;
	
	
	
	public List<WebElement> getPriceElements() {
	    return priceList;
	}

	

	public void clickFilterBtn() {
		filterBtn.click();
	}

	public void clickLowToHigh() {
		lowToHigh.click();
	}
	
	public void addProductsToCartDirectlyFromListMenu() {
		

		WebElement lastElement = (WebElement) Iterables.get(listAddToCart,0);
		lastElement.click();
	
	}
	
	public String selectAndGetDetailsProducts(String productName) {
		
		LogHelper.step("Search and click product: " + productName);

	    int sizeProducts = listProducts.size();
	    boolean productFound = false;
	    String descriptionProducts = null;

	    for (int i = 0; i < sizeProducts; i++) {
	        String currentProductName = listProducts.get(i).getText();

	        if (currentProductName.equalsIgnoreCase(productName)) {
	            productFound = true;

	            // Klik produk sesuai index
	            listProducts.get(i).click();
	            LogHelper.detail("Clicked product: " + currentProductName);

	            // Ambil nama produk yang tampil di detail page
	            String chosenProductName = listProducts.get(i).getText();

	            if (chosenProductName.equalsIgnoreCase(currentProductName)) {
	                LogHelper.pass("✅ Product matched! Displayed product: " + chosenProductName);
	            } else {
	                LogHelper.fail("❌ Product mismatch! Expected: " + currentProductName + " but got: " + chosenProductName);
	                Assert.fail("Product mismatch!");
	            }

	            // Ambil description product di detail page
	            descriptionProducts = DriverManager.getDriver()
	                    .findElement(AppiumBy.xpath("//android.view.ViewGroup[@content-desc='test-Description']/android.widget.TextView[1]"))
	                    .getText();

	            break; // Stop loop setelah ketemu
	        }
	    }

	    if (!productFound) {
	        LogHelper.fail("❌ Product '" + productName + "' not found in the list.");
	        Assert.fail("Product '" + productName + "' not found!");
	    }

	    return descriptionProducts;
		
	}
	

	public void addMultipleProducts() {
		WebElement lastElement = (WebElement) Iterables.get(listAddToCart,0);
		WebElement last = (WebElement) Iterables.get(listAddToCart,1);
		lastElement.click();
		last.click();
	}

	@AndroidFindBy(accessibility  = "test-CONTINUE SHOPPING")
	private WebElement continueShoppingBtn;
	public void clickContinueShoppingBtn() {
		
		continueShoppingBtn.click();
	}
	
	public void verifyBackToListProducts() {
		
		if(!listProducts.isEmpty()){	
				Reporter.log("Success Back To List Products");
		 	}
		else{
				Reporter.log("Failed Back To List Products");
		}
	}
	
	public Double getTotalPriceBeforeCheckout() {
		
		List<WebElement> productPrice = DriverManager.getDriver().findElements(AppiumBy.xpath("//android.view.ViewGroup[@content-desc='test-Price']/android.widget.TextView"));
		int countProductPrice = productPrice.size();
		double totalAmount = 0;
		
		// sum total amount chosen products
	    for(int i = 0; i<countProductPrice; i++ ) {

			String sortPrices = productPrice.get(i).getText();
			double expectedTotalAmountWithoutTaxes = Double.parseDouble(sortPrices.substring(1));
			totalAmount = totalAmount+expectedTotalAmountWithoutTaxes;
			Reporter.log("Total Amount Price Without Taxes : "+totalAmount);
		}
		
		return totalAmount;
	}

	
	
	public double getActualTotalPriceAfterCheckout() {
		DriverManager.getDriver().findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"Item total: $39.98\"));"));
		String actualTotalAmountString = DriverManager.getDriver().findElement(AppiumBy.xpath("//android.widget.TextView[@text='Item total: $39.98']")).getText();
		double actualTotalAmount = Double.parseDouble(actualTotalAmountString.substring(13));
		Reporter.log("Actual Total Amount : "+actualTotalAmount);
		return actualTotalAmount;
	}
	
	public void clickFinishBtn() {
		finishBtn.click();
	}
		
	@AndroidFindBy(xpath = "//android.widget.ScrollView[@content-desc=\"test-CHECKOUT: COMPLETE!\"]/android.view.ViewGroup/android.widget.TextView[1]")
	private WebElement orderComplete;
	public void verifyOrderComplete() {
		
	    if(orderComplete.isDisplayed()) {
	    	
	    	Reporter.log("Order Completed");
	    }
	    else
	    {
	    	Reporter.log("Failed Order");
	    }
	}

	public void verifyErrorMessage() {
		
	    if(errorMessage.isDisplayed()) {
	    	
	    	Reporter.log("Negative Flow Success");
	    }
	    else
	    {
	    	Reporter.log("Failed");
	    }
	}
	

	public void addAllProducts() {
	   
	    int totalAdded = 0;
	    LogHelper.step("Add All Products To Carts");
	    while (true) {
	        // Ambil ulang array tombol ADD TO CART yang terlihat
	  
	        // Kalau array kosong, berhenti
	        if (listAddToCart.isEmpty()) {
	        	LogHelper.detail("Verify All Products Add To Cart");
	            System.out.println("✅ Semua produk berhasil ditambahkan ke keranjang!");
	            break;
	        }

	        // Klik tombol satu per satu
	        for (WebElement addButton : listAddToCart) {
	            try {
	                addButton.click();

	                // Pastikan tombol berubah jadi REMOVE (menandakan sukses klik)
	                new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(2))
	                        .until(ExpectedConditions.attributeToBe(addButton, "name", "test-REMOVE"));

	                totalAdded++;
	                System.out.println("🛒 Produk ke-" + totalAdded + " berhasil ditambahkan.");
	            } catch (StaleElementReferenceException ignored) {
	                // Tombol sudah hilang dari DOM → lanjutkan saja
	            }
	        }

	        // Scroll agar menemukan tombol berikutnya
	        Map<String, Object> params = new HashMap<>();
	        params.put("direction", "down");
	        DriverManager.getDriver().executeScript("mobile: scroll", params);
	    }
	    LogHelper.step("Tap Cart Button & Verifikasi Halaman Cart");

	    try {
	        // 1️⃣ Pastikan button ada & visible
	        if (cartBtn.isDisplayed()) {
	            LogHelper.detail("Cart button ditemukan.");

	            // 2️⃣ Tap button
	            utils.tapByCoordinates(cartBtn,5);
	            LogHelper.detail("Cart button berhasil di-tap.");
	            
	            // 3️⃣ Verifikasi halaman cart
	            if (yourCartText.isDisplayed()) {
	                LogHelper.detail("Verifikasi Masuk Keranjang");
	            } else {
	               
	                Assert.fail("Halaman Cart tidak muncul setelah tap.");
	            }

	        } else {
	            
	            Assert.fail("Cart button tidak ditemukan.");
	        }

	    } catch (Exception e) {
	        LogHelper.detail("Exception: " + e.getMessage());
	        Assert.fail("Gagal tap cart button atau verifikasi cart page.", e);
	    }
	    

	    
	}
	
	public void tapCart() {
			
		cartBtn.click();
		
		}

}
