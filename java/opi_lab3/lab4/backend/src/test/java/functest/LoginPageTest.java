package functest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class LoginPageTest {

    private WebDriver webDriver;

    @BeforeEach
    public void setup() {
        Path driverPath = Paths.get( "chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", driverPath.toAbsolutePath().toString());
        webDriver = new ChromeDriver();
        webDriver.get("http://localhost:4200/lab4/login");
    }
    @Test
    public void testNonExistentUser() throws InterruptedException {
        WebElement userNameField = webDriver.findElement(By.id("username"));
        WebElement passwordField = webDriver.findElement(By.id("password"));
        userNameField.sendKeys("userthatdoesntexist");
        passwordField.sendKeys("thisuserpassword");

        WebElement button = webDriver.findElement(By.cssSelector("button"));
        button.click();
        new WebDriverWait(webDriver,Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOfElementLocated(By.id("errorBlock")));
        WebElement errorElement = webDriver.findElement((By.id("errorBlock")));

        assertTrue(errorElement.isDisplayed());

        assertEquals("Invalid username or password",errorElement.getText());
    }

    @Test
    public void testExistingUser(){
        WebElement userNameField = webDriver.findElement(By.id("username"));
        WebElement passwordField = webDriver.findElement(By.id("password"));
        userNameField.sendKeys("1");
        passwordField.sendKeys("1");

        WebElement button = webDriver.findElement(By.cssSelector("button"));
        button.click();
        new WebDriverWait(webDriver, Duration.ofSeconds(10)).until(ExpectedConditions.urlContains("/main"));
        String url = webDriver.getCurrentUrl();
        assertEquals(url,"http://localhost:4200/lab4/main");
    }

    @AfterEach
    public void destroy(){
        webDriver.quit();
    }
}
