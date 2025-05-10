package functest;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.interactions.Actions;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.time.Duration;
import java.util.List;

public class MainPageTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("http://localhost:4200/lab4/login");
        login();
    }

    private void login() {
        driver.findElement((By.id("username"))).sendKeys("1");
        driver.findElement(By.id("password")).sendKeys("1");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/main"));
    }

    @Test
    public void testFormSubmission() {
        selectDropdown("x", "1");
        setSlider("y", 3);
        selectDropdown("r", "1.5");

        driver.findElement(By.cssSelector(".submit-btn")).click();

        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("p-table")));

        Assertions.assertTrue(table.getText().contains("1"));
    }


    @Test
    public void testClearButton() {
        WebElement clearBtn = driver.findElement(By.cssSelector(".clear-btn"));
        clearBtn.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("tbody tr")));
        List<WebElement> rows = driver.findElements(By.cssSelector("p-table tbody tr"));

        assertEquals(0, rows.size());
    }

    private void selectDropdown(String id, String visibleText) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
        dropdown.click();

        List<WebElement> options = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("li[role='option']"))
        );

        for (WebElement option : options) {
            if (option.getText().trim().equals(visibleText)) {
                option.click();
                break;
            }
        }
    }


    private void setSlider(String id, int value) {
        WebElement slider = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));

        int min = -3, max = 5;
        int steps = max - min;
        int targetStep = value - min;

        int sliderWidth = slider.getSize().width;
        int offsetX = (sliderWidth / steps) * targetStep;

        Actions actions = new Actions(driver);
        actions.clickAndHold(slider).moveByOffset(offsetX - sliderWidth / 2, 0).release().perform();
    }


    @AfterEach
    public void destroy() {
        if (driver != null) driver.quit();
    }
}
