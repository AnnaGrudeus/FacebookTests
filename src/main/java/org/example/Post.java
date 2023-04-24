package org.example;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


class Post {
    private WebDriver driver;
    private Logger logger = LoggerFactory.getLogger(Post.class);
    private WebDriverWait wait;
    String email = null;
    String password = null;

    static {
        System.setProperty("logback.configurationFile", "src/main/resources/logback.xml");
    }

    @BeforeAll
    static void setUpBeforeClass() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Anna Grudeus\\Desktop\\chromedriver.exe");
    }
    //"C:\Users\Anna Grudeus\Desktop\chromedriver.exe"
    //C:\Users\Administrator\Desktop\chromedriver.exe

    @BeforeEach
    void setUp() {

        // Create an instance of ChromeOptions and add the desired option
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");

        // Create an instance of ChromeDriver with the options
        driver = new ChromeDriver(options);

        // Create an instance of WebDriverWait to explicitly wait for elements to load
        wait = new WebDriverWait(driver, 10);

        //Retrieve email and password
        File jsonFile = new File("C:\\temp\\facebook.json");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonFile);

            email = jsonNode.get("facebookCredentials").get("email").asText();
            password = jsonNode.get("facebookCredentials").get("password").asText();

            System.out.println("Email: " + email);
            System.out.println("Password: " + password);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Test
    void testPost() {
        //Go to facebook
        driver.get("https://www.facebook.com/");

        // If there is a cookie box displayed
        try {
            //Find cookies element, if it is displayed
            if (driver.findElement(By.cssSelector("div[data-testid='cookie-policy-manage-dialog']")).isDisplayed()) {
                WebElement cookiePolicyDialog = driver.findElement(By.cssSelector("div[data-testid='cookie-policy-manage-dialog']"));
                // Click the "Accept" button for cookies located by data-testid
                cookiePolicyDialog.findElement(By.cssSelector("[data-testid='cookie-policy-manage-dialog-accept-button']")).click();
                // Locate and retrieve the cookie dialog element
                WebElement cookieDialog = driver.findElement(By.cssSelector("div[data-testid='cookie-policy-manage-dialog']"));

                // Check if the cookie dialog is displayed
                if (cookieDialog.isDisplayed()) {
                    logger.info("Cookies dialog displayed");
                    // Find and click on the accept button within the dialog
                    cookieDialog.findElement(By.cssSelector("[data-testid='cookie-policy-manage-dialog-accept-button']")).click();
                    logger.info("Cookies accepted");
                }
            }
        } catch (Exception e) {
            logger.error("An error occurred while accepting cookies", e);
            // If the cookie dialog is not displayed or there is an exception, log a message
            logger.info("Cookies dialog not displayed");
        }

        try {
            //Enter username and password
            WebElement emailField = driver.findElement(By.id("email"));
            emailField.sendKeys(email);
            WebElement passwordField = driver.findElement(By.id("pass"));
            passwordField.sendKeys(password);
        } catch (Exception ex) {
            logger.info("Username and password could not be inserted");
        }

        //Click to login
        try {
            WebElement loginButton = driver.findElement(By.xpath("//button[@name='login']"));
            loginButton.click();

            wait.until(ExpectedConditions.urlContains("https://www.facebook.com/"));

            logger.info("Login successful");
        } catch (Exception ex) {
            logger.error("An error occure when trying to log in", ex);
            logger.info("You have not logged in");
        }

        // Click to create a new post
        try {
            WebElement createPostBtn = driver.findElement(By.xpath("//div[@aria-label='Skapa']"));
            createPostBtn.click();

            // Click on "inlägg" to see textbox
            WebElement inlaggButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Inlägg')]")));
            inlaggButton.click();

            // Write a message in textbox
            WebElement text = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div[aria-label*='Vad']")));
            text.sendKeys("Test by Anna");

            //Click to publish
            WebElement publish = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Publicera')]")));
            publish.click();
            logger.info("Post created on profile");
        } catch (Exception ex) {
            logger.error("Error occurred when posting message", ex);
            logger.info("Message could not be posted");
        }


        //View post
        try {
            // Navigate to profile page
            driver.get("https://www.facebook.com/profile.php");
            driver.get("https://www.facebook.com/profile.php");

            // Verify that the post is successfully published and displayed on the user's timeline
            WebElement postText = driver.findElement(By.xpath("//*[contains(text(),'Test by')]"));

            if (postText.isDisplayed()) {
                System.out.println("Status update is successfully posted on the user's timeline.");
            } else {
                System.out.println("Error: Status update is not posted on the user's timeline.");
            }
            logger.info("Post successfully displayed");
        } catch (Exception ex) {
            logger.info("Post was not displayed");
        }
        // Close the browser
        try {
            driver.quit();
            logger.info ("Web browser closed successfully");
        } catch (Exception ex){
            logger.info ("Something went wrong with closing web browser");
        }
    }
}

