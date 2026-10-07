package ru.dmiaa.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import com.codeborne.selenide.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.HasCdp;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Test {
    @BeforeAll
    public void setup () {
        System.setProperty("parallel.core.max", "1");
        Configuration.browser = "chrome";
        Configuration.browserSize="1600x900x16";
        Configuration.browserPosition="1x1";
        Configuration.baseUrl = "https://google.com";
        Configuration.browserVersion = "154.0";
        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("screenResolution", Configuration.browserSize);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--start-maximized");

        Configuration.browserCapabilities =options;
        Configuration.browserCapabilities.setCapability("selenoid:options", selenoidOptions);
        Configuration.remote="http://localhost:3195/wd/hub";
    }

    @org.junit.jupiter.api.Test
    @Order(1)
    @Tag("smoke")
    public void simpleTest () {
        Selenide.open("https://google.com");
        $("textarea[aria-label='Найти']").sendKeys("filetype:txt");
        $("textarea[aria-label='Найти']").scrollTo().click();
        $("textarea[aria-label='Найти']").sendKeys(Keys.ENTER);
    }

    @org.junit.jupiter.api.Test
    @Order(2)
    @Tag("smoke")
    public void cdpTest() {
        Selenide.open("https://google.com");
        RemoteWebDriver driver = (RemoteWebDriver) WebDriverRunner.getWebDriver();

        //Включаем Network
        executeCdpViaHttp(driver, "Network.enable", Map.of());

        //пробуем менять размер
        executeCdpViaHttp(driver, "Emulation.setDeviceMetricsOverride", Map.of(
                "width", 1280,
                "height", 720,
                "deviceScaleFactor", 1,
                "mobile", false
        ));

        //пробуем блокировку шрифтов
        executeCdpViaHttp(driver, "Network.setBlockedURLs", Map.of(
                "urls", List.of("*.woff", "*.woff2", "*.ttf", "*.eot", "*type=font*")
        ));
    }

    private void executeCdpViaHttp(RemoteWebDriver driver, String cmd, Map<String, Object> params) {
        try {
            // Формируем параметры для команды executeCdpCommand
            Map<String, Object> payload = Map.of(
                    "cmd", cmd,
                    "params", params
            );

            // Обертываем в объект Command и отправляем через публичный executor драйвера
            Command command = new Command(driver.getSessionId(), "executeCdpCommand", payload);
            Response response = driver.getCommandExecutor().execute(command);

            if (response != null && response.getState() != null && !response.getState().equals("success")) {
                throw new RuntimeException("Ошибка выполнения CDP команды " + cmd + ": " + response.getValue());
            }
        } catch (Exception e) {
            throw new RuntimeException("Не удалось отправить CDP команду: " + cmd, e);
        }
    }
}
