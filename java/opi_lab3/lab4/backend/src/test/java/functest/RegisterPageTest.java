package functest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegisterPageTest {
    private WebDriver webDriver;

    @BeforeEach
    public void setup() {
        Path driverPath = Paths.get( "chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", driverPath.toAbsolutePath().toString());
        webDriver = new ChromeDriver();
        webDriver.get("http://localhost:4200/lab4/register");
    }
    @Test
    public void registrationSuccess(){
        WebElement usernameElement = webDriver.findElement(By.id("username"));
        WebElement passwordElement = webDriver.findElement(By.id("password"));
        WebElement confirmPasswordElement = webDriver.findElement(By.id("confirmPassword"));
        Long t = System.nanoTime();
        String usernameNPassword = t.toString();
        usernameElement.sendKeys(usernameNPassword);
        passwordElement.sendKeys(usernameNPassword);
        confirmPasswordElement.sendKeys(usernameNPassword);

        WebElement button = webDriver.findElement(By.cssSelector("button"));
        button.click();
        new WebDriverWait(webDriver, Duration.ofSeconds(10)).until(ExpectedConditions.urlContains("/main"));
        String url = webDriver.getCurrentUrl();
        assertEquals(url,"http://localhost:4200/lab4/main");
    }

    @Test
    public void registrationFailPasswordsNotEqual(){
        WebElement usernameElement = webDriver.findElement(By.id("username"));
        WebElement passwordElement = webDriver.findElement(By.id("password"));
        WebElement confirmPasswordElement = webDriver.findElement(By.id("confirmPassword"));
        Long t = System.nanoTime()+1;
        String usernameNPassword = t.toString();
        usernameElement.sendKeys(usernameNPassword);
        passwordElement.sendKeys(usernameNPassword+"1");
        confirmPasswordElement.sendKeys(usernameNPassword);

        WebElement button = webDriver.findElement(By.cssSelector("button"));
        button.click();
        new WebDriverWait(webDriver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.className("error-message")));
        WebElement errorElement = webDriver.findElement((By.className("error-message")));

        assertTrue(errorElement.isDisplayed());
        assertEquals("Passwords must be equal",errorElement.getText());
    }
    @Test
    public void registrationFailUserIsRegistered(){
        WebElement usernameElement = webDriver.findElement(By.id("username"));
        WebElement passwordElement = webDriver.findElement(By.id("password"));
        WebElement confirmPasswordElement = webDriver.findElement(By.id("confirmPassword"));
        String usernameNPassword = "1";
        usernameElement.sendKeys(usernameNPassword);
        passwordElement.sendKeys(usernameNPassword);
        confirmPasswordElement.sendKeys(usernameNPassword);

        WebElement button = webDriver.findElement(By.cssSelector("button"));
        button.click();
        new WebDriverWait(webDriver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.className("error-message")));
        WebElement errorElement = webDriver.findElement((By.className("error-message")));

        assertTrue(errorElement.isDisplayed());
        assertEquals("Registration error",errorElement.getText());
    }

    @AfterEach
    public void destroy(){
        webDriver.quit();
    }
}
