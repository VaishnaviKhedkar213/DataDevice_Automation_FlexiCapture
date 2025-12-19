package DataDevice.FlexiCapture;

import org.testng.annotations.Test;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import java.nio.file.Files;

public class InvoiceCheaking {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    //============================================================
    @Test(priority = 1)
    public void loginTest() throws InterruptedException {
        driver.get("https://afc5.datadevice.com.au/FlexiCapture12/Login/Trescon#/Login");
        driver.findElement(By.id("userName")).sendKeys("Admin_Trescon");
        driver.findElement(By.id("password")).sendKeys("!o6jRZ@R");
        driver.findElement(By.id("loginButton")).click();
        System.out.println("=========== Login Successfully ==============\n");
    }

    //==============================================================
    @Test(priority = 2)
    public void clickWebVerification() throws Exception {
        WebElement link = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//a[@class='link' and contains(text(),'Web Verification Station')]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        System.out.println("===== Clicked Web Verification Station =====\n");
    }

    //============================================================
    @Test(priority = 3)
    public void selectDropdowns() {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("projectSelect")));
        Select select = new Select(dropdown);
        select.selectByVisibleText("Trescon_NewVersion");

        WebElement dropdown2 = wait.until(ExpectedConditions.elementToBeClickable(By.id("roleSelect")));
        Select select1 = new Select(dropdown2);
        select1.selectByVisibleText("Senior Verification Operator");

        driver.findElement(By.id("SelectButton")).click();
        System.out.println("===== Dropdowns selected =====\n");
    }

    //============================================================
    @Test(priority = 4)
    public void exploreQueue() {
        WebElement exploreQueue = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("div.queue__card_item_key.queue__exploreQueue\r\n"
            		+ "")
        ));
        exploreQueue.click();
        System.out.println("===== Explore Queue Clicked =====\n");
    }

    //============================================================
    @Test(priority = 5)
    public void processAllTasks() throws InterruptedException {
        // Step 1: Set table to 100 entries
        Thread.sleep(2000);
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#tasks_table_length select")
        ));
        Select select = new Select(dropdown);
        select.selectByIndex(2);
        System.out.println("===== Entries per page set to 100 =====\n");
        Thread.sleep(1500);

        // Step 2: Collect all task URLs first
        java.util.List<WebElement> taskElements = driver.findElements(By.cssSelector("#tasks_table tbody tr td:first-child span.taskLink"));
        java.util.List<String> taskUrls = new java.util.ArrayList<>();
        java.util.List<String> taskNames = new java.util.ArrayList<>();
        
        for (WebElement task : taskElements) {
            String url = task.getAttribute("data-href");
            String name = task.getText();
            taskUrls.add(url);
            taskNames.add(name);
        }
        
        System.out.println("===== Total tasks found: " + taskUrls.size() + " =====\n");

        // Step 3: Process each task by direct URL
        for (int i = 0; i < taskUrls.size(); i++) {
            System.out.println("\n========================================");
            System.out.println("===== Processing Task " + (i + 1) + " of " + taskUrls.size() + " → " + taskNames.get(i) + " =====");
            System.out.println("========================================\n");

            // Open task directly by URL
            driver.get(taskUrls.get(i));
            Thread.sleep(2000);

            // Run all validations + reject
            runAllValidations();

            // Return to queue
            System.out.println("===== Returning to queue... =====\n");
            driver.get("https://afc5.datadevice.com.au/FlexiCapture12/Verification/Trescon/Tasks?projectId=58&roleid=702&stageType=500&queueName=Verification");
            Thread.sleep(2000);
            
            System.out.println("===== Task " + (i + 1) + " completed successfully =====\n");
        }

        System.out.println("\n========================================");
        System.out.println("===== ALL TASKS PROCESSED SUCCESSFULLY =====");
        System.out.println("===== Total tasks processed: " + taskUrls.size() + " =====");
        System.out.println("========================================\n");
    }
    //============================================================
    public void readWorkId() {
        try {
            WebElement workIdBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@data-fieldname='WorkId']//div[@contenteditable='true']")
                )
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", workIdBox);
            Thread.sleep(300);
            
            String workId = workIdBox.getText().trim().replaceAll("[\\n\\r,]", "");// REMOVE extra spaces (new lines) + REMOVE commas
            if (workId.isEmpty())
                System.out.println("❌ Work ID is BLANK");
            else
                System.out.println("✓ Work ID value is: " + workId);
        } catch (Exception e) {
            System.out.println("❌ Error reading WorkId: " + e.getMessage());
        }
    }

    public void readInvoiceNo() {
        try {
            WebElement box = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@title='Invoice Number' and @contenteditable='true']")
                )
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", box);
            Thread.sleep(300);
            
            String txt = box.getText().trim().replaceAll("[\\n\\r,]", ""); // REMOVE extra spaces (new lines) + REMOVE commas
            if (txt.isEmpty())
                System.out.println("❌ InvoiceNo is BLANK");
            else
                System.out.println("✓ InvoiceNo value is: " + txt);
        } catch (Exception e) {
            System.out.println("❌ Error reading InvoiceNo: " + e.getMessage());
        }
    }

    public void readInvoiceDate() {
        try {
            WebElement box = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@title='Invoice Date' and @contenteditable='true']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", box);
            Thread.sleep(300);
            
            String txt = box.getText().trim().replaceAll("[\\n\\r,]", ""); // REMOVE extra spaces (new lines) + REMOVE commas
            if (txt.isEmpty()) {
                System.out.println("❌ InvoiceDate is BLANK");
                return;
            }
            System.out.print("✓ InvoiceDate value is: " + txt);
            if (isValidDateFormat(txt)) {
                System.out.println(" → Format CORRECT (dd/MM/yy)");
            } else {
                System.out.println(" → Format INCORRECT (Expected dd/MM/yy)");
            }
        } catch (Exception e) {
            System.out.println("❌ Error reading InvoiceDate: " + e.getMessage());
        }
    }

    private boolean isValidDateFormat(String dateStr) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yy");
            sdf.setLenient(false);
            sdf.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void readDescription() {
        try {
            WebElement box = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@title='Description' and @contenteditable='true']")
                )
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", box);
            Thread.sleep(300);
            
            String txt = box.getText().trim().replaceAll("[\\n\\r,]", ""); // REMOVE extra spaces (new lines) + REMOVE commas
            if (txt.isEmpty())
                System.out.println("❌ Description box is BLANK");
            else
                System.out.println("✓ Description is: " + txt);
        } catch (Exception e) {
            System.out.println("❌ Error reading Description: " + e.getMessage());
        }
    }

    public void readJobNumber() {
        try {
            WebElement box = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@title='JobNumber' and @contenteditable='true']")
                )
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", box);
            Thread.sleep(300);
            
            String txt = box.getText().trim().replaceAll("[\\n\\r,]", ""); // REMOVE extra spaces (new lines) + REMOVE commas
            if (txt.isEmpty())
                System.out.println("❌ JobNo box is BLANK");
            else
                System.out.println("✓ JobNo is: " + txt);
        } catch (Exception e) {
            System.out.println("❌ Error reading JobNumber: " + e.getMessage());
        }
    }

    public void readLocation() {
        try {
            WebElement box = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@title='Location' and @contenteditable='true']")
                )
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", box);
            Thread.sleep(300);
            
            String txt = box.getText().trim().replaceAll("[\\n\\r,]", ""); // REMOVE extra spaces (new lines) + REMOVE commas
            if (txt.isEmpty())
                System.out.println("❌ Location is BLANK");
            else
                System.out.println("✓ Location value: " + txt);
        } catch (Exception e) {
            System.out.println("❌ Error reading Location: " + e.getMessage());
        }
    }

    public void readInvoiceAmount() {
        try {
            WebElement box = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@title='InvoiceAmount' and @contenteditable='true']"))
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", box);
            Thread.sleep(300);
            
            String txt = box.getText().trim().replaceAll("[\\n\\r]", "").replace(",", ""); // REMOVE extra spaces (new lines) + REMOVE commas
            if (txt.isEmpty()) {
                System.out.println("❌ Invoice Amount is BLANK");
            } else {
                System.out.print("✓ Invoice Amount value: " + txt);
                if (txt.matches("\\d+(\\.\\d+)?")) {
                    System.out.println(" → Valid number");
                } else {
                    System.out.println(" → INVALID characters");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error reading InvoiceAmount: " + e.getMessage());
        }
    }

    public void readGSTAmount() {
        try {
            WebElement box = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@title='GSTAmount' and @contenteditable='true']"))
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", box);
            Thread.sleep(300);
            
            String txt = box.getText().trim().replaceAll("[\\n\\r]", "").replace(",", ""); // REMOVE extra spaces (new lines) + REMOVE commas
            if (txt.isEmpty()) {
                System.out.println("❌ GST Amount textbox is BLANK");
            } else {
                System.out.print("✓ GST Amount value: " + txt);
                if (txt.matches("\\d+(\\.\\d+)?")) {
                    System.out.println(" → Valid number");
                } else {
                    System.out.println(" → INVALID characters");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error reading GSTAmount: " + e.getMessage());
        }
    }

    public void hoverAndClickKebab() {
        try {
            System.out.println("--- Attempting to click kebab menu ---");
            Thread.sleep(2000);
            
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Actions actions = new Actions(driver);
            
            // Step 1: Find and hover over kebab area
            WebElement kebabArea = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[@class='kebabArea-xvWCTf']")
                )
            );
            
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", kebabArea);
            Thread.sleep(500);
            
            // Hover over the kebab area
            actions.moveToElement(kebabArea).perform();
            System.out.println("✓ Hovered over kebab area");
            Thread.sleep(1200); // Wait longer for three dots button to appear
            
            // Step 2: Click the kebab button using JavaScript directly
            String clickKebabScript = 
                "var kebabArea = document.querySelector('.kebabArea-xvWCTf');" +
                "if (kebabArea) {" +
                "    var button = kebabArea.querySelector('button');" +
                "    if (button) {" +
                "        button.click();" +
                "        return 'Success - clicked button inside kebabArea';" +
                "    }" +
                "    var parent = kebabArea.parentElement;" +
                "    if (parent) {" +
                "        button = parent.querySelector('button');" +
                "        if (button) {" +
                "            button.click();" +
                "            return 'Success - clicked button in parent';" +
                "        }" +
                "    }" +
                "    var kebabButton = document.querySelector('[class*=\"kebabButton\"]');" +
                "    if (kebabButton) {" +
                "        kebabButton.click();" +
                "        return 'Success - clicked kebabButton class';" +
                "    }" +
                "    var kebabDiv = document.getElementById('document-1-kebab');" +
                "    if (kebabDiv) {" +
                "        button = kebabDiv.querySelector('button');" +
                "        if (button) {" +
                "            button.click();" +
                "            return 'Success - clicked button in document-1-kebab';" +
                "        }" +
                "    }" +
                "}" +
                "return 'Failed - kebab button not found';";
            
            String result = (String) js.executeScript(clickKebabScript);
            System.out.println("✓ Kebab click result: " + result);
            Thread.sleep(1500);
            
        } catch (Exception e) {
            System.out.println("❌ Kebab click failed: " + e.getMessage());
        }
    }


    public void clickUpdateDocumentDefinition() {
        try {
            System.out.println("--- Attempting to click 'Update document definition' option ---");
            Thread.sleep(1000);
            
            JavascriptExecutor js = (JavascriptExecutor) driver;
            
            String clickUpdateScript = 
                "var updateDiv = document.getElementById('document-context-menu--update-template');" +
                "if (updateDiv) {" +
                "    updateDiv.click();" +
                "    return 'Success - clicked by ID';" +
                "}" +
                "var items = document.querySelectorAll('[class*=\"leftIconItem\"]');" +
                "for (var i = 0; i < items.length; i++) {" +
                "    var text = items[i].textContent || items[i].innerText || '';" +
                "    if (text.includes('Update document definition')) {" +
                "        items[i].click();" +
                "        return 'Success - clicked leftIconItem with text';" +
                "    }" +
                "}" +
                "var contextMenu = document.querySelector('[class*=\"context-menu\"], [id*=\"context-menu\"]');" +
                "if (contextMenu) {" +
                "    var allDivs = contextMenu.querySelectorAll('div');" +
                "    for (var i = 0; i < allDivs.length; i++) {" +
                "        var text = allDivs[i].textContent || '';" +
                "        if (text.includes('Update document definition')) {" +
                "            allDivs[i].click();" +
                "            return 'Success - clicked via context menu search';" +
                "        }" +
                "    }" +
                "}" +
                "var allElements = document.querySelectorAll('*');" +
                "for (var i = 0; i < allElements.length; i++) {" +
                "    var el = allElements[i];" +
                "    if (el.id && el.id.includes('update-template')) {" +
                "        el.click();" +
                "        return 'Success - clicked element with update-template ID';" +
                "    }" +
                "}" +
                "return 'Failed - Update option not found';";
            
            String result = (String) js.executeScript(clickUpdateScript);
            System.out.println("✓ Update definition click result: " + result);
            Thread.sleep(1500);
            
        } catch (Exception e) {
            System.out.println("❌ Failed to click 'Update document definition': " + e.getMessage());
        }
    }
    
    public void clickRecognize() {
        try {
            System.out.println("--- Attempting to click RECOGNIZE button ---");

            JavascriptExecutor js = (JavascriptExecutor) driver;

            String script =
                "var span = Array.from(document.querySelectorAll('span.buttonName-1D25tZ'))" +
                ".find(s => s.textContent.trim() === 'Recognize');" +
                "if (!span) return 'SPAN_NOT_FOUND';" +
                "var canvas = span.parentElement.querySelector('canvas.imageButtonCanvas-1PMoMG');" +
                "if (!canvas) return 'CANVAS_NOT_FOUND';" +
                "canvas.click();" +
                "return 'CLICKED';";

            String result = (String) js.executeScript(script);

            if ("CLICKED".equals(result)) {
                System.out.println("✓ RECOGNIZE clicked successfully");
            } else {
                System.out.println("❌ RECOGNIZE click failed → " + result);
            }

            Thread.sleep(5000);

        } catch (Exception e) {
            System.out.println("❌ Error clicking RECOGNIZE: " + e.getMessage());
        }
    }

  
    public void waitForRecognitionToComplete() {
    	    try {
    	        System.out.println("⏳ Waiting 30 seconds for recognition to complete...");
    	        Thread.sleep(40000); // 30 seconds
    	        System.out.println("✓ 30 seconds wait completed");
    	    } catch (InterruptedException e) {
    	        System.out.println("❌ Wait interrupted: " + e.getMessage());
    	        Thread.currentThread().interrupt();
    	    }
    	}

    public void clickServiceFields() {
        try {
            System.out.println("⏳ Waiting for ServiceFields to be present...");

            WebElement serviceFields = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[@data-fieldname='ServiceFields']")
                )
            );

            ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", serviceFields);

            Thread.sleep(300);

            ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", serviceFields);

            System.out.println("✓ ServiceFields clicked");

        } catch (Exception e) {
            System.out.println("❌ Error clicking ServiceFields: " + e.getMessage());
        }
    }

    public void readReceivedDate() {
        try {
            WebElement box = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@title='ReceivedDate' and @contenteditable='true']")));
            
            // Scroll the element into view
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", box);
            Thread.sleep(300);
            
            // Read text, remove line breaks and commas
            String txt = box.getText().trim().replaceAll("[\\n\\r,]", "");
            if (txt.isEmpty()) {
                System.out.println("❌ ReceivedDate is BLANK");
                return;
            }
            
            System.out.println("✓ ReceivedDate value is: " + txt);

            // Check against all valid formats
            if (isValidDateFormat(txt)) {
                System.out.println(" → Format CORRECT (matches one of the valid formats)");
            } else {
                System.out.println(" → Format INCORRECT (Invalid date format)");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error reading ReceivedDate: " + e.getMessage());
        }
    }

    private boolean isValidDateFormat1(String dateStr) {
        String[] VALID_DATE_FORMATS = {
            "d/M/yyyy",
            "ddd, d MMM yyyy HH:mm:ss zzz '(*)'",
            "ddd, dd MMM yyyy HH:mm:ss zzz '*'",
            "ddd, d MMM yyyy HH:mm:ss 'GMT'",
            "ddd, dd MMM yyyy HH:mm:ss 'GMT'",
            "ddd, d MMM yyyy HH:mm:ss zzz '(*)''(*)'",
            "d MMM yyyy HH:mm:ss zzz",
            "ddd,  d MMM yyyy HH:mm:ss zzz",
            "ddd, d MMM yyyy HH:mm:ss zzz",
            "ddd,  d MMM yyyy HH:mm:ss (UTC)",
            "ddd, d MMM yyyy HH:mm:ss (UTC)",
            "ddd, dd MMM yyyy HH:mm:ss (UTC)",
            "ddd, d MMM yyyy HH:mm:ss",
            "ddd, d MMM yyyy HH:mm:ss",
            "ddd, dd MMM yyyy HH:mm:ss",
            "dd/MM/yy",
            "dd-MMM-yy",
            "d-M-yyyy",
            "d-M-yy",
            "dd.MM.yy",
            "MM.dd.yy",
            "dd.MM.yyyy",
            "MM.dd.yyyy",
            "MMM d, yyyy",
            "d MMM yyyy",
            "d MMM yy",
            "ddMMMyy",
            "d-MMMM-yyyy",
            "yyyy-MM-dd",
            "d/MMM/yy",
            "d/MMM/yyyy",
            "d/MMMM/yyyy",
            "d MMMM yyyy",
            "d/MMMM/yy",
            "d MMMM yy",
            "dMMMMyyyy",
            "ddMMMyyyy",
            "M-d-yyyy",
            "d-MMM-yy",
            "d-MMM-yyyy",
            "d-MMMM-yyyy",
            "d/M/yy",
            "MMMM yyyy",
            "MMM-yy",
            "dd-MMM",
            "dd.MMM yyyy",
            "dd -MMMM",
            "dd -MMM",
            "dd-MMMM",
            "dd. MMMM yyyy",
            "d. MMMM yyyy",
            "MMM dd,yyyy",
            "MMM dd, yyyy",
            "MMMM dd,yyyy",
            "MMMM dd, yyyy",
            "MMMMdd,yyyy",
            "MMMdd,yyyy"
        };
        
        for (String format : VALID_DATE_FORMATS) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
                sdf.setLenient(false);
                sdf.parse(dateStr);
                return true; // Found a matching format
            } catch (Exception e) {
                // Ignore, try next format
            }
        }
        return false; // No matching format found
    }
    
    
    public void handleUnknownToInvoiceAndRecognize() {
        try {
            System.out.println("🔍 Checking document type...");

            // STEP 1: Check if document is UNKNOWN
            boolean isUnknown;
            try {
                wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class,'documentHeader') and contains(text(),'Unknown')]")
                    )
                );
                isUnknown = true;
                System.out.println("⚠ Document type is UNKNOWN");
            } catch (Exception e) {
                isUnknown = false;
                System.out.println("✓ Document type is NOT Unknown");
            }

            if (!isUnknown) {
                return; // No action needed
            }

            // STEP 2: Hover & click kebab menu (three dots)
            System.out.println("➡ Clicking kebab menu");
            hoverAndClickKebab();
            Thread.sleep(1500);

            // STEP 3: Hover Change Document Definitions → Invoice
            System.out.println("➡ Changing document definition to INVOICE");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Actions actions = new Actions(driver);

            WebElement changeDefinition = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.id("document-context-menu--change-template")
                )
            );

            actions.moveToElement(changeDefinition).perform();
            Thread.sleep(1000);

            WebElement invoiceOption = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'menu') and contains(@class,'visible')]//div[text()='Invoice']")
                )
            );

            actions.moveToElement(invoiceOption).click().perform();
            System.out.println("✓ Invoice selected");

            // STEP 4: Wait 10 seconds (as requested)
            System.out.println("⏳ Waiting 10 seconds after changing definition...");
            Thread.sleep(10000);

            // STEP 5: Click Recognize
            System.out.println("➡ Clicking RECOGNIZE");
            clickRecognize();

            // STEP 6: Wait for recognition to complete
            waitForRecognitionToComplete();

            System.out.println("✅ Unknown → Invoice → Recognize flow completed");

        } catch (Exception e) {
            System.out.println("❌ Flow failed: " + e.getMessage());
        }
    }

 public void checkAndMergeSeparatedInvoicePages() {
    try {
        System.out.println("🔍 Checking if invoice pages are separated...");

        // Check Invoice page count
        int invoicePages = driver.findElements(
            By.xpath("//div[contains(@class,'documentHeader') and contains(.,'Invoice')]" +
                     "/following::div[contains(@class,'thumbnail')]")
        ).size();

        // Check Unprocessed document existence
        boolean unprocessedExists = !driver.findElements(
            By.xpath("//div[contains(@class,'documentHeader') and contains(.,'Unprocessed document')]")
        ).isEmpty();

        if (invoicePages == 1 && unprocessedExists) {
            System.out.println("⚠ Pages are separated → merging required");

            WebElement sourcePage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'documentHeader') and contains(.,'Unprocessed document')]" +
                             "/following::div[contains(@class,'thumbnail')][1]")
                )
            );

            WebElement targetInvoice = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'documentHeader') and contains(.,'Invoice')]")
                )
            );

            // Scroll
            ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", sourcePage);
            Thread.sleep(300);

            ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", targetInvoice);
            Thread.sleep(300);

            // HTML5 Drag & Drop
            String jsDragDrop =
                "function dragDrop(src, tgt) {" +
                " const dataTransfer = new DataTransfer();" +
                " src.dispatchEvent(new DragEvent('dragstart',{dataTransfer,bubbles:true}));" +
                " tgt.dispatchEvent(new DragEvent('dragenter',{dataTransfer,bubbles:true}));" +
                " tgt.dispatchEvent(new DragEvent('dragover',{dataTransfer,bubbles:true}));" +
                " tgt.dispatchEvent(new DragEvent('drop',{dataTransfer,bubbles:true}));" +
                " src.dispatchEvent(new DragEvent('dragend',{dataTransfer,bubbles:true}));" +
                "}" +
                "dragDrop(arguments[0],arguments[1]);";

            ((JavascriptExecutor) driver)
                .executeScript(jsDragDrop, sourcePage, targetInvoice);

            Thread.sleep(2000);
            System.out.println("✅ Pages merged successfully");

        } else {
            System.out.println("✓ Pages already merged → no action needed");
        }

    } catch (Exception e) {
        System.out.println("❌ Page merge check failed: " + e.getMessage());
    }
}


    public void clickReject() {
        try {
            WebElement Reject = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='content']/div/div[1]/div/div[3]/div/div[3]/div")
                )
            );
            Reject.click();
            System.out.println("\n✓ Reject button clicked");
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("❌ Error clicking Reject: " + e.getMessage());
        }
    }

    public void confirmReject() {
        try {
            WebElement RejectClk = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"rejectButton\"]/canvas")
                )
            );
            RejectClk.click();
            System.out.println("✓ Reject confirmed");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("❌ Error confirming Reject: " + e.getMessage());
        }
    }

    //============================================================
    public void runAllValidations() {
        System.out.println("\n--- Running All Validations ---");
        readWorkId();
        readInvoiceNo();
        readInvoiceDate();
        readDescription();
        readJobNumber();
        readLocation();
        readInvoiceAmount();
        readGSTAmount();
        hoverAndClickKebab();
        clickUpdateDocumentDefinition();
        clickRecognize();
        waitForRecognitionToComplete();
        System.out.println("--- Validations Complete ---\n");
        clickServiceFields();
        readReceivedDate();
        handleUnknownToInvoiceAndRecognize();//check
        checkAndMergeSeparatedInvoicePages();//check
        clickReject();
       confirmReject();
    }

    //============================================================
    @org.testng.annotations.AfterMethod
    public void captureScreenshotOnFailure(org.testng.ITestResult result) {
        if (result.getStatus() == org.testng.ITestResult.FAILURE) {
            try {
                System.out.println("===== Test Failed → Capturing Screenshot =====");
                File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                String fileName = "screenshots/" + result.getName() + "_Failed.png";
                File dest = new File(fileName);
                dest.getParentFile().mkdirs();
                Files.copy(src.toPath(), dest.toPath());
                System.out.println("===== Screenshot Saved: " + dest.getAbsolutePath() + " =====\n");
            } catch (Exception e) {
                System.out.println("===== Screenshot Capture Failed: " + e.getMessage() + " =====\n");
            }
        }
    }

    @AfterClass
    public void tearDown() {
        // Uncomment to close browser after tests
          driver.quit();
    }
}

