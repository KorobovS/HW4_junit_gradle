import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class BonigarciaTest {

    WebDriver driver;
    WebDriverWait wait;
    final String BASE_URL = "https://bonigarcia.dev/selenium-webdriver-java/";

    @BeforeEach
    public void open() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(BASE_URL);
        wait = new WebDriverWait(driver, Duration.ofSeconds(1));
    }

    @AfterEach
    public void close() {
        driver.quit();
    }

    // Проверка, что загрузилось 6 Chapter
    @Test
    public void testAllChapter() {
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='card-body']"));

        Assertions.assertEquals(list.size(), 6);
    }

    // Проверка, что в определенном Chapter находится нужное количество ссылок
    @ParameterizedTest
    @CsvSource(value = {
            "3, 1, 8",
            "3, 2, 8",
            "3, 3, 5",
            "4, 1, 2",
            "4, 2, 1",
            "4, 3, 3"
    }, ignoreLeadingAndTrailingWhitespace = true)
    public void testChapterLink(int i, int j, int number) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='row'][" + i + "]/div[" + j + "]//a")));
        int size = driver.findElements(By.xpath("//div[@class='row'][" + i + "]/div[" + j + "]//a")).size();


        Assertions.assertEquals(size, number);
    }

    // Проверка каждой ссылки в каждом Chapter
    @Test()
    public void testChapterAllLink() {
        List<WebElement> webElementList = driver.findElements(By.xpath("//div[@class='card-body']/a"));

        for (WebElement element : webElementList) {
            String link = element.getDomAttribute("href");
            String title = element.getText();

            // Title кнопки не соответствует Title страницы
            switch (link) {
                case "navigation1.html" -> title = "Navigation example";
                case "draw-in-canvas.html" -> title = "Drawing in canvas";
                case "long-page.html" -> title = "This is a long page";
                case "iframes.html" -> title = "IFrame";
                case "login-slow.html" -> title = "Slow login form";
            }

            element.click();

            String resultUrl = driver.getCurrentUrl();

            //Страницы без Title
            if (link.equals("frames.html") || link.equals("multilanguage.html")) {
                Assertions.assertEquals(resultUrl, BASE_URL + link);
                driver.navigate().back();
                continue;
            }

            String resultTitle = driver.findElement(By.xpath("//h1[@class='display-6']")).getText();

            Assertions.assertEquals(resultUrl, BASE_URL + link);
            Assertions.assertEquals(resultTitle, title);

            driver.navigate().back();
        }
    }
}
